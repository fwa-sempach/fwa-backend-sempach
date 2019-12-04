package dev.elysion.fwa.enumeration;

public enum ParticipantStatus {
	NEW(""),
	INTERESTED(""),
	ACTIVE(""),
	INACTIVE("");

	private String label;

	ParticipantStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}
}
