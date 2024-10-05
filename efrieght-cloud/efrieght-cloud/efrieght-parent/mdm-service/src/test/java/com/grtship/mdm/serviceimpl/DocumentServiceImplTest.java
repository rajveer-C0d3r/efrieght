package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.enumeration.DocumentType;
import com.grtship.mdm.domain.Document;
import com.grtship.mdm.mapper.DocumentMapper;
import com.grtship.mdm.repository.DocumentRepository;
import com.grtship.mdm.service.DocumentService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class DocumentServiceImplTest {

	private static final String COUNTRY = "Country";
	@Autowired
	private DocumentService documentService;
	@Autowired
	private DocumentMapper documentMapper;
	@Autowired
	private DocumentRepository documentRepository;

	private DocumentDTO documentDto;

	public static DocumentDTO prepareDocumentDto() {
		DocumentDTO documentDTO = new DocumentDTO();
		documentDTO.setCode("PAN");
		documentDTO.setName("Pan Card");
		documentDTO.setType(DocumentType.OPERATIONAL);
		documentDTO.setIsMandatory(Boolean.TRUE);
		documentDTO.setClientId(1L);
		documentDTO.setCompanyId(1L);
		documentDTO.setReferenceName(COUNTRY);
		documentDTO.setReferenceId(1L);
		return documentDTO;
	}

	@BeforeEach
	void setUp() {
		documentDto = prepareDocumentDto();
	}

	@Test
	void testSave() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		assertThat(savedDocument.getId()).isNotNull();
		assertThat(savedDocument.getCode()).isEqualTo(documentDto.getCode());
		assertThat(savedDocument.getName()).isEqualTo(documentDto.getName());
		assertThat(savedDocument.getIsMandatory()).isEqualTo(documentDto.getIsMandatory());
		assertThat(savedDocument.getType()).isEqualTo(documentDto.getType());
		assertThat(savedDocument.getReferenceName()).isEqualTo(documentDto.getReferenceName());
	}

	@Test
	void checkNameIsUnique() {
		documentService.save(documentDto);
		assertThrows(ValidationException.class, () -> {
			documentService.save(documentDto);
		});
	}

	@Test
	void checkCodeIsUnique() {
		documentService.save(documentDto);
		documentDto.setName("Test Code Unique");
		assertThrows(ValidationException.class, () -> {
			documentService.save(documentDto);
		});
	}

//	@Test
//	void testUpdate() {
//		DocumentDTO savedDocument = documentService.save(documentDto);
//		System.out.println("Saved Document"+savedDocument);
//		documentDto.setCode("UPD");
//		documentDto.setName("Test Document Update");
//		documentDto.setType(DocumentType.REGISTRATION);
//		documentDto.setId(savedDocument.getId());
//		DocumentDTO updatedDocument = documentService.save(documentDto);
//		System.out.println("Saved Document"+updatedDocument);
//		assertThat(savedDocument.getId()).isEqualTo(updatedDocument.getId());
//		assertThat(updatedDocument.getCode()).isNotEqualTo(documentDto.getCode());
//		assertThat(updatedDocument.getName()).isNotEqualTo(documentDto.getName());
//		assertThat(updatedDocument.getType()).isNotEqualTo(documentDto.getType());
//		assertThat(savedDocument.getIsMandatory()).isEqualTo(updatedDocument.getIsMandatory());
//		assertThat(savedDocument.getType()).isEqualTo(updatedDocument.getType());
//	}

	@Test
	void testDelete() {
		DocumentDTO savedDocument = documentService.save(documentDto);
		documentService.delete(savedDocument.getId());
		Optional<Document> findById = documentRepository.findById(savedDocument.getId());
		assertFalse(findById.isPresent());
	}

	@Test
	void testSaveAll() {
		Set<Document> documents = prepareDocuments();
		List<Document> savedDocuments = documentService.saveAll(documents);
		assertThat(savedDocuments).isNotEmpty();
		assertThat(savedDocuments).hasSize(documents.size());
		assertThat(savedDocuments).allMatch(document -> document.getId() != null);
		assertThat(savedDocuments).allMatch(document -> document.getName() != null);
		assertThat(savedDocuments).allMatch(document -> document.getCode() != null);
		assertThat(savedDocuments).allMatch(document -> document.getType() != null);
	}

	private Set<Document> prepareDocuments() {
		documentDto.setReferenceId(1L);
		Document document1 = documentMapper.toEntity(documentService.save(documentDto));
		documentDto.setCode("AADHAR");
		documentDto.setName("Aadhar Card");
		documentDto.setIsMandatory(Boolean.FALSE);
		documentDto.setReferenceId(2L);
		Document document2 = documentMapper.toEntity(documentService.save(documentDto));
		Set<Document> documents = new HashSet<>();
		documents.add(document1);
		documents.add(document2);
		return documents;
	}

	@Test
	void testGetMapOfDocumentsByReferenceIdListAndReferenceName() {
		Set<Document> documents = prepareDocuments();
		List<Document> savedDocuments = documentService.saveAll(documents);
		List<Long> referenceIds = savedDocuments.stream().map(Document::getReferenceId).collect(Collectors.toList());
		Map<Long, Set<DocumentDTO>> mapOfDocuments = documentService
				.getMapOfDocumentsByReferenceIdListAndReferenceName(referenceIds, COUNTRY);
		assertThat(mapOfDocuments).isNotEmpty();
		assertThat(mapOfDocuments).hasSize(referenceIds.size());
		assertThat(mapOfDocuments.get(referenceIds.get(0))).isNotEmpty();
		assertThat(mapOfDocuments.get(referenceIds.get(1))).isNotEmpty();
	}

	@Test
	void testGetMapOfDocumentsByReferenceIdListAndReferenceNameForInvalidId() {
		Map<Long, Set<DocumentDTO>> mapOfDocuments = documentService
				.getMapOfDocumentsByReferenceIdListAndReferenceName(Arrays.asList(0L), "COUNTRYCOUNTRY");
		assertThat(mapOfDocuments).isEmpty();
	}

	@Test
	void testGetMapOfDocumentsByReferenceIdListAndReferenceNameForInvalidName() {
		Map<Long, Set<DocumentDTO>> mapOfDocuments = documentService
				.getMapOfDocumentsByReferenceIdListAndReferenceName(Arrays.asList(0L), COUNTRY);
		assertThat(mapOfDocuments).isEmpty();
	}

	@Test
	void testDeleteByReferenceNameAndIdIn() {
		Set<Document> documents = prepareDocuments();
		List<Document> savedDocuments = documentService.saveAll(documents);
		Set<Long> ids = savedDocuments.stream().map(Document::getId).collect(Collectors.toSet());
		documentService.deleteByReferenceNameAndIdIn(COUNTRY, ids);
		ids.stream().forEach(id -> {
			assertFalse(documentRepository.existsById(id));
		});
	}

}
