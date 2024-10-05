package com.grtship.core.constant;

public class AuthoritiesConstants {

	private AuthoritiesConstants() {
	}

	public static final String ADMIN = "ROLE_ADMIN";

	public static final String USER = "ROLE_USER";

	public static final String ANONYMOUS = "ROLE_ANONYMOUS";

	public static final String GSA = "ROLE_GSA";

	public static final String CSA = "ROLE_CSA";

	public static final String BSA = "ROLE_BSA";

	public static final String USER_ADD = "USER_ADD";
	public static final String USER_EDIT = "USER_EDIT";
	public static final String USER_DELETE = "USER_DELETE";
	public static final String USER_VIEW = "USER_VIEW";
	public static final String USER_DEACTIVATE = "USER_DEACTIVATE";
	public static final String USER_REACTIVATE = "USER_REACTIVATE";

	public static final String ROLE_ADD = "ROLE_ADD";
	public static final String ROLE_DELETE = "ROLE_DELETE";
	public static final String ROLE_EDIT = "ROLE_EDIT";
	public static final String ROLE_VIEW = "ROLE_VIEW";
	public static final String ROLE_DEACTIVATE = "ROLE_DEACTIVATE";
	public static final String ROLE_REACTIVATE = "ROLE_REACTIVATE";

	/*** Ledger Permissions... ***/
	public static final String LEDGER_ADD = "LEDGER_ADD";
	public static final String LEDGER_UPDATE = "LEDGER_EDIT";
	public static final String LEDGER_VIEW = "LEDGER_VIEW";
	public static final String LEDGER_DELETE = "LEDGER_DELETE";// only if user wants to permanently delete Ledger...
	public static final String LEDGER_DEACTIVATE = "LEDGER_DEACTIVATE";
	public static final String LEDGER_REACTIVATE = "LEDGER_REACTIVATE";

	// Company Branch Permissions
	public static final String COMPANY_BRANCH_VIEW = "COMPANY_BRANCH_VIEW";
	public static final String COMPANY_BRANCH_EDIT = "COMPANY_BRANCH_EDIT";
	public static final String COMPANY_BRANCH_ADD = "COMPANY_BRANCH_ADD";
	public static final String COMPANY_BRANCH_DEACTIVATE = "COMPANY_BRANCH_DEACTIVATE";
	public static final String COMPANY_BRANCH_REACTIVATE = "COMPANY_BRANCH_REACTIVATE";

	// Company Permissions
	public static final String COMPANY_ADD = "COMPANY_ADD";
	public static final String COMPANY_EDIT = "COMPANY_EDIT";
	public static final String COMPANY_VIEW = "COMPANY_VIEW";
	public static final String COMPANY_DEACTIVATE = "COMPANY_DEACTIVATE";
	public static final String COMPANY_REACTIVATE = "COMPANY_REACTIVATE";

	public static final String ROLE_OBJECT_MODULE_ADD = "OBJECT_MODULE_ADD";
	public static final String ROLE_OBJECT_MODULE_UPDATE = "OBJECT_MODULE_UPDATE";
	public static final String ROLE_OBJECT_MODULE_GET = "OBJECT_MODULE_VIEW";
	public static final String ROLE_OBJECT_MODULE_DELETE = "OBJECT_MODULE_DELETE";
	public static final String ROLE_OBJECT_MODULE_GET_ALL = "OBJECT_MODULE_VIEW";
	public static final String DEPARTMENT_ADD = "DEPARTMENT_ADD";
	public static final String DEPARTMENT_EDIT = "DEPARTMENT_EDIT";
	public static final String DEPARTMENT_VIEW = "DEPARTMENT_VIEW";
	public static final String DEPARTMENT_DELETE = "DEPARTMENT_DELETE";

	/* External Entity Permissions.. */
	public static final String EXTERNAL_ENTITY_ADD = "ENTITY_ADD";
	public static final String EXTERNAL_ENTITY_UPDATE = "ENTITY_EDIT";
	public static final String EXTERNAL_ENTITY_VIEW = "ENTITY_VIEW";
	public static final String EXTERNAL_ENTITY_DEACTIVATE = "ENTITY_DEACTIVATE";
	public static final String EXTERNAL_ENTITY_REACTIVATE = "ENTITY_REACTIVATE";

	/* Entity Branch Permissions.. */
	public static final String ENTITY_BRANCH_ADD = "ENTITY_BRANCH_ADD";
	public static final String ENTITY_BRANCH_UPDATE = "ENTITY_BRANCH_EDIT";
	public static final String ENTITY_BRANCH_VIEW = "ENTITY_BRANCH_VIEW";
	public static final String ENTITY_BRANCH_DEACTIVATE = "ENTITY_BRANCH_DEACTIVATE";
	public static final String ENTITY_BRANCH_REACTIVATE = "ENTITY_BRANCH_REACTIVATE";

	/* Destination Permissions.. */
	public static final String DESTINATION_ADD = "DESTINATION_ADD";
	public static final String DESTINATION_EDIT = "DESTINATION_EDIT";
	public static final String DESTINATION_VIEW = "DESTINATION_VIEW";
	public static final String DESTINATION_DELETE = "DESTINATION_DELETE";
	
	// Accounts Group Permissions
    public static final String GROUP_VIEW = "GROUP_VIEW";
    public static final String GROUP_EDIT = "GROUP_EDIT";
    public static final String GROUP_ADD = "GROUP_ADD";
    public static final String GROUP_DELETE = "GROUP_DELETE";
    public static final String GROUP_DEACTIVATE = "GROUP_DEACTIVATE";
    public static final String GROUP_REACTIVATE = "GROUP_REACTIVATE";
    
    //Currency Permissions
    public static final String CURRENCY_ADD = "CURRENCY_ADD";
	public static final String CURRENCY_EDIT = "CURRENCY_EDIT";
	public static final String CURRENCY_VIEW = "CURRENCY_VIEW";
	public static final String CURRENCY_DELETE = "CURRENCY_DELETE";
	
	//Designation Permissions
    public static final String DESIGNATION_ADD = "DESIGNATION_ADD";
	public static final String DESIGNATION_EDIT = "DESIGNATION_EDIT";
	public static final String DESIGNATION_VIEW = "DESIGNATION_VIEW";
	public static final String DESIGNATION_DELETE = "DESIGNATION_DELETE";
	
	//Country Permissions
    public static final String COUNTRY_ADD = "COUNTRY_ADD";
	public static final String COUNTRY_EDIT = "COUNTRY_EDIT";
	public static final String COUNTRY_VIEW = "COUNTRY_VIEW";
	public static final String COUNTRY_DELETE = "COUNTRY_DELETE";
	public static final String COUNTRY_DEACTIVATE = "COUNTRY_DEACTIVATE";
	public static final String COUNTRY_REACTIVATE = "COUNTRY_REACTIVATE";
}
