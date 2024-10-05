package com.grt.elogfrieght.services.user.aop;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.grtship.common.aop.FilterAspect;
import com.grtship.common.beans.SpringBeans;
import com.grtship.common.dto.AccessControlDTO;
import com.grtship.core.annotation.AccessFilter;

@Aspect
@Component
public class UserFilterAspect extends FilterAspect {

	private static final String BRANCH_ID = "branchId";
	private static final String COMPANY_ID = "companyId";
	private static final String CLIENT_ID = "clientId";
	private static final String INCLUDE_ADMIN = "includeAdmin";
	private static final String INCLUDE_COMPANY = "includeCompany";
	private static final String BRANCH_ACCESS_FILTER = "branchAccessFilter";
	private static final String COMPANY_ACCESS_FILTER = "companyAccessFilter";
	private static final String CLIENT_ACCESS_FILTER = "clientAccessFilter";
	private static final String STANDARD_ACCESS_FILTER = "standardFilter";

	@PersistenceContext
	private EntityManager entityManager;

	@Around(value = "@annotation(accessFilter)")
	@Override
	public Object enableObjectFilter(ProceedingJoinPoint joinPoint, AccessFilter accessFilter) throws Throwable {

		AccessControlDTO accessBean = SpringBeans.getAccessDTO();
		Session session = entityManager.unwrap(Session.class);
		int includeAdmin = 0;
		int includeCompany = 0;
		boolean hasOtherConditions = false;
		if (session != null) {
			if (accessFilter.allowAdminData()) {
				includeAdmin = 1;
			}
			if (accessFilter.companyAccessFlag()) {
				includeCompany = 1;
			}
			if (accessFilter.clientAccessFlag() && accessBean.getClientId() != null) {
				Filter clientFilter = session.enableFilter(CLIENT_ACCESS_FILTER);
				clientFilter.setParameter(CLIENT_ID, accessBean.getClientId());
				clientFilter.setParameter(INCLUDE_ADMIN, 1);
				hasOtherConditions = true;
			}
			if (accessFilter.companyAccessFlag() && accessBean.getCompanyId() != null) {
				Filter companyFilter = session.enableFilter(COMPANY_ACCESS_FILTER);
				companyFilter.setParameter(COMPANY_ID, accessBean.getCompanyId());
				companyFilter.setParameter(INCLUDE_ADMIN, includeAdmin);
				companyFilter.setParameter(INCLUDE_COMPANY, includeCompany);
				hasOtherConditions = true;
			}
			if (accessFilter.branchAccessFlag() && accessBean.getBranchId() != null) {
				Filter branchFilter = session.enableFilter(BRANCH_ACCESS_FILTER);
				branchFilter.setParameter(BRANCH_ID, accessBean.getBranchId());
				branchFilter.setParameter(INCLUDE_ADMIN, includeAdmin);
				branchFilter.setParameter(INCLUDE_COMPANY, includeCompany);
				hasOtherConditions = true;
			}
			
			if (!hasOtherConditions) {
				Filter standardFilter = session.enableFilter(STANDARD_ACCESS_FILTER);
				standardFilter.setParameter(INCLUDE_ADMIN, includeAdmin);
				standardFilter.setParameter(INCLUDE_COMPANY, includeCompany);
			}
			Object obj = joinPoint.proceed();
			session.disableFilter(STANDARD_ACCESS_FILTER);
			session.disableFilter(CLIENT_ACCESS_FILTER);
			session.disableFilter(COMPANY_ACCESS_FILTER);
			session.disableFilter(BRANCH_ACCESS_FILTER);

			return obj;
		}
		return joinPoint.proceed();
	}

}
