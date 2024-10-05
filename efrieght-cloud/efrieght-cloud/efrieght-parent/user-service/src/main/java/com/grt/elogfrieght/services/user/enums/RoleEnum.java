package com.grt.elogfrieght.services.user.enums;

public enum RoleEnum {

	CLIENT(0, "CLIENT"), ADMIN(1, "ADMIN");

	private final int id;
	private final String name;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	private RoleEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static RoleEnum fromInt(int id) {
		switch (id) {
		case 0:
			return CLIENT;
		case 1:
			return ADMIN;

		default:
			return null;
		}
	}

}
