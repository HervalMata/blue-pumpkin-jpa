package bluepumpkin.domain;

import static javax.persistence.EnumType.STRING;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrimaryKeyJoinColumn;

@SuppressWarnings("serial")
@Entity
@IdClass(ParticipationId.class)
public class Participation implements Serializable {

	@Id
	@Column(name = "employee_id")
	private Long employeeId;

	@Id
	@Column(name = "event_id")
	private Long eventId;
	
	@Column(nullable = false)
	@Enumerated(STRING)
	private ParticipationStatus status;

	@ManyToOne(optional = false)
	@JoinColumn(name = "employee_id", referencedColumnName = "id", 
	nullable = false, insertable=false, updatable=false)
	private Employee employee;

	@ManyToOne(optional = false)
	@JoinColumn(name = "event_id", referencedColumnName = "id",
	nullable = false, insertable=false, updatable=false)
	private Event event;

	protected Participation() {
	}
	
	public Participation(Long employeeId, Long eventId, ParticipationStatus status) {
		this.employeeId = employeeId;
		this.eventId = eventId;
		this.status = status;
	}

	public Participation(Long employeeId, Long eventId,
			ParticipationStatus status, Employee employee, Event event) {
		this.employeeId = employeeId;
		this.eventId = eventId;
		this.status = status;
		this.employee = employee;
		this.event = event;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long param) {
		this.employeeId = param;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long param) {
		this.eventId = param;
	}
	
	public ParticipationStatus getStatus() {
		return this.status;
	}

	public void setStatus(ParticipationStatus status) {
		this.status = status;
	}

	public Employee getEmployee() {
		return employee;
	}
	
	public Event getEvent() {
		return event;
	}

}
