package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.DocumentDownloadDTO;
import com.grtship.mdm.criteria.DocumentCriteria;
import com.grtship.mdm.domain.DocumentStorage;
import com.grtship.mdm.repository.DocumentStorageRepository;
import com.grtship.mdm.service.DocumentService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class DocumentQueryServiceImplTest {
	
	private static final String COUNTRY = "Country";
	@Autowired
	private DocumentService documentService;
	@Autowired
	private DocumentQueryServiceImpl documentQueryServiceImpl;
	@Autowired
	private DocumentStorageRepository documentStorageRepository;

	private DocumentDTO documentDto;
	private DocumentCriteria criteria;

	@BeforeEach
	void setUp() {
		documentDto = DocumentServiceImplTest.prepareDocumentDto();
	}

	@Test
	void testFindAllByCriteriaId() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		criteria = new DocumentCriteria();
		criteria.setId(savedDocument.getId());
		List<DocumentDTO> documents = documentQueryServiceImpl.findAll(criteria);
		assertThat(documents).isNotEmpty();
		assertThat(documents).allMatch(document -> document.getId().equals(criteria.getId()));
		assertThat(documents).allMatch(document -> document.getCode() != null);
		assertThat(documents).allMatch(document -> document.getName() != null);
		assertThat(documents).allMatch(document -> document.getType() != null);
	}
	
	@Test
	void testFindAllByCriteriaIdForInvalidId() {
		criteria = new DocumentCriteria();
		criteria.setId(0L);
		List<DocumentDTO> documents = documentQueryServiceImpl.findAll(criteria);
		assertThat(documents).isEmpty();
	}
	
	@Test
	void testFindAllByCriteriaReferenceId() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		criteria = new DocumentCriteria();
		criteria.setReferenceId(savedDocument.getReferenceId());
		List<DocumentDTO> documents = documentQueryServiceImpl.findAll(criteria);
		assertThat(documents).isNotEmpty();
		assertThat(documents).allMatch(document -> document.getId() != null);
		assertThat(documents).allMatch(document -> document.getCode() != null);
		assertThat(documents).allMatch(document -> document.getName() != null);
		assertThat(documents).allMatch(document -> document.getType() != null);
		assertThat(documents).allMatch(document -> document.getReferenceId().equals(criteria.getReferenceId()));
	}
	
	@Test
	void testFindAllByCriteriaIdForInvalidReferenceId() {
		criteria = new DocumentCriteria();
		criteria.setReferenceId(0L);
		List<DocumentDTO> documents = documentQueryServiceImpl.findAll(criteria);
		assertThat(documents).isEmpty();
	}
	
	@Test
	void testFindAllByCriteriaReferenceName() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		criteria = new DocumentCriteria();
		criteria.setReferenceName(savedDocument.getReferenceName());
		List<DocumentDTO> documents = documentQueryServiceImpl.findAll(criteria);
		assertThat(documents).isNotEmpty();
		assertThat(documents).allMatch(document -> document.getId()!=null);
		assertThat(documents).allMatch(document -> document.getCode() != null);
		assertThat(documents).allMatch(document -> document.getName() != null);
		assertThat(documents).allMatch(document -> document.getType() != null);
		assertThat(documents).allMatch(document -> document.getReferenceName().equals(criteria.getReferenceName()));
	}
	
	@Test
	void testFindAllByCriteriaIdForInvalidReferenceName() {
		criteria = new DocumentCriteria();
		criteria.setReferenceName("COUNTRYCOUNTRY");
		List<DocumentDTO> documents = documentQueryServiceImpl.findAll(criteria);
		assertThat(documents).isEmpty();
	}
	
	@Test
	void testFindAllByCriteriaType() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		criteria = new DocumentCriteria();
		criteria.setDocumentType(savedDocument.getType());
		List<DocumentDTO> documents = documentQueryServiceImpl.findAll(criteria);
		assertThat(documents).isNotEmpty();
		assertThat(documents).allMatch(document -> document.getId() != null);
		assertThat(documents).allMatch(document -> document.getCode() != null);
		assertThat(documents).allMatch(document -> document.getName() != null);
		assertThat(documents).allMatch(document -> document.getType().equals(criteria.getDocumentType()));
	}

	@Test
	void testGetDocumentIdByCode() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		Long documentIdByCode = documentQueryServiceImpl.getDocumentIdByCode(savedDocument.getCode());
		assertThat(documentIdByCode).isNotNull();
	}
	
	@Test
	void testGetDocumentIdByCodeForInvalidCode() {
		Long documentIdByCode = documentQueryServiceImpl.getDocumentIdByCode("COUNTRYCOUNTRY");
		assertThat(documentIdByCode).isNull();
	}

	@Test
	void testGetIdByReferenceIdAndReferenceName() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		Set<Long> id = documentQueryServiceImpl.getIdByReferenceIdAndReferenceName(savedDocument.getReferenceId(),
				savedDocument.getReferenceName());
		assertThat(id).isNotEmpty();
		assertTrue(id.contains(savedDocument.getId()));
	}
	
	@Test
	void testGetIdByReferenceIdAndReferenceNameForInvalidId() {
		Set<Long> id = documentQueryServiceImpl.getIdByReferenceIdAndReferenceName(0L, COUNTRY);
		assertThat(id).isEmpty();
	}
	
	@Test
	void testGetIdByReferenceIdAndReferenceNameForInvalidName() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		Set<Long> id = documentQueryServiceImpl.getIdByReferenceIdAndReferenceName(savedDocument.getReferenceId(),
				"COUNTRYCOUNTRY");
		assertThat(id).isEmpty();
	}

	@Test
	void testFindOne() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		Optional<DocumentDTO> getDocumentById = documentQueryServiceImpl.findOne(savedDocument.getId());
		assertTrue(getDocumentById.isPresent());
		assertThat(savedDocument.getId()).isNotNull();
		assertThat(savedDocument.getId()).isEqualTo(getDocumentById.get().getId());
		assertThat(savedDocument.getCode()).isEqualTo(getDocumentById.get().getCode());
		assertThat(savedDocument.getName()).isEqualTo(getDocumentById.get().getName());
		assertThat(savedDocument.getIsMandatory()).isEqualTo(getDocumentById.get().getIsMandatory());
		assertThat(savedDocument.getType()).isEqualTo(getDocumentById.get().getType());
		assertThat(savedDocument.getReferenceName()).isEqualTo(getDocumentById.get().getReferenceName());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<DocumentDTO> getDocumentById = documentQueryServiceImpl.findOne(0L);
		assertFalse(getDocumentById.isPresent());
	}

	@Test
	void testGetDocumentsOnEdit() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		prepareAndSaveDocumentStorage(savedDocument);	    
		Set<DocumentDownloadDTO> documentsOnEdit = documentQueryServiceImpl
				.getDocumentsOnEdit(savedDocument.getReferenceId(), savedDocument.getReferenceName());
		assertThat(documentsOnEdit).isNotEmpty();
		assertThat(documentsOnEdit).allMatch(document -> document.getId() != null);
		assertThat(documentsOnEdit).allMatch(document -> document.getFileName() != null);
		assertThat(documentsOnEdit).allMatch(document -> document.getOriginalFileName() != null);
		assertThat(documentsOnEdit)
				.allMatch(document -> document.getReferenceId().equals(savedDocument.getReferenceId()));
		assertThat(documentsOnEdit)
				.allMatch(document -> document.getReferenceName().equals(savedDocument.getReferenceName()));
	}

	private void prepareAndSaveDocumentStorage(DocumentDTO savedDocument) {
		DocumentStorage documentStorage = DocumentStorage.builder().documentDefinitionId(1L).fileStorage("https://")
				.referenceId(savedDocument.getReferenceId()).referenceName(savedDocument.getReferenceName())
				.originalFileName("Aadhar Card").fileType("PDF").storageLocation("https://storage Location").build();
        documentStorageRepository.save(documentStorage);
	}
	
	@Test
	void testGetDocumentsOnEditForInvalidId() {
		Set<DocumentDownloadDTO> documentsOnEdit = documentQueryServiceImpl
				.getDocumentsOnEdit(0L, COUNTRY);
		assertThat(documentsOnEdit).isEmpty();
	}
	
	@Test
	void testGetDocumentsOnEditForInvalidName() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		Set<DocumentDownloadDTO> documentsOnEdit = documentQueryServiceImpl.getDocumentsOnEdit(savedDocument.getId(),
				"COUNTRYCOUNTRY");
		assertThat(documentsOnEdit).isEmpty();
	}

}
