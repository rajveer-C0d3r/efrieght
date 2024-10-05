package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.GstDTO;
import com.grtship.mdm.criteria.GstCriteria;
import com.grtship.mdm.service.GstFilterService;
import com.grtship.mdm.service.GstService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class GstFilterServiceImplTest {

	@Autowired private GstFilterService gstFilterService;
	
	@Mock Pageable pageable;
	
	@Autowired private GstService gstService;
	
	private GstDTO gstDTO;
	
	private GstCriteria gstCriteria;
	
	@BeforeEach
	public void setUp() {
		gstDTO=GstServiceImplTest.prepareGstDto();
	}

	@Test
	void testFindByCriteriaGstCriteriaId() {
		GstDTO savedGst = gstService.save(gstDTO);
		gstCriteria = new GstCriteria();
		gstCriteria.setId(savedGst.getId());
		List<GstDTO> gstDTOs = gstFilterService.findByCriteria(gstCriteria);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getHsnSacCode() != null);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getType() != null);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getId().equals(gstObj.getId()));
	}
	
	@Test
	void testFindByCriteriaGstCriteriaIdForInvalidId() {
		gstCriteria = new GstCriteria();
		gstCriteria.setId(0L);
		List<GstDTO> gstDTOs = gstFilterService.findByCriteria(gstCriteria);
		assertThat(gstDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaGstCriteriaHsnSacCode() {
		GstDTO savedGst=gstService.save(gstDTO);
		gstCriteria=new GstCriteria();
		gstCriteria.setHsnSacCode(savedGst.getHsnSacCode());
		List<GstDTO> gstDTOs = gstFilterService.findByCriteria(gstCriteria);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getHsnSacCode().equals(gstCriteria.getHsnSacCode()));
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getType() != null);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getId()!=null);
	}
	
	@Test
	void testFindByCriteriaGstCriteriaHsnSacCodeForInvalidCode() {
		gstCriteria = new GstCriteria();
		gstCriteria.setHsnSacCode("JAYESHJAIN156");
		List<GstDTO> gstDTOs = gstFilterService.findByCriteria(gstCriteria);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getHsnSacCode() != null);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getType() != null);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getId() != null);
		assertThat(gstDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaGstCriteriaType() {
		gstService.save(gstDTO);
		gstCriteria=new GstCriteria();
		gstCriteria.setType("GOODS");
		List<GstDTO> gstDTOs = gstFilterService.findByCriteria(gstCriteria);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getHsnSacCode() != null);
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getType().toString().equals(gstCriteria.getType()));
		assertThat(gstDTOs).allMatch(gstObj -> gstObj.getId()!=null);
	}
	

	@Test
	void testFindByCriteriaGstCriteriaPageable() {
		gstService.save(gstDTO);
		gstCriteria=new GstCriteria();
		gstCriteria.setType("GOODS");
		Page<GstDTO> gstDTOs = gstFilterService.findByCriteria(gstCriteria,PageRequest.of(0,20));
		assertThat(gstDTOs.getSize()).isEqualTo(20);
		assertThat(gstDTOs.getContent()).allMatch(gstObj -> gstObj.getHsnSacCode().equals(gstCriteria.getHsnSacCode()));
		assertThat(gstDTOs.getContent()).allMatch(gstObj -> gstObj.getType() != null);
		assertThat(gstDTOs.getContent()).allMatch(gstObj -> gstObj.getId()!=null);
	}

}
