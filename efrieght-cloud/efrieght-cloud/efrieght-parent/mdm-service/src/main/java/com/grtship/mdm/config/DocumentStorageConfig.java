package com.grtship.mdm.config;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

//@ConfigurationProperties(prefix = "file")
@Data
@Component
public class DocumentStorageConfig {
	@Value("${file.upload-dir}")
	 private String uploadDir;
}
