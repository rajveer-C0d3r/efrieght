package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.GstDTO;
import com.grtship.core.enumeration.GstType;
import com.grtship.mdm.service.GstService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class GstServiceImplTest {
	
	@Autowired private GstService gstService;
	
	@Mock Pageable pageable;
	
	private GstDTO gstDTO;
	
	public static GstDTO prepareGstDto() {
		GstDTO gstDTO = new GstDTO();
		gstDTO.setHsnSacCode("GTV8196156KM");
		gstDTO.setType(GstType.GOODS);
		gstDTO.setDescription("First gst No");
		gstDTO.setClientId(1L);
		gstDTO.setCompanyId(1L);
		gstDTO.setBranchId(1L);
		return gstDTO;
	}
	
	@BeforeEach
	void setUp() {
		gstDTO=prepareGstDto();
	}
	

	@Test
	void testSave() {
	  GstDTO savedGst =gstService.save(gstDTO);
	  assertThat(savedGst.getHsnSacCode()).isEqualTo(gstDTO.getHsnSacCode());
	  assertThat(savedGst.getType()).isEqualTo(gstDTO.getType());
	  assertThat(savedGst.getDescription()).isEqualTo(gstDTO.getDescription());
	}
	
	@Test
    void testUpdate() {
	  GstDTO savedGst =gstService.save(gstDTO);
	  savedGst.setHsnSacCode("TGFY81616MK");
	  savedGst.setType(GstType.SERVICE);
	  savedGst.setBranchId(gstDTO.getBranchId());
	  GstDTO updatedGst =gstService.save(savedGst);
	  assertThat(updatedGst.getHsnSacCode()).isNotEqualTo(gstDTO.getHsnSacCode());
	  assertThat(updatedGst.getType()).isNotEqualTo(gstDTO.getType());
	  assertThat(updatedGst.getDescription()).isEqualTo(gstDTO.getDescription());
	  assertThat(updatedGst.getId()).isEqualTo(savedGst.getId());
    }
	
	@Test
    void checkHsnCodeIsRequired() {
	  gstDTO.setHsnSacCode(null);
	  assertThrows(ConstraintViolationException.class,() -> {
		  gstService.save(gstDTO);
	  });
    }
	
	@Test
    void checkGstTypeIsRequired() {
	  gstDTO.setType(null);
	  assertThrows(ConstraintViolationException.class,() -> {
		  gstService.save(gstDTO);
	  });
    }
	
	@Test
	public void checkGstCreatedAtBranchLevel() {
		gstDTO.setBranchId(null);
		assertThrows(ValidationException.class,() -> {
			gstService.save(gstDTO);
		});
	}
	
	@Test
	void checkHsnCodeIsUnique() {
		gstService.save(gstDTO);
	    assertThrows(ValidationException.class,() -> {
		   gstService.save(gstDTO);
	    });
	}

	@Test
	void testFindAll() {
		gstService.save(gstDTO);
		List<GstDTO> gstDTOs = gstService.findAll(pageable).getContent();
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getHsnSacCode() != null);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getType() != null);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getId() != null);
	}

	@Test
	void testFindOne() {
		GstDTO savedGst = gstService.save(gstDTO);
		GstDTO getGstById = gstService.findOne(savedGst.getId()).get();
		assertThat(savedGst.getId()).isEqualTo(getGstById.getId());
		assertThat(savedGst.getHsnSacCode()).isEqualTo(getGstById.getHsnSacCode());
		assertThat(savedGst.getType()).isEqualTo(getGstById.getType());
		assertThat(savedGst.getDescription()).isEqualTo(getGstById.getDescription());
		assertThat(savedGst.getClientId()).isEqualTo(getGstById.getClientId());
		assertThat(savedGst.getCompanyId()).isEqualTo(getGstById.getCompanyId());
	}

	@Test
	void testDelete() {
		GstDTO savedGst = gstService.save(gstDTO);
		gstService.delete(savedGst.getId());
		Optional<GstDTO> gstById = gstService.findOne(savedGst.getId());
		assertFalse(gstById.isPresent());
	}

}
