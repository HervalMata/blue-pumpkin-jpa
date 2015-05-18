package bluepumpkin.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import bluepumpkin.domain.Employee;
import bluepumpkin.domain.Event;
import bluepumpkin.domain.Participation;
import bluepumpkin.domain.ParticipationId;
import bluepumpkin.domain.ParticipationStatus;
import bluepumpkin.repository.EmployeeRepository;
import bluepumpkin.repository.EventRepository;
import bluepumpkin.repository.ParticipationRepository;
import static bluepumpkin.domain.ParticipationStatus.*;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Service
public class AdminService {

	private final ParticipationRepository participationRepository;
	private final EventRepository eventRepository;
	private final EmployeeRepository employeeRepository;
	
	@Autowired
	public AdminService(ParticipationRepository participationRepository,
			EventRepository eventRepository, EmployeeRepository employeeRepository) {
		this.participationRepository = participationRepository;
		this.eventRepository = eventRepository;
		this.employeeRepository = employeeRepository;
	}

	public List<Participation> getFutureWaitingParticipations() {
		List<Participation> participations = participationRepository.findByStatus(WAITING).stream()
		.filter(p -> p.getEvent().getDateTime().compareTo(LocalDateTime.now()) > 0)
		.collect(collectingAndThen(toList(), this::convertPartEventDateTimesToDateType));
		Collections.reverse(participations);
		return participations;
	}
	private List<Participation> convertPartEventDateTimesToDateType(List<Participation> participations) {
		participations.forEach(p -> p.getEvent().convertToDateType());
		return participations;
	}
	
	public void changeParticipationStatus(Long eventId, Long empId, ParticipationStatus status) {
		ParticipationId id = new ParticipationId(empId, eventId);
		Participation p = participationRepository.findOne(id);
		p.setStatus(status);
		participationRepository.save(p);
	}

	public List<Event> getUpcomingEvents() {
		return eventRepository.findAll().stream()
			.filter(e -> e.getDateTime().compareTo(LocalDateTime.now()) > 0)
			.collect(collectingAndThen(toList(), this::sortEventsByDateTime));
	}
	private List<Event> sortEventsByDateTime(List<Event> events) {
		return events.stream()
			.sorted((e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
			.collect(collectingAndThen(toList(), this::convertEventDateTimesToDateType));	
	}
	private List<Event> convertEventDateTimesToDateType(List<Event> events) {
		events.forEach(e -> e.convertToDateType());
		return events;
	}

	public void createEvent(Event event) {
		event.convertToLocalDateTimeType();
		eventRepository.save(event);
	}

	public Event getEvent(Long eventId) {
		Event event = eventRepository.findOne(eventId);
		event.convertToDateType();
		return event;
	}

	public void updateEvent(Event event) {
		event.convertToLocalDateTimeType();
		eventRepository.save(event);
	}

	public void deleteEvent(Long id) {
		eventRepository.delete(id);
	}

	public List<Event> getPastEvents() {
		List<Event> pastEvents = eventRepository.findAll().stream()
			.filter(e -> e.getDateTime().compareTo(LocalDateTime.now()) < 0)
			.collect(collectingAndThen(toList(), this::sortEventsByDateTime));
		Collections.reverse(pastEvents);
		return pastEvents;
	}

	public List<Employee> getAccounts() {
		Comparator<Employee> byLastName = (c1, c2) -> c1.getLastName()
	            .compareTo(c2.getLastName());
		Comparator<Employee> byDepartment = (c1, c2) -> c1.getDepartment()
	            .compareTo(c2.getDepartment());
		return employeeRepository.findAll().stream()
				.sorted(byDepartment.thenComparing(byLastName))
				.collect(collectingAndThen(toList(), this::convertDatesOfBirthToDateType));
	}
	private List<Employee> convertDatesOfBirthToDateType(List<Employee> employees) {
		employees.forEach(e -> e.convertToDateType());
		return employees;
	}

}
