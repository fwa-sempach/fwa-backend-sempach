package dev.elysion.fwa.enumeration;

public enum DocumentType {
	INFO(""),
	ORG(""),
	ADMIN("");

	private String label;

	DocumentType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}
}
