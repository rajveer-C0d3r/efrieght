package com.grtship.account.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.adaptor.MasterModuleAdapter;
import com.grtship.account.criteria.GroupCriteria;
import com.grtship.account.service.GroupQueryService;
import com.grtship.account.service.GroupService;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.GroupDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.filter.BooleanFilter;
import com.grtship.core.filter.StringFilter;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class GroupQueryServiceImplTest {
	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupQueryService groupQueryService;

	private GroupDTO groupDto;

	@MockBean
	private MasterModuleAdapter masterModuleAdapter;

	private GroupCriteria groupCriteria;

	@BeforeEach
	void setUp() throws Exception {
		groupDto = GroupServiceImplTest.prepareData();
	}

	@Test
	void testFindByCriteriaGroupCriteriaName() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		groupCriteria = new GroupCriteria();
		StringFilter filter = new StringFilter();
		filter.setContains(savedGroup.getName());
		groupCriteria.setName(filter);
		List<GroupDTO> groupDTOs = groupQueryService.findByCriteria(groupCriteria);
		assertThat(groupDTOs).isNotNull();
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getId() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getName().equals(savedGroup.getName()));
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getCode() != null);
	}

	@Test
	void testFindByCriteriaGroupCriteriaCode() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		groupCriteria = new GroupCriteria();
		StringFilter filter = new StringFilter();
		filter.setContains(savedGroup.getCode());
		groupCriteria.setCode(filter);
		List<GroupDTO> groupDTOs = groupQueryService.findByCriteria(groupCriteria);
		assertThat(groupDTOs).isNotNull();
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getId() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getName() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getCode().equals(savedGroup.getCode()));
	}

	@SuppressWarnings("unused")
	@Test
	void testFindByCriteriaGroupCriteriaActiveFlag() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		groupCriteria = new GroupCriteria();
		BooleanFilter booleanFilter = new BooleanFilter();
		booleanFilter.setEquals(Boolean.FALSE);
		groupCriteria.setActiveFlag(booleanFilter);
		List<GroupDTO> groupDTOs = groupQueryService.findByCriteria(groupCriteria);
		assertThat(groupDTOs).isNotNull();
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getId() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getName() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getCode() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getActiveFlag().equals(Boolean.FALSE));
	}

	@SuppressWarnings("unused")
	@Test
	void testFindByCriteriaGroupCriteriaActiveStatus() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		groupCriteria = new GroupCriteria();
		groupCriteria.setStatus("PENDING");
		List<GroupDTO> groupDTOs = groupQueryService.findByCriteria(groupCriteria);
		assertThat(groupDTOs).isNotNull();
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getId() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getName() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getCode() != null);
		assertThat(groupDTOs).allMatch(groupObj -> groupObj.getStatus().equals(DomainStatus.valueOf(groupCriteria.getStatus())));
	}

	@Test
	void testFindByCriteriaGroupCriteriaPageable() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		groupCriteria = new GroupCriteria();
		StringFilter filter = new StringFilter();
		filter.setContains(savedGroup.getName());
		groupCriteria.setName(filter);
		Page<GroupDTO> pageableGroups = groupQueryService.findByCriteria(groupCriteria, PageRequest.of(0, 20));
		assertThat(pageableGroups.getNumber()).isEqualTo(0);
		assertThat(pageableGroups.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(pageableGroups.getContent()).allMatch(groupObj -> groupObj.getId() != null);
		assertThat(pageableGroups.getContent()).allMatch(groupObj -> groupObj.getName().equals(savedGroup.getName()));
		assertThat(pageableGroups.getContent()).allMatch(groupObj -> groupObj.getCode() != null);
	}

	@Test
	void testCountByCriteria() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		groupCriteria = new GroupCriteria();
		StringFilter filter = new StringFilter();
		filter.setContains(savedGroup.getName());
		groupCriteria.setName(filter);
		Long count = groupQueryService.countByCriteria(groupCriteria);
		assertThat(count).isEqualTo(1);
	}


	@Test
	void testFindOne() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		GroupDTO getGroupById = groupQueryService.findOne(savedGroup.getId()).get();
		assertThat(getGroupById.getName()).isEqualTo(savedGroup.getName());
		assertThat(getGroupById.getNatureOfGroup()).isEqualTo(savedGroup.getNatureOfGroup());
		assertThat(getGroupById.getNetBalanceFlag()).isEqualTo(savedGroup.getNetBalanceFlag());
		assertThat(getGroupById.getSubGroupFlag()).isEqualTo(savedGroup.getSubGroupFlag());
		assertThat(getGroupById.getNetBalanceFlag()).isEqualTo(savedGroup.getNetBalanceFlag());
	}

	@SuppressWarnings("unused")
	@Test
	void testGetParentGroups() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		List<BaseDTO> baseDTOs = groupQueryService.getParentGroups();
		assertThat(baseDTOs).isNotNull();
		assertThat(baseDTOs).allMatch(groupObj -> groupObj.getId() != null);
		assertThat(baseDTOs).allMatch(groupObj -> groupObj.getName() != null);
	}

	@Test
	void testGetChildGroups() {

	}
	
	public void prepareCodeMocking() {
		when(masterModuleAdapter.generateCode(ReferenceNameConstant.GROUP, null)).thenReturn("GRP789");
	}
}
