package com.grt.elogfrieght.services.user.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.print.Pageable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.Permission;
import com.grt.elogfrieght.services.user.service.PermissionService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.dto.PermissionDTO;
import com.grtship.core.enumeration.PermissionType;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class PermissionServiceImplTest {
	
	@Autowired private PermissionService permissionService;
	@Mock Pageable pageable;
	private PermissionDTO permissionDTO;

	public static Permission createEntity(String code,String moduleName,PermissionType permissionType) {
		return new Permission().permissionCode(code).moduleName(moduleName).permissionType(permissionType)
				.permissionLabel(code);
	}
	
	public static PermissionDTO createPermissionDto(String code,String moduleName,PermissionType permissionType) {
		return PermissionDTO.builder().permissionCode(code).moduleName(moduleName)
				.allPermissions(null).permissionLabel(moduleName).build();
	}
	
	public static PermissionDTO createPermissionDto() {
		PermissionDTO permissionDTO = new PermissionDTO();
		permissionDTO.setPermissionLabel("COMPANY ADD");
		permissionDTO.setPermissionCode("COMPANY_ADD");
		permissionDTO.setModuleName("COMPANY");
		permissionDTO.setPermissionType(PermissionType.BOTH);
		return permissionDTO;
	}
	
	@BeforeEach
	void setUp() throws Exception {
		permissionDTO = createPermissionDto();
	}

	@Test
	void testSave() {
		PermissionDTO savedPermission = permissionService.save(permissionDTO);
		assertThat(savedPermission.getModuleName()).isEqualTo(permissionDTO.getModuleName());
		assertThat(savedPermission.getPermissionLabel()).isEqualTo(permissionDTO.getPermissionLabel());
		assertThat(savedPermission.getPermissionCode()).isEqualTo(permissionDTO.getPermissionCode());
		assertThat(savedPermission.getPermissionType()).isEqualTo(permissionDTO.getPermissionType());
	}

	@Test
	void testUpdate() {
		PermissionDTO savedPermission = permissionService.save(permissionDTO);
		savedPermission.setPermissionType(PermissionType.CLIENT);
		PermissionDTO updatedPermission = permissionService.update(savedPermission);
		assertThat(savedPermission.getModuleName()).isEqualTo(updatedPermission.getModuleName());
		assertThat(savedPermission.getPermissionLabel()).isEqualTo(updatedPermission.getPermissionLabel());
		assertThat(savedPermission.getPermissionCode()).isEqualTo(updatedPermission.getPermissionCode());
		assertThat(updatedPermission.getPermissionType()).isEqualTo(PermissionType.CLIENT);
	}
	
	@Test
	void checkPermissionCodeIsCorrectWhileUpdating() {
		PermissionDTO savedPermission = permissionService.save(permissionDTO);
		savedPermission.setPermissionType(PermissionType.CLIENT);
		savedPermission.setPermissionCode("COMPANY_ADDDD");
		assertThrows(InvalidDataException.class, () -> {
			permissionService.update(savedPermission);
		});
	}
}
