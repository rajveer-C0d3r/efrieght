package com.grtship.client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.BranchGstDetailsDTO;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class BranchGstDetailsServiceTest {
	
	@Autowired
	private BranchGstDetailsService branchGstDetailsService;
	
	private BranchGstDetailsDTO branchGstDetailsDTO;
	
	public static BranchGstDetailsDTO prepareBranchGstDetails() {
		BranchGstDetailsDTO branchGstDetailsDTO=new BranchGstDetailsDTO();
		branchGstDetailsDTO.setCompanyId(1L);
		branchGstDetailsDTO.setGstNo("TVYUB8165165KM");
		branchGstDetailsDTO.setNatureOfBusinessActivity("Xerox");
		return branchGstDetailsDTO;
	}
	
	@BeforeEach
	void setUp() {
		branchGstDetailsDTO=prepareBranchGstDetails();
	}

	@Test
	void testSave() {
		BranchGstDetailsDTO savedGst = branchGstDetailsService.save(branchGstDetailsDTO);
		assertThat(savedGst.getId()).isNotNull();
		assertThat(savedGst.getGstNo()).isEqualTo(branchGstDetailsDTO.getGstNo());
		assertThat(savedGst.getNatureOfBusinessActivity()).isEqualTo(branchGstDetailsDTO.getNatureOfBusinessActivity());
	}
    
	@Test
	void checkGstNoIsRequired() {
		branchGstDetailsDTO.setGstNo(null);
		assertThrows(ConstraintViolationException.class, () -> {
			branchGstDetailsService.save(branchGstDetailsDTO);
		});
	}
	
	@Test
	void checkGstNoShouldNotBeMoreThan16Characters() {
		branchGstDetailsDTO.setGstNo("VBHY8919815616151KMKML");
		assertThrows(ConstraintViolationException.class, () -> {
			branchGstDetailsService.save(branchGstDetailsDTO);
		});
	}
	
	@Test
	void checkNatureOfBussinessShouldNotBeMoreThan16Characters() {
		branchGstDetailsDTO.setGstNo("VBHY8919815616151KMKML");
		assertThrows(ConstraintViolationException.class, () -> {
			branchGstDetailsService.save(branchGstDetailsDTO);
		});
	}

	@Test
	void testFindAll() {
		branchGstDetailsService.save(branchGstDetailsDTO);
		List<BranchGstDetailsDTO> detailsDTOs = branchGstDetailsService.findAll();
		assertThat(detailsDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(detailsDTOs).allMatch(gst -> gst.getId() != null);
		assertThat(detailsDTOs).allMatch(gst -> gst.getGstNo() != null);
	}

	@Test
	void testFindOne() {
		BranchGstDetailsDTO savedGst = branchGstDetailsService.save(branchGstDetailsDTO);
		BranchGstDetailsDTO getGstById = branchGstDetailsService.findOne(savedGst.getId()).get();
		assertThat(getGstById.getId()).isNotNull();
		assertThat(getGstById.getId()).isEqualTo(savedGst.getId());
		assertThat(getGstById.getGstNo()).isEqualTo(savedGst.getGstNo());
		assertThat(getGstById.getNatureOfBusinessActivity()).isEqualTo(savedGst.getNatureOfBusinessActivity());
	}

	@Test
	void testDelete() {
		BranchGstDetailsDTO savedGst = branchGstDetailsService.save(branchGstDetailsDTO);
		branchGstDetailsService.delete(savedGst.getId());
		Optional<BranchGstDetailsDTO> getGstById=branchGstDetailsService.findOne(savedGst.getId());
		assertFalse(getGstById.isPresent());
	}
}
