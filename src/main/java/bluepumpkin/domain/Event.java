package bluepumpkin.domain;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

@SuppressWarnings("serial")
@Entity
public class Event implements Serializable {
	
	private static final String NOT_BLANK_MESSAGE = "The value may not be empty!";

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(STRING)
	private EventType type;

	@NotBlank(message = Event.NOT_BLANK_MESSAGE)
	@Column(nullable = false)
	private String name;

    @NotBlank(message = Event.NOT_BLANK_MESSAGE)
	@Column(nullable = false)
	private String place;

	@Column(nullable = false)
	private LocalDateTime dateTime;

	@Column(nullable = true)
	private String description;

	@OneToMany(mappedBy = "event", orphanRemoval = true)
	private Set<Participation> participations = new HashSet<>();
	
	@OneToMany(mappedBy = "event", orphanRemoval = true)
	private Set<Team> teams = new HashSet<>();
	
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @Future(message = "The value \"${formatter.format('%1$td-%1$tm-%1$tY', validatedValue)}\" is not in future!")
	@Transient
	private Date convertedDateTime;

	public Event() {
	}
	
	// for testing
	public Event(String name, LocalDateTime dateTime) {
		this.name = name;
		this.dateTime = dateTime;
	}

	public Event(EventType type, String name, String place,
			LocalDateTime dateTime, String description) {
		this.type = type;
		this.name = name;
		this.place = place;
		this.dateTime = dateTime;
		this.description = description;
	}

	public void convertToDateType() {
		this.convertedDateTime = Date.from(this.dateTime
				.atZone(ZoneId.systemDefault())
				.toInstant());
	}
	
	public void convertToLocalDateTimeType() {
		this.dateTime = LocalDateTime.from(this.convertedDateTime.toInstant()
				.atZone(ZoneId.systemDefault()));
	}
	
	public Date getConvertedDateTime() {
		return this.convertedDateTime;
	}
	
	public void setConvertedDateTime(Date param) {
		this.convertedDateTime = param;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long param) {
		this.id = param;
	}
	
	public EventType getType() {
		return type;
	}

	public void setType(EventType param) {
		this.type = param;
	}

	public String getName() {
		return name;
	}

	public void setName(String param) {
		this.name = param;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String param) {
		this.place = param;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String param) {
		this.description = param;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime param) {
		this.dateTime = param;
	}

	public Set<Participation> getParticipations() {
		return participations;
	}

	public void setParticipations(Set<Participation> param) {
		this.participations = param;
	}
	
	public Set<Team> getTeams() {
		return teams;
	}

	public void setTeams(Set<Team> teams) {
		this.teams = teams;
	}
	
	@Override
	public String toString() {
		return "Event [name=" + name + ", place=" + place + ", dateTime="
				+ dateTime + "]";
	}
	
}