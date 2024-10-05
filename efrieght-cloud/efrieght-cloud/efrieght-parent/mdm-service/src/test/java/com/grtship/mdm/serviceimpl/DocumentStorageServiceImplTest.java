package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.DocumentStorageDTO;
import com.grtship.mdm.config.DocumentStorageConfig;
import com.grtship.mdm.exception.DocumentStorageException;
import com.grtship.mdm.mapper.DocumentStorageMapper;
import com.grtship.mdm.repository.DocumentRepository;
import com.grtship.mdm.repository.DocumentStorageRepository;
import com.grtship.mdm.service.DocumentService;
import com.grtship.mdm.service.DocumentStorageService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class DocumentStorageServiceImplTest {

	@Autowired DocumentStorageRepository documentStorageRepository;
	@Autowired DocumentStorageService documentStorageService;
	@Autowired DocumentStorageMapper documentStrorageMapper;
	@Autowired DocumentStorageConfig documentStorageconfig ;
	@Autowired DocumentRepository documentRepository;
	@Autowired DocumentService documentService;
	
	private DocumentStorageDTO documentStorageDTO;
	private DocumentDTO documentDTO;

	public static DocumentStorageDTO createDocumentStorage() {
		DocumentStorageDTO documentStorage = DocumentStorageDTO.builder().fileType("pdf").fileStorage("accountInfo-1-1-1-Doo1-202010123344")
				.originalFileName("accountInfo").storageLocation("/tmp/grt/files/upload/112020-09-29T16:46:55.939Z.pdf")
				.build();
		return documentStorage;
	}

	@BeforeEach
	public void init() {
		documentDTO=DocumentServiceImplTest.prepareDocumentDto();
		documentStorageDTO = createDocumentStorage();
	}

	@Test
	void testSave() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		prepareDocumentStorageDtoAsRequired(savedDocument);
		DocumentStorageDTO savedDocumentStorage = documentStorageService.save(documentStorageDTO);
		assertThat(savedDocumentStorage).isNotNull();
		assertThat(savedDocumentStorage.getId()).isNotNull();
		assertThat(savedDocumentStorage.getReferenceId()).isEqualTo(documentStorageDTO.getReferenceId());
		assertThat(savedDocumentStorage.getReferenceName()).isEqualTo(documentStorageDTO.getReferenceName());
		assertThat(savedDocumentStorage.getDocumentDefinitionId())
				.isEqualTo(documentStorageDTO.getDocumentDefinitionId());
		assertThat(savedDocumentStorage.getClientId()).isEqualTo(documentStorageDTO.getClientId());
		assertThat(savedDocumentStorage.getCompanyId()).isEqualTo(documentStorageDTO.getCompanyId());
		assertThat(savedDocumentStorage.getFileStorage()).isEqualTo(documentStorageDTO.getFileStorage());
		assertThat(savedDocumentStorage.getOriginalFileName()).isEqualTo(documentStorageDTO.getOriginalFileName());
		assertThat(savedDocumentStorage.getStorageLocation()).isEqualTo(documentStorageDTO.getStorageLocation());
	}

	private void prepareDocumentStorageDtoAsRequired(DocumentDTO savedDocument) {
		documentStorageDTO.setReferenceId(savedDocument.getReferenceId());
		documentStorageDTO.setReferenceName(savedDocument.getReferenceName());
		documentStorageDTO.setDocumentDefinitionId(savedDocument.getId());
		documentStorageDTO.setCompanyId(savedDocument.getCompanyId());
		documentStorageDTO.setClientId(savedDocument.getClientId());
		documentStorageDTO.setDocumentCode(savedDocument.getCode());
		documentStorageDTO.setDocumentName(savedDocument.getName());
	}

	@Test
	void testStoreFile() throws IOException {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		prepareDocumentStorageDtoAsRequired(savedDocument);
		MultipartFile multipartFile = prepareFile();
		DocumentStorageDTO storeFile = documentStorageService.storeFile(multipartFile, documentStorageDTO);
		assertThat(storeFile).isNotNull();
		assertThat(storeFile.getDocumentDefinitionId()).isEqualTo(documentStorageDTO.getDocumentDefinitionId());
		assertThat(storeFile.getReferenceId()).isEqualTo(documentStorageDTO.getReferenceId());
		assertThat(storeFile.getOriginalFileName()).isEqualTo(multipartFile.getOriginalFilename());
		assertThat(storeFile.getFileType()).isEqualTo(multipartFile.getContentType());
		assertThat(storeFile.getOriginalFileName()).isEqualTo(multipartFile.getOriginalFilename());
		assertThat(storeFile.getFileStorage()).isNotNull();
		assertThat(storeFile.getStorageLocation()).isNotNull();
	}

	private MultipartFile prepareFile() throws FileNotFoundException, IOException {
		File file = new File("C:\\Users\\jazzc\\Downloads\\lifecycle.pdf");
		FileInputStream input = new FileInputStream(file);
		MultipartFile multipartFile = new MockMultipartFile("file",
		            file.getName(), "pdf", IOUtils.toByteArray(input));
		return multipartFile;
	}
	
	@Test
	void testStoreFileForInvalidDocumentId() throws IOException {
		documentStorageDTO.setDocumentDefinitionId(0L);
		MultipartFile multipartFile = prepareFile();
		assertThrows(DocumentStorageException.class, () -> {
			documentStorageService.storeFile(multipartFile, documentStorageDTO);
		});
	}
	
	

	@Test
	void testLoadFileAsResource() throws IOException {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		prepareDocumentStorageDtoAsRequired(savedDocument);
		MultipartFile multipartFile = prepareFile();
		DocumentStorageDTO storeFile = documentStorageService.storeFile(multipartFile, documentStorageDTO);
		Resource loadFileAsResource = documentStorageService.loadFileAsResource(storeFile.getStorageLocation());
		assertTrue(loadFileAsResource.exists());
		assertThat(loadFileAsResource.getURI()).isNotNull();
	}
	
	@Test
	void testLoadFileAsResourceForInvalidLocation() throws IOException {
		assertThrows(FileNotFoundException.class, () -> {
			documentStorageService.loadFileAsResource("ERRORERROR");
		});
	}

	@Test
	void testGetOriginalFileName() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		prepareDocumentStorageDtoAsRequired(savedDocument);
		DocumentStorageDTO savedDocumentStorage = documentStorageService.save(documentStorageDTO);
		String originalFileName = documentStorageService.getOriginalFileName(savedDocumentStorage.getFileStorage());
		assertThat(originalFileName).isNotNull();
		assertThat(originalFileName).isEqualTo(savedDocumentStorage.getOriginalFileName());
	}
	
	@Test
	void testGetOriginalFileNameForInvalidFileName() {
		String originalFileName = documentStorageService.getOriginalFileName("ERRORERROR");
		assertThat(originalFileName).isNull();
	}

	@Test
	void testGetByReferenceIdsAndName() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		prepareDocumentStorageDtoAsRequired(savedDocument);
		DocumentStorageDTO savedDocumentStorage = documentStorageService.save(documentStorageDTO);
		List<Long> referenceIds = new ArrayList<>();
		referenceIds.add(savedDocumentStorage.getReferenceId());
		referenceIds.add(savedDocumentStorage.getReferenceId());
		List<DocumentStorageDTO> documentStorages = documentStorageService.getByReferenceIdsAndName(referenceIds,
				savedDocumentStorage.getReferenceName());
		assertThat(documentStorages).isNotEmpty();
		assertThat(documentStorages).allMatch(document -> document.getId() != null);
		assertThat(documentStorages).allMatch(document -> document.getOriginalFileName() != null);
		assertThat(documentStorages).allMatch(document -> document.getStorageLocation() != null);
		assertThat(documentStorages).allMatch(document -> document.getFileStorage() != null);
		assertThat(documentStorages)
				.allMatch(document -> document.getReferenceId().equals(savedDocumentStorage.getReferenceId()));
		assertThat(documentStorages)
				.allMatch(document -> document.getReferenceName().equals(savedDocumentStorage.getReferenceName()));
	}
	
	@Test
	void testGetByReferenceIdsAndNameForInvalidId() {
		List<Long> referenceIds = new ArrayList<>();
		referenceIds.add(0l);
		List<DocumentStorageDTO> documentStorages = documentStorageService.getByReferenceIdsAndName(referenceIds,
				documentDTO.getReferenceName());
		assertThat(documentStorages).isEmpty();
	}
	
	@Test
	void testGetByReferenceIdsAndNameForInvalidName() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		prepareDocumentStorageDtoAsRequired(savedDocument);
		DocumentStorageDTO savedDocumentStorage = documentStorageService.save(documentStorageDTO);
		List<Long> referenceIds = new ArrayList<>();
		referenceIds.add(savedDocumentStorage.getReferenceId());
		List<DocumentStorageDTO> documentStorages = documentStorageService.getByReferenceIdsAndName(referenceIds,
				"ERRORERRRO");
		assertThat(documentStorages).isEmpty();
	}

}
