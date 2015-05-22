package bluepumpkin.domain;

/**
 * Available statuses for participations.
 */
public enum ParticipationStatus {

	NOTREQUESTED(""),
	WAITING("Waiting"),
	APPROVED("Approved"),
	DENIED("Denied");

	private String displayName;

	ParticipationStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
        return name();
    }
	
	@Override
    public String toString() {
        return getDisplayName();
    }
	
}
