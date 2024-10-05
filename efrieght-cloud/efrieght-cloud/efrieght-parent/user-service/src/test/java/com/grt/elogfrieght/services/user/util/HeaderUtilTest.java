package com.grt.elogfrieght.services.user.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class HeaderUtilTest {

	
	private static final String APPLICATION_NAME="Grt Logistics";
	private static final String MESSAGE="Successfully Saved";
	private static final String PARAM="clientId";
	private static final String ENTITY_NAME="Company";
	private static final String UNPROCESSABLE_ENTITY="Unprocessable Entity";

	@Test
	void testCreateAlert() {
		HttpHeaders headers=HeaderUtil.createAlert(APPLICATION_NAME,MESSAGE,PARAM);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-alert").get(0)).isEqualTo(MESSAGE);
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(PARAM);
	}

	@Test
	void testCreateEntityCreationAlertIfEnableTranslationTrue() {
		HttpHeaders headers = HeaderUtil.createEntityCreationAlert(APPLICATION_NAME, true, ENTITY_NAME, PARAM);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-alert").get(0))
				.isEqualTo(APPLICATION_NAME + "." + ENTITY_NAME + ".created");
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(PARAM);
	}
	
	@Test
	void testCreateEntityCreationAlertIfEnableTranslationFalse() {
		HttpHeaders headers = HeaderUtil.createEntityCreationAlert(APPLICATION_NAME, false, ENTITY_NAME, PARAM);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-alert").get(0))
				.isEqualTo("A new " + ENTITY_NAME + " is created with identifier " + PARAM);
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(PARAM);
	}
	
	@Test
	void testCreateEntityUpdateAlertIfEnableTranslationTrue() {
		HttpHeaders headers = HeaderUtil.createEntityUpdateAlert(APPLICATION_NAME, true, ENTITY_NAME, PARAM);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-alert").get(0))
				.isEqualTo(APPLICATION_NAME + "." + ENTITY_NAME + ".updated");
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(PARAM);
	}

	@Test
	void testCreateEntityUpdateAlertIfEnableTranslationFalse() {
		HttpHeaders headers = HeaderUtil.createEntityUpdateAlert(APPLICATION_NAME, false, ENTITY_NAME, PARAM);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-alert").get(0))
				.isEqualTo("A " + ENTITY_NAME + " is updated with identifier " + PARAM);
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(PARAM);
	}

	@Test
	void testCreateEntityDeletionAlertIfEnableTransalationTrue() {
		HttpHeaders headers = HeaderUtil.createEntityDeletionAlert(APPLICATION_NAME, true, ENTITY_NAME, PARAM);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-alert").get(0))
				.isEqualTo(APPLICATION_NAME + "." + ENTITY_NAME + ".deleted");
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(PARAM);
	}

	@Test
	void testCreateEntityDeletionAlertIfEnableTransalationFalse() {
		HttpHeaders headers = HeaderUtil.createEntityDeletionAlert(APPLICATION_NAME, false, ENTITY_NAME, PARAM);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-alert").get(0))
				.isEqualTo("A " + ENTITY_NAME + " is deleted with identifier " + PARAM);
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(PARAM);
	}

	@Test
	void testCreateFailureAlertIfEnableTranslationTrue() {
		HttpHeaders headers = HeaderUtil.createFailureAlert(APPLICATION_NAME, true, ENTITY_NAME, UNPROCESSABLE_ENTITY,
				MESSAGE);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-error").get(0)).isEqualTo("error." + UNPROCESSABLE_ENTITY);
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(ENTITY_NAME);
	}
	
	@Test
	void testCreateFailureAlertIfEnableTranslationFalse() {
		HttpHeaders headers = HeaderUtil.createFailureAlert(APPLICATION_NAME, false, ENTITY_NAME, UNPROCESSABLE_ENTITY,
				MESSAGE);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("X-" + APPLICATION_NAME + "-error").get(0)).isEqualTo(MESSAGE);
		assertThat(headers.get("X-" + APPLICATION_NAME + "-params").get(0)).isEqualTo(ENTITY_NAME);
	}
}
