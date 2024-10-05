/**
 * 
 */
package com.grtship.core.enumeration;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ajay
 *
 */
public enum ClassName {
	PRIMITIVE_BYTE(1, "byte"), BYTE(2, "class java.lang.Byte"), PRIMITIVE_INTEGER(3, "int"),
	INTEGER(4, "class java.lang.Integer"), STRING(5, "class java.lang.String"), PRIMITIVE_SHORT(6, "short"),
	SHORT(7, "class java.lang.Short"), PRIMITIVE_LONG(8, "long"), LONG(9, "class java.lang.Long"),
	PRIMITIVE_DOUBLE(10, "double"), DOUBLE(11, "class java.lang.Double"), PRIMITIVE_FLOAT(12, "float"),
	FLOAT(13, "class java.lang.Float"), UIID(15, "class java.util.UUID"), PRIMITIVE_BOOLEAN(16, "boolean"),
	BOOLEAN(17, "class java.lang.Boolean"), ENUM(18, "class java.lang.Enum");

	ClassName(int id, String name) {
		this.id = id;
		this.name = name;

	}

	public static List<String> getClassTypeNames() {
		return Arrays.asList(PRIMITIVE_BYTE.getName(), PRIMITIVE_DOUBLE.getName(), PRIMITIVE_FLOAT.getName(),
				PRIMITIVE_INTEGER.getName(), PRIMITIVE_LONG.getName(), PRIMITIVE_SHORT.getName(), BYTE.getName(),
				DOUBLE.getName(), FLOAT.getName(), STRING.getName(), SHORT.getName(), LONG.getName(), INTEGER.getName(),
				UIID.getName(), PRIMITIVE_BOOLEAN.getName(), BOOLEAN.getName(), ENUM.getName());
	}

	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
