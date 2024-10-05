package com.grtship.account.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.adaptor.MasterModuleAdapter;
import com.grtship.account.domain.Group;
import com.grtship.account.repository.GroupRepository;
import com.grtship.account.service.GroupQueryService;
import com.grtship.account.service.GroupService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.GroupDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.NatureOfGroup;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class GroupServiceImplTest {
	private static final String DEACTIVATE = "DEACTIVATE";

	private static final String REACTIVATE = "REACTIVATE";

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupQueryService groupQueryService;

	private GroupDTO groupDto;

	@MockBean
	private MasterModuleAdapter masterModuleAdapter;

	public static GroupDTO prepareData() {
		GroupDTO groupDto = new GroupDTO();
		groupDto = new GroupDTO();
		groupDto.setParentGroupName("Jayesh");
		groupDto.setClientId(1L);
		groupDto.setCompanyId(1L);
		groupDto.setName("Vimal Group");
		groupDto.setCode("GRP123");
		groupDto.setNetBalanceFlag(Boolean.TRUE);
		groupDto.setParentGroupId(null);
		groupDto.setNatureOfGroup(NatureOfGroup.INCOME);
		groupDto.setAffectsGrossProfitFlag(Boolean.TRUE);
		groupDto.setSubGroupFlag(Boolean.FALSE);
		groupDto.setActiveFlag(Boolean.FALSE);
		groupDto.setStatus(DomainStatus.PENDING);
		groupDto.setNetBalanceFlag(Boolean.FALSE);
		groupDto.setAliases(prepareAliases());
		groupDto.setDirectIncomeFlag(Boolean.TRUE);
		return groupDto;
	}

	public static Set<ObjectAliasDTO> prepareAliases() {
		Set<ObjectAliasDTO> aliases = new HashSet<>();
		aliases.add(ObjectAliasDTO.builder().name("GRP789").build());
		aliases.add(ObjectAliasDTO.builder().name("GRP563").build());
		return aliases;
	}

	@BeforeEach
	void setUp() throws Exception {
		groupDto = prepareData();
	}

	@Test
	void testSave() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		Group getSavedGroup = groupRepository.findById(savedGroup.getId()).get();
		assertThat(getSavedGroup.getName()).isEqualTo(groupDto.getName());
		assertThat(getSavedGroup.getNatureOfGroup()).isEqualTo(groupDto.getNatureOfGroup());
		assertThat(getSavedGroup.getNetBalanceFlag()).isEqualTo(groupDto.getNetBalanceFlag());
		assertThat(getSavedGroup.getSubGroupFlag()).isEqualTo(groupDto.getSubGroupFlag());
		assertThat(getSavedGroup.getNetBalanceFlag()).isEqualTo(groupDto.getNetBalanceFlag());
	}

	@Test
	void testUpdate() {
		prepareCodeMocking();
		GroupDTO savedDto = groupService.save(groupDto);
		GroupDTO group = groupQueryService.findOne(savedDto.getId()).get();
		group.setName("ABC");
		group.setNatureOfGroup(NatureOfGroup.EXPENSE);
		group.setDirectExpenseFlag(true);
		group.setNetBalanceFlag(false);
		group.setAffectsGrossProfitFlag(false);
		group.setAliases(null);
		GroupDTO updatedDto = groupService.save(group);
		assertThat(group).isNotNull();
		assertThat(group.getName()).isEqualTo(updatedDto.getName());
		assertThat(group.getNatureOfGroup()).isEqualTo(updatedDto.getNatureOfGroup());
		assertThat(group.getAffectsGrossProfitFlag()).isEqualTo(updatedDto.getAffectsGrossProfitFlag());
		assertThat(group.getNetBalanceFlag()).isEqualTo(updatedDto.getNetBalanceFlag());
		assertThat(group.getAliases()).isNullOrEmpty();
	}

	@Test
	void checkCodeIsRequired() {
		groupDto.setCode(null);
		assertThrows(ConstraintViolationException.class, () -> {
			groupService.save(groupDto);
		});
	}

	@Test
	void checkNameIsRequired() {
		groupDto.setName(null);
		assertThrows(ConstraintViolationException.class, () -> {
			groupService.save(groupDto);
		});
	}

	@Test
	void checkNameIsUnique() {
		prepareCodeMocking();
		groupService.save(groupDto);
		groupDto.setAliases(null);
		assertThrows(ValidationException.class, () -> {
			groupService.save(groupDto);
		});
	}

	@Test
	void checkNetBalanceFlagIsRequired() {
		groupDto.setNetBalanceFlag(null);
		assertThrows(ConstraintViolationException.class, () -> {
			groupService.save(groupDto);
		});
	}

	@Test
	void checkAliasIsUnique() {
		prepareCodeMocking();
		groupService.save(groupDto);
		groupDto.setCode("G878");
		groupDto.setName("ABC");
		assertThrows(ValidationException.class, () -> {
			groupService.save(groupDto);
		});
	}

	@Test
	void checkIncomeTypeIsRequired() {
		groupDto.setDirectIncomeFlag(null);
		assertThrows(ValidationException.class, () -> {
			groupService.save(groupDto);
		});
	}

	@Test
	void checkSubgroupFlagIsRequiredWhenUnderIsNotPrimary() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		Group getSavedGroup = groupRepository.findById(savedGroup.getId()).get();
		groupDto.setParentGroupId(getSavedGroup.getId());
		groupDto.setSubGroupFlag(null);
		assertThrows(ValidationException.class, () -> {
			groupService.save(groupDto);
		});
	}

	@Test
	void checkExpenseTypeIsRequired() {
		groupDto.setNatureOfGroup(NatureOfGroup.EXPENSE);
		groupDto.setDirectExpenseFlag(null);
		assertThrows(ValidationException.class, () -> {
			groupService.save(groupDto);
		});
	}

	@Test
	void testDelete() {
		prepareCodeMocking();
		GroupDTO savedDto = groupService.save(groupDto);
		groupService.delete(savedDto.getId());
		Optional<Group> optionalGroup = groupRepository.findById(savedDto.getId());
		assertFalse(optionalGroup.isPresent());
	}

	public DeactivationDTO prepareDeactivationDTO() {
		DeactivationDTO deactivateDto = new DeactivationDTO();
		deactivateDto.setDeactivationWefDate(LocalDate.now());
		deactivateDto.setDeactivationReason("I no longer need this group");
		deactivateDto.setType(DEACTIVATE);
		return deactivateDto;
	}

	@Test
	void testDeactivate() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedGroup.getId());

		GroupDTO groupDto = groupService.deactivate(deactivateDto);

		assertThat(groupDto.getDeactivate()).isNotNull();
		assertThat(groupDto.getDeactivate().getDeactivationReason()).isEqualTo(deactivateDto.getDeactivationReason());
		assertThat(groupDto.getDeactivate().getDeactivationWefDate()).isEqualTo(deactivateDto.getDeactivationWefDate());
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenDeactivationDateIsPast() throws Exception {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedGroup.getId());
		deactivateDto.setDeactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			groupService.deactivate(deactivateDto);
		});
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(0L);
		assertThrows(InvalidDataException.class, () -> {
			groupService.deactivate(deactivateDto);
		});
	}

	public ReactivationDTO prepareReactivationDTO() {
		ReactivationDTO reactivateDto = new ReactivationDTO();
		reactivateDto.setReactivationWefDate(LocalDate.now().plus(Period.ofDays(1)));
		reactivateDto.setReactivationReason("I want to reactivate this group");
		reactivateDto.setType(REACTIVATE);
		return reactivateDto;
	}

	@Test
	void testReactivate() {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setDeactivatedDate(LocalDate.now());
		deactivateDto.setReferenceId(savedGroup.getId());
		groupService.deactivate(deactivateDto);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(savedGroup.getId());
        
		GroupDTO groupDto = groupService.reactivate(reactivateDto);

		assertThat(groupDto.getReactivate()).isNotNull();
		assertThat(groupDto.getReactivate().getReactivationReason()).isEqualTo(reactivateDto.getReactivationReason());
		assertThat(groupDto.getReactivate().getReactivationWefDate()).isEqualTo(reactivateDto.getReactivationWefDate());
	}

	@Test
	void testReactivateShouldThrowExceptionWhenReactivationDateIsPast() throws Exception {
		prepareCodeMocking();
		GroupDTO savedGroup = groupService.save(groupDto);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(savedGroup.getId());
		reactivateDto.setReactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			groupService.reactivate(reactivateDto);
		});
	}

	@Test
	void testReactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(0L);
		assertThrows(InvalidDataException.class, () -> {
			groupService.reactivate(reactivateDto);
		});
	}

	public void prepareCodeMocking() {
		when(masterModuleAdapter.generateCode(ReferenceNameConstant.GROUP, null)).thenReturn("GRP789");
	}

}
