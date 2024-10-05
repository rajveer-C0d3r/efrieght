package com.grtship.common.aop;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.grtship.common.beans.SpringBeans;
import com.grtship.common.dto.AccessControlDTO;
import com.grtship.core.annotation.AccessFilter;

@Aspect
@Component
public abstract class FilterAspect {
	
	
	
	private static final Logger log = LoggerFactory.getLogger(FilterAspect.class);


	private static final String BRANCH_ID = "branchId";
	private static final String COMPANY_ID = "companyId";
	private static final String CLIENT_ID = "clientId";
	private static final String BRANCH_ACCESS_FILTER = "branchAccessFilter";
	private static final String COMPANY_ACCESS_FILTER = "companyAccessFilter";
	private static final String CLIENT_ACCESS_FILTER = "clientAccessFilter";

	@PersistenceContext
	private EntityManager entityManager;

	@Around(value = "@annotation(accessFilter)")
	public Object enableObjectFilter(ProceedingJoinPoint joinPoint, AccessFilter accessFilter) throws Throwable {

		AccessControlDTO accessBean = SpringBeans.getAccessDTO();
		log.info("AccessControlDTO : {} ",accessBean);
		// Get the Session from the entityManager in current persistence context
		Session session = entityManager.unwrap(Session.class);

		if (session != null) {
			if (accessFilter.clientAccessFlag() && accessBean.getClientId() != null) {
				Filter clientFilter = session.enableFilter(CLIENT_ACCESS_FILTER);
				clientFilter.setParameter(CLIENT_ID, accessBean.getClientId());
			}
			if (accessFilter.companyAccessFlag() && accessBean.getCompanyId() != null) {
				Filter companyFilter = session.enableFilter(COMPANY_ACCESS_FILTER);
				companyFilter.setParameter(COMPANY_ID, accessBean.getCompanyId());
			}
			if (accessFilter.branchAccessFlag() && accessBean.getBranchId() != null) {
				Filter branchFilter = session.enableFilter(BRANCH_ACCESS_FILTER);
				branchFilter.setParameter(BRANCH_ID, accessBean.getBranchId());
			}

			Object obj = joinPoint.proceed();

			session.disableFilter(CLIENT_ACCESS_FILTER);
			session.disableFilter(COMPANY_ACCESS_FILTER);
			session.disableFilter(BRANCH_ACCESS_FILTER);

			return obj;
		}
		return joinPoint.proceed();
	}

}
