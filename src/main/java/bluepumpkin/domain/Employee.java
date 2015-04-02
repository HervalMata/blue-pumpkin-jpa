package bluepumpkin.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import bluepumpkin.domain.Team;
import java.util.List;
import javax.persistence.ManyToMany;

@SuppressWarnings("serial")
@Entity
public class Employee implements Serializable {

	@Id
	private Long id;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String lastName;
	
	@Column(nullable = false)
	private String position;
	
	@Column(nullable = false)
	private String department;
	
	@Column(nullable = true)
	private String telephone;
	
	@Column(nullable = false)
	private LocalDate dateOfBirth;
	
	@OneToOne(optional = false, orphanRemoval = true, cascade = { MERGE, REFRESH })
	@MapsId("id")
	@PrimaryKeyJoinColumn
	private Account account;

	@OneToMany(mappedBy = "employee", orphanRemoval = true, cascade = ALL) 
//	@OrderBy("status ASC")
	private Set<Participation> participations = new HashSet<>();

	@ManyToMany(mappedBy = "employees")
	private Set<Team> teams = new HashSet<>();

	protected Employee() {
	}
	
//	for testing
	public Employee(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Employee(Long id, String firstName, String lastName, String position,
			String department, String telephone, LocalDate dateOfBirth) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.position = position;
		this.department = department;
		this.telephone = telephone;
		this.dateOfBirth = dateOfBirth;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String param) {
		this.firstName = param;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String param) {
		this.lastName = param;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String param) {
		this.position = param;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String param) {
		this.department = param;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String param) {
		this.telephone = param;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate param) {
		this.dateOfBirth = param;
	}

	public Account getAccount() {
	    return account;
	}

	public void setAccount(Account param) {
	    this.account = param;
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

}