package com.grt.elogfrieght.services.user.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.Permission;
import com.grt.elogfrieght.services.user.domain.Role;
import com.grt.elogfrieght.services.user.repository.PermissionRepository;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.PermissionDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.RoleDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.PermissionType;

import javassist.tools.rmi.ObjectNotFoundException;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class RoleServiceImplTest {

	@Autowired
	private EntityManager em;
	@Autowired
	private RoleServiceImpl roleService;
	@MockBean
	private PermissionRepository permissionRepository;
	@Autowired
	private RoleQueryServiceImpl roleQueryServiceImpl;
	@Mock
	Pageable pageable;
	private RoleDTO roleDto;

	public static RoleDTO createRoleDto(EntityManager em) {
		Permission permission1 = PermissionServiceImplTest.createEntity("ENTITY_ADDITION", "ENTITY",
				PermissionType.CLIENT);
		Permission permission2 = PermissionServiceImplTest.createEntity("COUNTRY_ADDITION", "COUNTRY",
				PermissionType.CLIENT);
		Permission permission3 = PermissionServiceImplTest.createEntity("DESTINATION_ADDITION", "DESTINATION",
				PermissionType.CLIENT);
		em.persist(permission1);
		em.persist(permission2);
		em.persist(permission3);

		Set<PermissionDTO> permissions = new HashSet<>();
		permissions.add(PermissionDTO.builder().permissionCode(permission1.getPermissionCode()).build());
		permissions.add(PermissionDTO.builder().permissionCode(permission2.getPermissionCode()).build());
		permissions.add(PermissionDTO.builder().permissionCode(permission3.getPermissionCode()).build());

		RoleDTO roleDto = new RoleDTO();
		roleDto.setName("Test Role Save");
		roleDto.setIsPublic(Boolean.FALSE);
		roleDto.setClientId(1L);
		roleDto.setCompanyId(1L);
		roleDto.setPermissions(permissions);
		roleDto.setStatus(DomainStatus.PENDING);
		return roleDto;
	}
	 
	 @BeforeEach
	 void beforeEach(){
		 roleDto = createRoleDto(em);
	 }
	 
	@Test
	void testSave() {
		RoleDTO savedRole = roleService.save(roleDto);
		Role roleFromDB = em.find(Role.class, savedRole.getId());
		assertEquals(roleDto.getName(), savedRole.getName());
		assertNotNull(savedRole.getId());
		assertNotNull(savedRole.getPermissions());
		assertThat(roleFromDB.getPermissions()).hasSize(3);
	}
	
	@Test
	void checkNameIsRequired() {
		roleDto.setName(null);
		assertThrows(ConstraintViolationException.class,() -> {
			roleService.save(roleDto);
		});
	}
	
	@Test
	void checkPermissionsIsRequired() {
		roleDto.setPermissions(null);
		assertThrows(ValidationException.class,() -> {
			roleService.save(roleDto);
		});
	}
	
	@Test
	void checkRoleNameIsUnique() {
		roleService.save(roleDto);
		assertThrows(ValidationException.class,() -> {
			roleService.save(roleDto);
		});
	}

	@Test
	void testUpdate() throws InvalidDataException, ObjectNotFoundException {
		RoleDTO savedRole = roleService.save(roleDto);
		savedRole.setName("Test role update");
		RoleDTO updatedRole = roleService.update(savedRole);
		assertThat(updatedRole.getName()).isNotEqualTo(roleDto.getName());
		assertNotNull(updatedRole.getId());
		assertEquals(updatedRole.getId(), savedRole.getId());
	}

	@Test
	void testDelete() {
		RoleDTO savedRole = roleService.save(roleDto);
		roleService.delete(savedRole.getId());
		Optional<RoleDTO> getRoleById=roleQueryServiceImpl.findOne(savedRole.getId());
		assertFalse(getRoleById.isPresent());
	}
	
	
	public DeactivationDTO prepareDeactivationDTO() {
    	DeactivationDTO deactivateDto = new DeactivationDTO();
    	deactivateDto.setDeactivationWefDate(LocalDate.now());
    	deactivateDto.setDeactivationReason("I no longer need this Role");
    	deactivateDto.setType("DEACTIVATE");
		return deactivateDto;
    }
	
	@Test
	void testDeactivate() {
		RoleDTO savedRole = roleService.save(roleDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedRole.getId());

		RoleDTO deactivatedRole = roleService.deactivate(deactivateDto);
		assertThat(deactivatedRole.getDeactivate()).isNotNull();
		assertThat(deactivatedRole.getDeactivate().getDeactivationReason()).isEqualTo(deactivateDto.getDeactivationReason());
		assertThat(deactivatedRole.getDeactivate().getDeactivationWefDate()).isEqualTo(deactivateDto.getDeactivationWefDate());
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenDeactivationDateIsPast() throws Exception {
		RoleDTO savedRole = roleService.save(roleDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedRole.getId());
		deactivateDto.setDeactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			roleService.deactivate(deactivateDto);
		});
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(0L);
		assertThrows(InvalidDataException.class, () -> {
			roleService.deactivate(deactivateDto);
		});
	}
	
	@Test
	void testDeactivateShouldThrowExceptionWhenAlreadyDeactivated() throws Exception {
		DeactivationDTO deactivateDtoToSet = prepareDeactivationDTO();
		deactivateDtoToSet.setDeactivatedDate(LocalDate.now());
		roleDto.setDeactivate(deactivateDtoToSet);
		RoleDTO savedRole = roleService.save(roleDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedRole.getId());
		assertThrows(ValidationException.class, () -> {
			roleService.deactivate(deactivateDto);
		});
	}
	
	public ReactivationDTO prepareReactivationDTO() {
    	ReactivationDTO reactivateDto = new ReactivationDTO();
    	reactivateDto.setReactivationWefDate(LocalDate.now());
    	reactivateDto.setReactivationReason("I want to reactivate this role");
    	reactivateDto.setType("REACTIVATE");
		return reactivateDto;
    }
	
	@Test
	void testReactivate() {
		DeactivationDTO deactivateDtoToSet = prepareDeactivationDTO();
		deactivateDtoToSet.setDeactivatedDate(LocalDate.now());
		roleDto.setDeactivate(deactivateDtoToSet);
		RoleDTO savedRole = roleService.save(roleDto);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(savedRole.getId());

		RoleDTO deactivatedRole = roleService.reactivate(reactivateDto);
		assertThat(deactivatedRole.getReactivate()).isNotNull();
		assertThat(deactivatedRole.getReactivate().getReactivationReason()).isEqualTo(reactivateDto.getReactivationReason());
		assertThat(deactivatedRole.getReactivate().getReactivationWefDate()).isEqualTo(reactivateDto.getReactivationWefDate());
	}

	@Test
	void testReactivateShouldThrowExceptionWhenReactivationDateIsPast() throws Exception {
		RoleDTO savedRole = roleService.save(roleDto);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(savedRole.getId());
		reactivateDto.setReactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			roleService.reactivate(reactivateDto);
		});
	}

	@Test
	void testReactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		DeactivationDTO deactivateDtoToSet = prepareDeactivationDTO();
		deactivateDtoToSet.setDeactivatedDate(LocalDate.now());
		roleDto.setDeactivate(deactivateDtoToSet);
		roleService.save(roleDto);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(0L);
		assertThrows(InvalidDataException.class, () -> {
			roleService.reactivate(reactivateDto);
		});
	}

}
