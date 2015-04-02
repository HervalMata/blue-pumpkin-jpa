package bluepumpkin.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ParticipationId implements Serializable {
	
	private Long employeeId;
	private Long eventId;

	public ParticipationId() {
	}
	public ParticipationId(Long employeeId, Long eventId) {
		this.employeeId = employeeId;
		this.eventId = eventId;
	}
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public Long getEventId() {
		return eventId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((employeeId == null) ? 0 : employeeId.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParticipationId other = (ParticipationId) obj;
		if (employeeId == null) {
			if (other.employeeId != null)
				return false;
		} else if (!employeeId.equals(other.employeeId))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		return true;
	}
	
}
