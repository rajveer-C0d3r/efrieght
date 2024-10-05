package com.grtship.notification.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.grtship.core.dto.NotificationDTO;
import com.grtship.core.enumeration.AdminURL;
import com.grtship.core.enumeration.ClientURL;
import com.grtship.core.enumeration.NotificationStatus;
import com.grtship.core.enumeration.NotificationType;
import com.grtship.core.enumeration.UserType;
import com.grtship.notification.config.MailConfig;

/**
 * Service for sending email's.
 * <p>
 * We use the {@link Async} annotation to send email's asynchronously.
 */
@Service
public class MailService {

	private static final String PERMISSION = "?permission=";

	private final Logger log = LoggerFactory.getLogger(MailService.class);

	private static final String USER = "user";

	private static final String BASE_URL = "baseUrl";

	private final MailConfig mailConfig;

	private final JavaMailSender javaMailSender;

	private final MessageSource messageSource;

	private final SpringTemplateEngine templateEngine;

	private static final String SUCCESS_MESSAGE = "Notification Sent Successfully";

	private final NotificationLogService notificationService;

	/**
	 * @param mailConfig
	 * @param javaMailSender
	 * @param messageSource
	 * @param templateEngine
	 */
	public MailService(MailConfig mailConfig, JavaMailSender javaMailSender, MessageSource messageSource,
			SpringTemplateEngine templateEngine, NotificationLogService notificationService) {

		this.mailConfig = mailConfig;
		this.javaMailSender = javaMailSender;
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
		this.notificationService = notificationService;
	}

	/**
	 * @param to
	 * @param subject
	 * @param content
	 * @param isMultipart
	 * @param isHtml
	 * @throws javax.mail.MessagingException
	 */
	public Boolean sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml,
			NotificationType notificationType) throws javax.mail.MessagingException {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart,
				isHtml, to, subject, content);
		Boolean status = Boolean.FALSE;
		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
			message.setTo(to);
			message.setFrom(mailConfig.getFrom());
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			notificationService.saveNotificationLog(to, SUCCESS_MESSAGE, notificationType, NotificationStatus.SUCCESS);
			log.debug("Sent email to User '{}'", to);
			status = Boolean.TRUE;
		} catch (MailException | MessagingException e) {
			log.warn("Email could not be sent to user '{}'", to, e);
			notificationService.saveNotificationLog(to, e.getMessage(), notificationType, NotificationStatus.FAILURE);
			status = Boolean.FALSE;
		}
		return status;
	}

	/**
	 * @param user
	 * @param templateName
	 * @param titleKey
	 * @throws javax.mail.MessagingException
	 */

	public Boolean sendEmailFromTemplate(NotificationDTO notificationDto, String templateName, String titleKey)
			throws javax.mail.MessagingException {
		if (notificationDto.getEmail() == null) {
			log.debug("Email doesn't exist for user '{}'", notificationDto.getLogin());
			return Boolean.TRUE;
		}
		Locale locale = extractedLocale(notificationDto);
		Context context = setLocaleInContext(locale);
		context.setVariable(USER, notificationDto);
		if (notificationDto.getUserType().equals(UserType.ADMIN)) {
			context.setVariable(BASE_URL, mailConfig.getBaseUrlAdmin());
		} else {
			context.setVariable(BASE_URL, mailConfig.getBaseUrlClient());
		}
		String content = templateEngine.process(templateName, context);
		String subject = messageSource.getMessage(titleKey, null, locale);
		return sendEmail(notificationDto.getEmail(), subject, content, false, true,
				notificationDto.getNotificationType());
	}

	/**
	 * @param user
	 * @throws javax.mail.MessagingException
	 */
	public Boolean sendActivationEmail(NotificationDTO notificationDto) throws javax.mail.MessagingException {
		log.debug("Sending activation email to '{}'", notificationDto.getEmail());
		log.debug("User obj in send Activate Mail {}", notificationDto);
		return sendEmailFromTemplate(notificationDto, "mail/activationEmail", "email.activation.title");
	}

	/**
	 * @param user
	 * @throws javax.mail.MessagingException
	 */
	public Boolean sendCreationEmail(NotificationDTO notificationDto) throws javax.mail.MessagingException {
		log.debug("Sending creation email to '{}'", notificationDto.getEmail());
		return sendEmailFromTemplate(notificationDto, "mail/creationEmail", "email.activation.title");
	}

	/**
	 * @param user
	 * @throws javax.mail.MessagingException
	 */
	public Boolean sendPasswordResetMail(NotificationDTO notificationDto) throws javax.mail.MessagingException {
		log.debug("Sending password reset email to '{}'", notificationDto.getEmail());
		return sendEmailFromTemplate(notificationDto, "mail/passwordResetEmail", "email.reset.title");
	}

	public Boolean sendApprovalRequestEmail(NotificationDTO notificationDto) throws javax.mail.MessagingException {
		log.debug("Sending Approval request email to '{}'", notificationDto.getEmail());
		return sendApprovalEmailFromTemplate(notificationDto, "mail/approvalEmail", "email.approval.title");
	}

	public Boolean sendApprovalEmailFromTemplate(NotificationDTO notificationDto, String templateName, String titleKey)
			throws javax.mail.MessagingException {
		if (notificationDto.getEmail() == null) {
			log.debug("Email doesn't exist for user '{}'", notificationDto.getLogin());
			return Boolean.TRUE;
		}
		Locale locale = extractedLocale(notificationDto);
		Context context = setLocaleInContext(locale);
		context.setVariable(USER, notificationDto);
		setUrlAsPerUser(notificationDto, context, getUrl(notificationDto));
		return sendEmail(notificationDto.getEmail(), setSubject(titleKey, locale), setContent(templateName, context), false, true,
				notificationDto.getNotificationType());
	}

	private Context setLocaleInContext(Locale locale) {
		return new Context(locale);
	}

	private Locale extractedLocale(NotificationDTO notificationDto) {
		return Locale.forLanguageTag(notificationDto.getLangKey());
	}

	private String getUrl(NotificationDTO notificationDto) {
		if (notificationDto.getUserType().equals(UserType.CLIENT)) {
			return ClientURL.valueOf(notificationDto.getModuleName()).getLabel() + notificationDto.getReferenceId()
					+ PERMISSION + notificationDto.getRoleId();
		} else {
			return AdminURL.valueOf(notificationDto.getModuleName()).getLabel() + notificationDto.getReferenceId()
					+ PERMISSION + notificationDto.getRoleId();
		}
	}

	private void setUrlAsPerUser(NotificationDTO notificationDto, Context context, String url) {
		if (notificationDto.getUserType().equals(UserType.ADMIN)) {
			setAdminPortalUrl(context, url);
		} else {
			setClientPortalUrl(context, url);
		}
	}

	private void setClientPortalUrl(Context context, String url) {
		context.setVariable(BASE_URL, mailConfig.getBaseUrlClient() + "/" + url);
	}

	private void setAdminPortalUrl(Context context, String url) {
		context.setVariable(BASE_URL, mailConfig.getBaseUrlAdmin() + "/" + url);
	}

	private String setContent(String templateName, Context context) {
		return templateEngine.process(templateName, context);
	}

	private String setSubject(String titleKey, Locale locale) {
		return messageSource.getMessage(titleKey, null, locale);
	}
}
