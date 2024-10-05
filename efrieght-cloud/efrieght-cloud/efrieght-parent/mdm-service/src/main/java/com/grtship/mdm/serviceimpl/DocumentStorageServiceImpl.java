package com.grtship.mdm.serviceimpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.DocumentStorageDTO;
import com.grtship.mdm.config.DocumentStorageConfig;
import com.grtship.mdm.domain.DocumentStorage;
import com.grtship.mdm.exception.DocumentStorageException;
import com.grtship.mdm.mapper.DocumentStorageMapper;
import com.grtship.mdm.repository.DocumentStorageRepository;
import com.grtship.mdm.service.DocumentStorageService;

/**
 * Service Implementation for managing {@link DocumentStorage}.
 */
@Service
@Transactional
public class DocumentStorageServiceImpl implements DocumentStorageService {

	private static final String STRING = "";

	private static final String FILE_NOT_FOUND = "File not found ";

	private static final String DOCUMENT_NOT_PRESENT_FOR_GIVEN_DOCUMENT_CODE_AND_DOCUMENT_NAME = "Document not present for given Document Code and Document Name.";

	private static final String COULD_NOT_CREATE_THE_DIRECTORY_WHERE_THE_UPLOADED_FILES_WILL_BE_STORED = "Could not create the directory where the uploaded files will be stored.";

	private final Logger log = LoggerFactory.getLogger(DocumentStorageServiceImpl.class);

	private final Path fileStorageLocation;

	public final SimpleDateFormat dateTimeFormate = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	private DocumentStorageRepository documentStorageRepository;

	@Autowired
	private DocumentStorageMapper documentStorageMapper;

	@Autowired
	private DocumentQueryServiceImpl documentService;

	@Value("${document.content_type}")
	private String contentType;

	@Autowired
	public DocumentStorageServiceImpl(DocumentStorageConfig documentStorage) {
		this.fileStorageLocation = Paths.get(documentStorage.getUploadDir()).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new DocumentStorageException(COULD_NOT_CREATE_THE_DIRECTORY_WHERE_THE_UPLOADED_FILES_WILL_BE_STORED,
					ex);
		}
	}

	/**
	 * save document properties..
	 * 
	 **/
	@Override
	public DocumentStorageDTO save(DocumentStorageDTO documentStorageDTO) {
		log.debug("Request to save DocumentStorage : {}", documentStorageDTO);
		DocumentStorage documentStorage = documentStorageMapper.toEntity(documentStorageDTO);
		if(documentStorageDTO.getId()!=null) {
			documentStorage.setId(documentStorageDTO.getId());
		}
		documentStorage = documentStorageRepository.save(documentStorage);
		return documentStorageMapper.toDto(documentStorage);
	}

	/***
	 * storeFile is used to upload file to directory and call save function to save
	 * document related properties to database
	 **/
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.CREDIT_TERMS)
	@Override
	public DocumentStorageDTO storeFile(MultipartFile file, DocumentStorageDTO documentStorageDto) throws IOException {

		Long documentDefinitionId = documentService.getDocumentIdByCode(documentStorageDto.getDocumentCode());
		if (documentDefinitionId == null)
			throw new DocumentStorageException(DOCUMENT_NOT_PRESENT_FOR_GIVEN_DOCUMENT_CODE_AND_DOCUMENT_NAME);

		documentStorageDto.setDocumentDefinitionId(documentDefinitionId);

		String fileType = file.getContentType();
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		String fileStorageName = prepareFileStorageName(documentStorageDto);
		Path targetLocation = this.fileStorageLocation.resolve(fileStorageName);
		Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

		documentStorageDto.setDocumentDefinitionId(documentDefinitionId);
		documentStorageDto.setFileType(fileType);
		documentStorageDto.setOriginalFileName(originalFileName);
		documentStorageDto.setFileStorage(fileStorageName);
		documentStorageDto.setStorageLocation(targetLocation.toString());

		return save(documentStorageDto);
	}

	/***
	 * prepareFileStorageName will prepare fileStorage name...
	 **/
	private String prepareFileStorageName(DocumentStorageDTO documentStorageDto) {
		String fileStorageName = STRING;
		String date = formatDateTime(new Date());
		fileStorageName = fileStorageName + documentStorageDto.getClientId() + "-" + documentStorageDto.getCompanyId() + "-"
				+ documentStorageDto.getDocumentDefinitionId() + "-" + documentStorageDto.getDocumentCode() + "-"
				+ date;

		return fileStorageName;
	}

	/**
	 * formatDateTime is used to convert date into yyyyddmmhhmmss.... *
	 **/
	private String formatDateTime(Date date) {
		if (date == null) {
			return "";
		} else {
			return dateTimeFormate.format(date);
		}
	}

	/***
	 * getOriginalFileName will get original file name by storage file name..
	 * 
	 * @throws MalformedURLException
	 * @throws FileNotFoundException 
	 **/
	public Resource loadFileAsResource(String fileName) throws MalformedURLException, FileNotFoundException {
		Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
		Resource resource = new UrlResource(filePath.toUri());
		if (resource.exists()) {
			return resource;
		} else {
			throw new FileNotFoundException(FILE_NOT_FOUND + fileName);
		}
	}

	/**
	 * getOriginalFileName will get original file name by storage file name..
	 **/
	@Override
	public String getOriginalFileName(String fileName) {
		return documentStorageRepository.findOriginalFileNameByFileStorage(fileName);
	}

	@Override
	public List<DocumentStorageDTO> getByReferenceIdsAndName(List<Long> referenceIds, String referenceName) {
		if (CollectionUtils.isEmpty(referenceIds)) {
			return Collections.emptyList();
		}
		List<DocumentStorageDTO> documents = new ArrayList<>();
		documentStorageRepository.findByReferenceNameAndReferenceIdIn(referenceName, referenceIds)
				.ifPresent(obj -> documents.addAll(documentStorageMapper.toDto(obj)));
		return documents;
	}

}
