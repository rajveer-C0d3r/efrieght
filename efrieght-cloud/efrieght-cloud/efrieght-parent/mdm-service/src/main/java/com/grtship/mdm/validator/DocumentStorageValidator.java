package com.grtship.mdm.validator;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.grtship.mdm.exception.DocumentStorageException;

@Component
@PropertySource("classpath:content-type.properties")
public class DocumentStorageValidator {

	private static final String FILE_NOT_SUPPORTED = "File not supported.";

	@Value("${document.content_type}")
	private String contentType;

	public void validateFile(MultipartFile file) {
		List<String> allowedContentType = Arrays.asList(contentType.split(","));
		if (!allowedContentType.contains(file.getContentType()))
			throw new DocumentStorageException(FILE_NOT_SUPPORTED);
	}

}
