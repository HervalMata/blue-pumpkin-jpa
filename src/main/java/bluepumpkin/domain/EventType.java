package bluepumpkin.domain;

/**
 * Available types for events.
 */
public enum EventType {

	MEETING("Meeting"),
	TRAINING("Training"),
	SPORTSEVENT("Sports Event"),
	TRIP("Trip");
	
	private String displayName;

	EventType(String displayName) {
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
