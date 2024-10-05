package com.grtship.mdm.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.grtship.core.dto.DocumentStorageDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.domain.DocumentStorage}.
 */
public interface DocumentStorageService {

	/**
	 * Save a documentStorage.
	 *
	 * @param documentStorageDTO the entity to save.
	 * @return the persisted entity.
	 */
	DocumentStorageDTO save(DocumentStorageDTO documentStorageDTO);

	List<DocumentStorageDTO> getByReferenceIdsAndName(List<Long> referenceIds, String referenceName);

	Resource loadFileAsResource(String fileName) throws MalformedURLException, FileNotFoundException;

	String getOriginalFileName(String fileName);

	DocumentStorageDTO storeFile(MultipartFile file, DocumentStorageDTO documentStorageDto) throws IOException;

}
