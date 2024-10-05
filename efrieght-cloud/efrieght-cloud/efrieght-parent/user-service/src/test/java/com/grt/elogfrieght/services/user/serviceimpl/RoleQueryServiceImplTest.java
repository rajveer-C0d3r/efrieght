package com.grt.elogfrieght.services.user.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.criteria.RoleCriteria;
import com.grt.elogfrieght.services.user.domain.Role;
import com.grt.elogfrieght.services.user.repository.PermissionRepository;
import com.grtship.core.dto.RoleDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.filter.LongFilter;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class RoleQueryServiceImplTest {
	
	@Autowired private EntityManager em;
	@Autowired private RoleServiceImpl roleService;
	@MockBean private PermissionRepository permissionRepository;
    @Autowired private RoleQueryServiceImpl roleQueryServiceImpl;
    @Mock Pageable pageable;

	public Role role;
	public RoleDTO roleDto;
	private RoleCriteria criteria;
	
	@BeforeEach
	void setUp() {
		roleDto=RoleServiceImplTest.createRoleDto(em);
	}

	@Test
	void testFindByCriteriaRoleCriteriaName() {
		RoleDTO savedRole = roleService.save(roleDto);
		criteria = new RoleCriteria();
		criteria.setName(savedRole.getName());
		List<RoleDTO> roleDTOs = roleQueryServiceImpl.findByCriteria(criteria);
		assertThat(roleDTOs).allMatch(role -> role.getId() != null);
		assertThat(roleDTOs).allMatch(role -> role.getName().contains(criteria.getName()));
		assertThat(roleDTOs).allMatch(role -> role.getStatus() != null);
	}
	
	@Test
	void testFindByCriteriaRoleCriteriaStatus() {
		roleService.save(roleDto);
		criteria = new RoleCriteria();
		criteria.setStatus("PENDING");
		List<RoleDTO> roleDTOs = roleQueryServiceImpl.findByCriteria(criteria);
		assertThat(roleDTOs).isNotEmpty();
		assertThat(roleDTOs).allMatch(role -> role.getId() != null);
		assertThat(roleDTOs).allMatch(role -> role.getName() != null);
		assertThat(roleDTOs).allMatch(role -> role.getStatus().equals(DomainStatus.valueOf(criteria.getStatus())));
	}
	
	@Test
	void testFindByCriteriaRoleCriteriaActiveFlag() {
		RoleDTO savedRole = roleService.save(roleDto);
		criteria = new RoleCriteria();
		criteria.setActive(savedRole.getActiveFlag());
		List<RoleDTO> roleDTOs = roleQueryServiceImpl.findByCriteria(criteria);
		assertThat(roleDTOs).isNotEmpty();
		assertThat(roleDTOs).allMatch(role -> role.getId() != null);
		assertThat(roleDTOs).allMatch(role -> role.getName() != null);
		assertThat(roleDTOs).allMatch(role -> role.getActiveFlag().equals(criteria.getActive()));
	}
	
	@Test
	void testFindByCriteriaRoleCriteriaSubmittedForApproval() {
		RoleDTO savedRole = roleService.save(roleDto);
		criteria = new RoleCriteria();
		criteria.setSubmittedForApproval(savedRole.getSubmittedForApproval());
		List<RoleDTO> roleDTOs = roleQueryServiceImpl.findByCriteria(criteria);
		assertThat(roleDTOs).isNotEmpty();
		assertThat(roleDTOs).allMatch(role -> role.getId() != null);
		assertThat(roleDTOs).allMatch(role -> role.getName() != null);
		assertThat(roleDTOs)
				.allMatch(role -> role.getSubmittedForApproval().equals(criteria.getSubmittedForApproval()));
	}
	
	@Test
	void testFindByCriteriaRoleCriteriaId() {
		RoleDTO savedRole = roleService.save(roleDto);
		LongFilter idFilter = new LongFilter();
		idFilter.setEquals(savedRole.getId());
		RoleCriteria idCriteria = new RoleCriteria();
		idCriteria.setId(idFilter);
		List<RoleDTO> roleDTOs = roleQueryServiceImpl.findByCriteria(idCriteria);
		System.out.println("ROle Dtos" + roleDTOs);
		assertThat(roleDTOs).allMatch(role -> role.getId() == idFilter.getEquals());
		assertThat(roleDTOs).allMatch(role -> role.getName() != null);
		assertThat(roleDTOs).allMatch(role -> role.getActiveFlag() != null);
	}

	@Test
	void testFindByCriteriaRoleCriteriaPageable() {
		RoleDTO savedRole = roleService.save(roleDto);
		criteria = new RoleCriteria();
		criteria.setSubmittedForApproval(savedRole.getSubmittedForApproval());
		Page<RoleDTO> roleDTOs = roleQueryServiceImpl.findByCriteria(criteria, PageRequest.of(0, 20));
		assertThat(roleDTOs).isNotEmpty();
		assertThat(roleDTOs.getSize()).isEqualTo(20);
		assertThat(roleDTOs.getContent()).allMatch(role -> role.getId() != null);
		assertThat(roleDTOs.getContent()).allMatch(role -> role.getName() != null);
		assertThat(roleDTOs.getContent()).allMatch(role -> role.getActiveFlag() != null);
	}
	

	@Test
	void testCountByCriteria() {
		RoleDTO savedRole = roleService.save(roleDto);
		criteria = new RoleCriteria();
		criteria.setSubmittedForApproval(savedRole.getSubmittedForApproval());
		Long count = roleQueryServiceImpl.countByCriteria(criteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}

	@Test
	void testFindOne() {
		RoleDTO savedRole = roleService.save(roleDto);
		RoleDTO getRoleById = roleQueryServiceImpl.findOne(savedRole.getId()).get();
		Role roleFromDB = em.find(Role.class, getRoleById.getId());
		assertEquals(getRoleById.getName(), savedRole.getName());
		assertEquals(getRoleById.getId(), savedRole.getId());
		assertNotNull(getRoleById.getPermissions());
		assertThat(roleFromDB.getPermissions()).hasSize(3);
	}

}
