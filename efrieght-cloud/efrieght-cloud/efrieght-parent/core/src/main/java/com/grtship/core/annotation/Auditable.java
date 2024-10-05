/**
 * 
 */
package com.grtship.core.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lombok.Getter;

@Retention(RUNTIME)
@Target({METHOD})
/**
 * @author jazzc
 *
 */
public @interface Auditable {

	ActionType action()

	default ActionType.SAVE;

	Module module() default Module.CLIENT;

	public enum ActionType {
		SAVE("SAVE"), UPDATE("UPDATE"), DELETE("DELETE"), DEACTIVATE("DEACTIVATE"), REACTIVATE("REACTIVATE"),
		ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), DISABLE("DISABLE"), ENABLE("ENABLE"), REGISTER("REGISTER");

		@Getter
		private String key;

		@Getter
		private String label;

		ActionType(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}

	public enum Module {
		CLIENT("CLIENT"), COMPANY("COMPANY"), COMPANY_BRANCH("COMPANY_BRANCH"), DESTINATION("DESTINATION"),
		DESIGNATION("DESIGNATION"), GROUP("GROUP"), LEDGER("LEDGER"), BANK("BANK"), GST("GST"), DOCUMENT("DOCUMENT"),
		TDS("TDS"), USER("USER"), ENTITY("ENTITY"), ENTITY_BRANCH("ENTITY_BRANCH"),
		ENTITY_BRANCH_TAX("ENTITY_BRANCH_TAX"), ENTITY_CREDIT_TERMS("ENTITY_CREDIT_TERMS"), COUNTRY("COUNTRY"),
		CURRENCY("CURRENCY"), DEPARTMENT("DEPARTMENT"), BANK_CREDIT_TERMS("BANK_CREDIT_TERMS"),
		CARGO_CARTING("CARGO_CARTING"), CARGO_READY("CARGO_READY"), ROLE("ROLE"), PERMISSION("PERMISSION"),
		BANK_RECEIPT("BANK_RECEIPT"), INVOICE("INVOICE"), INVOICE_TRANSACTION("INVOICE_TRANSACTION"),
		OBJECT_ALIAS("OBJECT_ALIAS"), SHIPMENT_REFERENCE("SHIPMENT_REFERENCE"), ADDRESS("ADDRESS"),
		CONTAINER("CONTAINER"), CONTAINER_PACKAGE("CONTAINER_PACKAGE"), CREDIT_TERMS("CREDIT_TERMS"),
		DOCUMENT_STORAGE("DOCUMENT_STORAGE"), ENTITY_BRANCH_CONTACT("ENTITY_BRANCH_CONTACT"),
		ENTITY_GROUP("ENTITY_GROUP"), EQUIPMENT("EQUIPMENT"), EQUIPMENT_SIZE("EQUIPMENT_SIZE"),
		EXTERNAL_ENTITY("EXTERNAL_ENTITY"), MODULE("MODULE"), OBJECT_CODE("OBJECT_CODE"),
		OBJECT_MODULE("OBJECT_MODULE"), SECTOR("SECTOR"), STATE("STATE"), UNIT("UNIT"), VESSEL("VESSEL");

		@Getter
		private String key;

		@Getter
		private String label;

		Module(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}
}
