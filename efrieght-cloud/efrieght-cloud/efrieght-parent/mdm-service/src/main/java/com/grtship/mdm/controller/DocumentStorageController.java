package com.grtship.mdm.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.grtship.core.dto.DocumentDownloadDTO;
import com.grtship.core.dto.DocumentStorageDTO;
import com.grtship.mdm.service.DocumentQueryService;
import com.grtship.mdm.service.DocumentStorageService;
import com.grtship.mdm.validator.DocumentStorageValidator;

/**
 * REST controller for managing {@link com.grt.efreight.domain.DocumentStorage}.
 */
@RestController
@RequestMapping("/api/document-storage")
public class DocumentStorageController {

	@Value("${spring.application.name}")
	private String applicationName;

	private final DocumentStorageService documentStorageService;

	@Autowired private DocumentStorageValidator documentStorageValidator;
	@Autowired private DocumentQueryService documentQueryService; 

	public DocumentStorageController(DocumentStorageService documentStorageService) {
		this.documentStorageService = documentStorageService; 
	}

	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	public ResponseEntity<DocumentStorageDTO> uploadDocument(@RequestParam("file") MultipartFile file,
			@RequestParam("code") String code, @RequestParam("referenceName") String referenceName,
			@RequestParam("referenceId") Long referenceId,@RequestHeader Map<String,String> headers) throws IOException {

		DocumentStorageDTO documentStorageDto = new DocumentStorageDTO();
		documentStorageDto=getDocumentStorageDtoObj(code, referenceName, referenceId, headers);
		documentStorageValidator.validateFile(file);
		documentStorageDto = documentStorageService.storeFile(file, documentStorageDto);

		return ResponseEntity.ok(documentStorageDto);
	}

	@PutMapping(value = "/upload", consumes = { "multipart/form-data" })
	public ResponseEntity<DocumentStorageDTO> updateDocument(@RequestParam("file") MultipartFile file,
			@RequestParam("id") Long id, @RequestParam("code") String code,
			@RequestParam("referenceName") String referenceName, @RequestParam("referenceId") Long referenceId,
			@RequestHeader Map<String,String> headers)
			throws IOException {

		DocumentStorageDTO documentStorageDto = new DocumentStorageDTO();
		documentStorageDto=getDocumentStorageDtoObj(code, referenceName, referenceId, headers);
		documentStorageDto.setId(id);
		documentStorageValidator.validateFile(file);
		documentStorageDto = documentStorageService.storeFile(file, documentStorageDto);

		return ResponseEntity.ok(documentStorageDto);
	}

	private DocumentStorageDTO getDocumentStorageDtoObj(String code, String referenceName, Long referenceId,
			Map<String, String> headers) {
		DocumentStorageDTO documentStorageDto = new DocumentStorageDTO();
		documentStorageDto.setDocumentCode(code);
		documentStorageDto.setReferenceId(referenceId);
		documentStorageDto.setReferenceName(referenceName);
		for (Entry<String, String> element : headers.entrySet()) {
			if(element.getKey().equals("clientid") && element.getKey()!=null) {
				Long clientId = Long.parseLong(element.getValue());
	        	documentStorageDto.setClientId(clientId);	
	        }
	        if(element.getKey().equals("companyid") && element.getKey()!=null) {
	        	documentStorageDto.setCompanyId(Long.parseLong(element.getValue()));	
	        }
		}
		return documentStorageDto;
	}
	
	@GetMapping("/action")
	public ResponseEntity<Resource> downloadFile(@RequestParam String fileName, HttpServletRequest request)
			throws MalformedURLException, FileNotFoundException {
		Resource resource = documentStorageService.loadFileAsResource(fileName);
		String originalFileName = documentStorageService.getOriginalFileName(fileName);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
		}
		if (contentType == null)
			contentType = "application/octet-stream";
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
				.body(resource);
	}

	@PostMapping("/getAll")
	public List<DocumentStorageDTO> getDocuments(@RequestParam("referenceName") String referenceName,
			@RequestBody List<Long> referenceIds) {
		return documentStorageService.getByReferenceIdsAndName(referenceIds, referenceName);
	}
	
	@GetMapping("/get/{referenceName}/{referenceId}")
	public Set<DocumentDownloadDTO> getDocumentsOnEdit(@PathVariable("referenceName") String referenceName,
			@PathVariable("referenceId") Long referenceId) {
		return documentQueryService.getDocumentsOnEdit(referenceId, referenceName);
	}
}
