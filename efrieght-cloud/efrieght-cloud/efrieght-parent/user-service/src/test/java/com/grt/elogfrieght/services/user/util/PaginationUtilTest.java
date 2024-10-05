package com.grt.elogfrieght.services.user.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grt.elogfrieght.services.user.criteria.RoleCriteria;
import com.grt.elogfrieght.services.user.serviceimpl.RoleQueryServiceImpl;
import com.grt.elogfrieght.services.user.serviceimpl.RoleServiceImpl;
import com.grt.elogfrieght.services.user.serviceimpl.RoleServiceImplTest;
import com.grtship.core.dto.RoleDTO;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class PaginationUtilTest {
	
	private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
	
	@Autowired EntityManager em;
	@Autowired private RoleServiceImpl roleService;
	@Autowired private RoleQueryServiceImpl roleQueryServiceImpl;
	private RoleDTO roleDto;
	
	 @BeforeEach
	 void beforeEach(){
		 roleDto = RoleServiceImplTest.createRoleDto(em);
	 }

	@Test
	void testGeneratePaginationHttpHeaders() {
		RoleDTO savedRole = roleService.save(roleDto);
		RoleCriteria criteria = new RoleCriteria();
		criteria.setSubmittedForApproval(savedRole.getSubmittedForApproval());
		Page<RoleDTO> roleDTOs = roleQueryServiceImpl.findByCriteria(criteria, PageRequest.of(0, 20));
		HttpHeaders generatePaginationHttpHeaders = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), roleDTOs);
		assertThat(generatePaginationHttpHeaders).isNotNull();
		assertThat(generatePaginationHttpHeaders).isNotEmpty();
		assertThat(generatePaginationHttpHeaders.get(HEADER_X_TOTAL_COUNT)).hasSizeGreaterThanOrEqualTo(1);
		assertTrue(generatePaginationHttpHeaders.get(HttpHeaders.LINK).get(0).contains("page=0&size=20"));
	}
}
