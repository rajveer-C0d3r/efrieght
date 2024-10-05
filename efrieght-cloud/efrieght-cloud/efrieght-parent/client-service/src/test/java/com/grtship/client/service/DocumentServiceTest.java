package com.grtship.client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.domain.Client;
import com.grtship.client.domain.Company;
import com.grtship.client.domain.Document;
import com.grtship.client.mapper.DocumentMapper;
import com.grtship.client.serviceimpl.CompanyBranchServiceImplTest;
import com.grtship.client.serviceimpl.CompanyServiceImplTest;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.enumeration.DocumentType;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class DocumentServiceTest {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private EntityManager em;

	@Autowired
	private DocumentMapper documentMapper;

	private DocumentDTO documentDTO;

	private static DocumentDTO prepareDocumentDto(EntityManager em) {
		Client client = CompanyServiceImplTest.prepareClient();
		em.persist(client);
		em.flush();

		Company company = CompanyBranchServiceImplTest.prepareCompany(em, client);

		DocumentDTO documentDTO = new DocumentDTO();
		documentDTO.setCode("ADR");
		documentDTO.setName("Aadhar Card");
		documentDTO.setType(DocumentType.OPERATIONAL);
		documentDTO.setIsMandatory(Boolean.TRUE);
		documentDTO.setUploadDocument(new byte[] { 1, 6, 3 });
		documentDTO.setReferenceId(1L);
		documentDTO.setReferenceName("Entity");
		documentDTO.setCompanyId(company.getId());
		documentDTO.setClientId(client.getId());
		return documentDTO;
	}

	@BeforeEach
	void setUp() {
		documentDTO = prepareDocumentDto(em);
	}

	@Test
	void testSave() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		assertThat(savedDocument.getId()).isNotNull();
		assertThat(savedDocument.getCode()).isEqualTo(documentDTO.getCode());
		assertThat(savedDocument.getName()).isEqualTo(documentDTO.getName());
		assertThat(savedDocument.getReferenceId()).isEqualTo(documentDTO.getReferenceId());
		assertThat(savedDocument.getReferenceName()).isEqualTo(documentDTO.getReferenceName());
	}
	
	@Test
	void testUpdate() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		savedDocument.setCode("PAN");
		savedDocument.setName("Pan Card");
		DocumentDTO updatedDocument = documentService.save(savedDocument);
		assertThat(updatedDocument.getId()).isNotNull();
		assertThat(updatedDocument.getId()).isEqualTo(savedDocument.getId());
		assertThat(updatedDocument.getCode()).isNotEqualTo(documentDTO.getCode());
		assertThat(updatedDocument.getName()).isNotEqualTo(documentDTO.getName());
		assertThat(updatedDocument.getReferenceId()).isEqualTo(savedDocument.getReferenceId());
		assertThat(updatedDocument.getReferenceName()).isEqualTo(savedDocument.getReferenceName());
	}

	@Test
	void testFindAll() {
		documentService.save(documentDTO);
		List<DocumentDTO> documentDTOs = documentService.findAll();
		assertThat(documentDTOs).isNotEmpty();
		assertThat(documentDTOs).allMatch(document -> document.getId() != null);
		assertThat(documentDTOs).allMatch(document -> document.getReferenceId() != null);
		assertThat(documentDTOs).allMatch(document -> document.getReferenceName() != null);
	}

	@Test
	void testFindOne() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		DocumentDTO getDocumentById = documentService.findOne(savedDocument.getId()).get();
		assertThat(getDocumentById.getId()).isNotNull();
		assertThat(getDocumentById.getCode()).isEqualTo(savedDocument.getCode());
		assertThat(getDocumentById.getName()).isEqualTo(savedDocument.getName());
		assertThat(getDocumentById.getReferenceId()).isEqualTo(savedDocument.getReferenceId());
		assertThat(getDocumentById.getReferenceName()).isEqualTo(savedDocument.getReferenceName());
	}

	@Test
	void testDelete() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		documentService.delete(savedDocument.getId());
		Optional<DocumentDTO> getDocumentById = documentService.findOne(savedDocument.getId());
		assertFalse(getDocumentById.isPresent());
	}

	@Test
	void testSaveAll() {
		Set<Document> documents = new HashSet<>();
		documents.add(documentMapper.toEntity(documentDTO));
		documentDTO.setCode("PAN");
		documentDTO.setName("PAN Card");
		documents.add(documentMapper.toEntity(documentDTO));
		List<Document> getDocuments = documentService.saveAll(documents);
		assertThat(getDocuments).isNotEmpty();
		assertThat(getDocuments).allMatch(document -> document.getId() != null);
		assertThat(getDocuments).allMatch(document -> document.getReferenceId() != null);
		assertThat(getDocuments).allMatch(document -> document.getReferenceName() != null);
	}

	@Test
	void testGetMapOfDocumentsByReferenceIdList() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		List<Long> referenceIds = new ArrayList<>(Arrays.asList(savedDocument.getReferenceId()));
		Map<Long, List<DocumentDTO>> documentsByReferenceId = documentService
				.getMapOfDocumentsByReferenceIdList(referenceIds, savedDocument.getReferenceName());
		assertThat(documentsByReferenceId).isNotEmpty();
		assertThat(documentsByReferenceId.get(savedDocument.getReferenceId())).hasSizeGreaterThanOrEqualTo(1);
	}

	@Test
	void testGetDocumentIdByReferenceIdAndReferenceName() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		Set<Long> documentIds = documentService.getDocumentIdByReferenceIdAndReferenceName(
				savedDocument.getReferenceId(), savedDocument.getReferenceName());
		assertThat(documentIds).hasSizeGreaterThanOrEqualTo(1);
	}

	@Test
	void testDeleteByDocumentIdList() {
		DocumentDTO savedDocument = documentService.save(documentDTO);
		documentService.deleteByDocumentIdList(new TreeSet<>(Arrays.asList(savedDocument.getId())));
		Optional<DocumentDTO> getDocumentById = documentService.findOne(savedDocument.getId());
		assertFalse(getDocumentById.isPresent());
	}
}
