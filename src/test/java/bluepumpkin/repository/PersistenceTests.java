package bluepumpkin.repository;

//import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

//import org.hamcrest.Matchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import bluepumpkin.BluePumpkinJpaApplication;
import bluepumpkin.domain.Employee;
import bluepumpkin.domain.Event;
import bluepumpkin.domain.Participation;
import bluepumpkin.domain.ParticipationId;
import bluepumpkin.repository.AccountRepository;
import bluepumpkin.repository.EmployeeRepository;
import bluepumpkin.repository.EventRepository;
import bluepumpkin.repository.ParticipationRepository;
import static bluepumpkin.domain.EventType.*;
import static bluepumpkin.domain.ParticipationStatus.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BluePumpkinJpaApplication.class)
//@WebAppConfiguration
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceTests {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private ParticipationRepository participationRepository;
	
	@Test
	public void _01retrievesEventsByEmail() { 
		List<Event> events = eventRepository
				.findByParticipationsEmployeeAccountEmail("sam.brown@bluepumpkin.com");
		assertThat(events.size()).isEqualTo(3);
//		checks participations of earliest event
		List<Participation> eventParticipations = new ArrayList<Participation>(events.get(0).getParticipations());
		assertThat(eventParticipations.size()).isEqualTo(1);
		assertThat(eventParticipations.get(0).getEmployee().getAccount().getEmail())
				.isEqualTo("sam.brown@bluepumpkin.com");
		assertThat(eventParticipations.get(0).getEmployee().getDateOfBirth()).isEqualTo(LocalDate.of(1974, 5, 27));
		assertThat(eventParticipations.get(0).getEvent().getName()).isEqualTo("Floorball");
	}
	
	@Test
	public void _02retrievesEventsEmployeeNotParticipatingIn() {	
		assertThat(eventRepository.findByParticipationsEmployeeIdNotIn(1L).size()).isEqualTo(1);
		assertThat(eventRepository.findByParticipationsEmployeeIdNotIn(1L).get(0).getName()).isEqualTo("Sightseeing Trip");
	}
	
	@Test
	public void _03insertsNewParticipationByEmployee() {
		Employee emp = employeeRepository.findOne(1L);
		assertThat(emp.getParticipations()).hasSize(3);
		
		Event event = new Event(TRIP, "Sightseeing Trip", "Blue Lake", LocalDateTime.of(2015, 7, 1, 9, 30), "");
		event.setId(4L);
//		Participation participation = new Participation(emp.getId(), event.getId(), WAITING);
		Participation participation = new Participation(1L, 4L, WAITING, emp, event);
		
		emp.getParticipations().add(participation);
		emp = employeeRepository.save(emp);
		assertThat(emp.getParticipations()).hasSize(4);
		
		assertThat(participationRepository.findAll()).hasSize(5);
		participation = participationRepository.findOne(new ParticipationId(1L, 4L));	
		assertThat(participation.getEmployee().getFirstName()).isEqualTo("Sam");
		assertThat(participation.getEvent().getName()).isEqualTo("Sightseeing Trip");
	}
	
	@Test
	public void _04insertedParticipationRolledBack() {
		assertThat(participationRepository.findAll()).hasSize(4);
		assertThat(employeeRepository.findOne(1L).getParticipations()).hasSize(3);
	}
	
	@Test
	public void _05updatesEmailByEmployee() {
		Employee emp = employeeRepository.findOne(1L);
		emp.getAccount().setEmail("s.brown@bluepumpkin.com");
		employeeRepository.save(emp);
		assertThat(accountRepository.findOne(1L).getEmail()).isEqualTo("s.brown@bluepumpkin.com");
	}

	@Test
	public void _06deletesEmployeeAndItsAssociations() {
		Employee emp = employeeRepository.findOne(1L);
		employeeRepository.delete(emp);
		assertThat(participationRepository.findAll()).hasSize(1);
		assertThat(participationRepository.findAll().get(0).getEmployee().getFirstName()).isEqualTo("Barry");
	}
	
}
