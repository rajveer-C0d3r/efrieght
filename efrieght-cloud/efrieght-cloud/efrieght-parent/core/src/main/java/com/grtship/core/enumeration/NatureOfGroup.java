package com.grtship.core.enumeration;

/**
 * The NatureOfGroup enumeration.
 */
public enum NatureOfGroup {
    INCOME("Income"),
    EXPENSE("Expense"),
    ASSETS("Assets"),
    LIABILITIES("Liabilities");

    private String key;
    
    private String label;

    NatureOfGroup(String label) {
        this.label = label;
    }

    public String getKey() {
        return key;
    }
    
    public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return this.label;
	}
}
