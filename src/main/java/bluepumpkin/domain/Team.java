package bluepumpkin.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import bluepumpkin.domain.Employee;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

@SuppressWarnings("serial")
@Entity
public class Team implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "event_id", 
		referencedColumnName = "id", nullable = false)
	private Event event;

	@ManyToMany
	@JoinTable(name = "Team_Employee", 
		joinColumns = @JoinColumn(name = "team_id", 
			referencedColumnName = "id", nullable = false), 
		inverseJoinColumns = @JoinColumn(name = "employee_id", 
			referencedColumnName = "id", nullable = false))
	private Set<Employee> employees = new HashSet<>();
	
	@Column(nullable = true)
	private Integer score;
	
	protected Team() {
	}
	
	public Team(Event event, Set<Employee> employees, Integer score) {
		this.event = event;
		this.employees = employees;
		this.score = score;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event param) {
		this.event = param;
	}

	public Set<Employee> getEmployees() {
	    return employees;
	}

	public void setEmployees(Set<Employee> param) {
	    this.employees = param;
	}
	
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer param) {
		this.score = param;
	}

	@Override
	public String toString() {
		return "Team [event=" + event.getName() + ", number of employees=" + employees.size() + ", score="
				+ score + "]";
	}

}