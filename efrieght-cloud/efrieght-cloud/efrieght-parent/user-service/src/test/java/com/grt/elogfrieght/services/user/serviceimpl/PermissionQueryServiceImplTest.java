package com.grt.elogfrieght.services.user.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.criteria.PermissionCriteria;
import com.grt.elogfrieght.services.user.domain.Permission;
import com.grt.elogfrieght.services.user.service.PermissionService;
import com.grtship.core.dto.PermissionDTO;
import com.grtship.core.enumeration.PermissionType;
import com.grtship.core.filter.StringFilter;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class PermissionQueryServiceImplTest {
	@Autowired private EntityManager em;
	@Autowired private PermissionQueryServiceImpl permissionQueryService;
	@Autowired private PermissionService permissionService;
	@Mock Pageable pageable;
	
	private PermissionDTO permissionDTO;
	private PermissionCriteria criteria;

	@BeforeEach
	void setUp() throws Exception {
		permissionDTO = PermissionServiceImplTest.createPermissionDto();
	}
	
	@Test
	void testFindByCriteriaPermissionCriteriaCode() {
		PermissionDTO savedPermission = permissionService.save(permissionDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedPermission.getPermissionCode());
		criteria = new PermissionCriteria();
		criteria.setPermissionCode(stringFilter);
		List<PermissionDTO> permissions = permissionQueryService.findByCriteria(criteria);
		assertThat(permissions).isNotNull();
		assertThat(permissions).allMatch(permission -> permission.getModuleName() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionCode().equals(stringFilter.getEquals()));
		assertThat(permissions).allMatch(permission -> permission.getPermissionLabel() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionType() != null);
	}
	
	@Test
	void testFindByCriteriaPermissionCriteriaModuleName() {
		PermissionDTO savedPermission = permissionService.save(permissionDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedPermission.getModuleName());
		criteria = new PermissionCriteria();
		criteria.setModuleName(stringFilter);
		List<PermissionDTO> permissions = permissionQueryService.findByCriteria(criteria);
		assertThat(permissions).isNotNull();
		assertThat(permissions).allMatch(permission -> permission.getModuleName().equals(stringFilter.getEquals()));
		assertThat(permissions).allMatch(permission -> permission.getPermissionCode() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionLabel() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionType() != null);
	}
	
	@Test
	void testFindByCriteriaPermissionCriteriaType() {
		permissionService.save(permissionDTO);
		criteria = new PermissionCriteria();
		criteria.setType("CLIENT");
		List<PermissionDTO> permissions = permissionQueryService.findByCriteria(criteria);
		assertThat(permissions).isNotNull();
		assertThat(permissions).allMatch(permission -> permission.getModuleName() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionCode() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionLabel() != null);
		assertThat(permissions).allMatch(
				permission -> permission.getPermissionType().equals(PermissionType.valueOf(criteria.getType())));
	}
	
	@Test
	void testFindByCriteriaPermissionCriteriaTypes() {
		permissionService.save(permissionDTO);
		criteria = new PermissionCriteria();
		criteria.setTypes(new ArrayList<>(Arrays.asList("CLIENT")));
		List<PermissionDTO> permissions = permissionQueryService.findByCriteria(criteria);
		assertThat(permissions).isNotNull();
		assertThat(permissions).allMatch(permission -> permission.getModuleName() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionCode() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionLabel() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionType()
				.equals(PermissionType.valueOf(criteria.getTypes().get(0))));
	}

	@Test
	void testFindByCriteriaPermissionCriteriaPageable() {
		PermissionDTO savedPermission = permissionService.save(permissionDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedPermission.getPermissionCode());
		criteria = new PermissionCriteria();
		criteria.setPermissionCode(stringFilter);
		Page<PermissionDTO> permissions = permissionQueryService.findByCriteria(criteria, PageRequest.of(0, 20));
		assertThat(permissions.getSize()).isEqualTo(20);
		assertThat(permissions.getContent()).isNotNull();
		assertThat(permissions.getContent()).allMatch(permission -> permission.getModuleName() != null);
		assertThat(permissions.getContent())
				.allMatch(permission -> permission.getPermissionCode().equals(stringFilter.getEquals()));
		assertThat(permissions.getContent()).allMatch(permission -> permission.getPermissionLabel() != null);
		assertThat(permissions.getContent()).allMatch(permission -> permission.getPermissionType() != null);
	}

	@Test
	void testGetByPermissionCodes() {
		Permission savedPermission1 = PermissionServiceImplTest.createEntity("ENTITY_ADDITION","ENTITY",PermissionType.CLIENT);
		Permission savedPermission2 = PermissionServiceImplTest.createEntity("COUNTRY_ADDITION","ENTITY",PermissionType.CLIENT);
		Permission savedPermission3 = PermissionServiceImplTest.createEntity("COMPANY_ADDITION","ENTITY",PermissionType.CLIENT);
		
		em.persist(savedPermission1);
		em.flush();
		em.persist(savedPermission2);
		em.flush();
		em.persist(savedPermission3);
		em.flush();
		
		List<String> permissionCodes = new ArrayList<>();
		permissionCodes.add(savedPermission1.getPermissionCode());
		permissionCodes.add(savedPermission2.getPermissionCode());
		
		List<PermissionDTO> permissions = permissionQueryService.getByPermissionCodes(permissionCodes);
		assertThat(permissions).isNotEmpty();
		assertThat(permissions.size()).isGreaterThanOrEqualTo(2);
		assertThat(permissions).isNotNull();
		assertThat(permissions).allMatch(permission -> permission.getModuleName() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionCode() != null);
		assertThat(permissions).allMatch(permission -> permission.getPermissionLabel() != null);
	}

}
