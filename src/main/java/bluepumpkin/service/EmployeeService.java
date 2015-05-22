package bluepumpkin.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.collectingAndThen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bluepumpkin.domain.Employee;
import bluepumpkin.domain.EmployeeNotFoundException;
import bluepumpkin.domain.Event;
import bluepumpkin.domain.Participation;
import bluepumpkin.domain.ParticipationId;
import bluepumpkin.domain.Team;
import bluepumpkin.repository.EmployeeRepository;
import bluepumpkin.repository.EventRepository;
import bluepumpkin.repository.ParticipationRepository;
import static bluepumpkin.domain.EventType.*;
import static bluepumpkin.domain.ParticipationStatus.*;

@Service
public class EmployeeService {
	
	private final EmployeeRepository employeeRepository;
	
	private final EventRepository eventRepository;
	
	private final ParticipationRepository participationRepository;
	
	@Autowired
	public EmployeeService(final EmployeeRepository employeeRepository,
			final EventRepository eventRepository, final ParticipationRepository participationRepository) {
		this.employeeRepository = employeeRepository;
		this.eventRepository = eventRepository;
		this.participationRepository = participationRepository;
	}
	
	public void validateEmployee(Long empId) {
		this.employeeRepository.findById(empId)
				.orElseThrow(() -> new EmployeeNotFoundException(empId));
	}
	public Employee validateAndGetEmployee(Long empId) {
		return this.employeeRepository.findById(empId)
				.orElseThrow(() -> new EmployeeNotFoundException(empId));
	}
	
	public Employee findByEmail(String email) {
		return employeeRepository.findByAccountEmail(email);
	}
	
	public List<Participation> getFutureSortedParticipations(Employee emp) {
		List<Participation> participations = emp.getParticipations().stream()
				.filter(p -> p.getEvent().getDateTime().compareTo(LocalDateTime.now()) > 0)
				.sorted((p1, p2) -> p1.getEvent().getDateTime()
						.compareTo(p2.getEvent().getDateTime()))
				.collect(collectingAndThen(toList(), this::convertEventDateTimesToDateType)); 
		return participations;
	}
	private List<Participation> convertEventDateTimesToDateType(List<Participation> participations) {
		participations.forEach(p -> p.getEvent().convertToDateType());
		return participations;
	}
	
	public Event getLatestSportsEvent() {
		return eventRepository.findByType(SPORTSEVENT).stream()
			.filter(e -> { 
				List<Team> teams = new ArrayList<>(e.getTeams());
				if (teams.isEmpty()) return false;
				return teams.get(0).getScore() != null && teams.get(1).getScore() != null; 
			})
			.max((e1,e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
			.map(e -> {
				e.convertToDateType();
				return e;
			})
			.orElse(null);
	}

	public void createParticipationRequest(Long eventId, Long empId) {
//		TODO if exists, state must not be 'denied'. If doesn't exists, event must exists and empId must be equal
		participationRepository.save(new Participation(empId, eventId, WAITING));
	}

	public void deleteParticipationRequest(Long eventId, Long empId) {
//		TODO if exists and state is not 'denied' and empIds are equal
		participationRepository.delete(new ParticipationId(empId, eventId));
	}

	public List<Participation> getUpcomingEvents(Employee employee) {	
		if (employee.getParticipations().isEmpty()) {
			return transformUpcomingEventsToParticipations(employee);
		}
		
		LocalDateTime now = LocalDateTime.now();
		
		List<Participation> upcParticips = employee.getParticipations().stream()
				.filter(p -> p.getEvent().getDateTime().compareTo(now) > 0)
				.collect(toList());
		
		List<Event> upcEvents = eventRepository.findAll().stream()
			.filter(e -> e.getDateTime().compareTo(now) > 0)
			.collect(toList());
		
//		boolean requested = false;
//		
//		for (Event e : upcEvents) {
//			for (Participation p : upcParticips) {
//				if (e.getId().equals(p.getEventId())) {
//					requested = true;
//					break;
//				}	
//			}
//			if (requested == false)
//				upcParticips.add(new Participation(null, e.getId(), NOTREQUESTED, null, e));
//			else
//				requested = false;
//		}
		
		List<Long> eventIdsOfUpcParticips = upcParticips.stream()
			.map(p -> p.getEventId())
			.collect(toList());
		for (Event e : upcEvents) {
			if (!eventIdsOfUpcParticips.contains(e.getId()))
				upcParticips.add(new Participation(null, e.getId(), NOTREQUESTED, null, e));
		}
		
		upcParticips = upcParticips.stream()
			.sorted((p1, p2) -> p1.getEvent().getDateTime().compareTo(p2.getEvent().getDateTime()))
			.collect(toList());
		upcParticips.forEach(p -> p.getEvent().convertToDateType());
		
//		all upcoming events with employee's participation requests 
		return upcParticips;
	}
	private List<Participation> transformUpcomingEventsToParticipations(Employee employee) {
		List<Participation> upcEventsAsParticips = new ArrayList<>();
		
		eventRepository.findAll().stream()
		.filter(e -> e.getDateTime().compareTo(LocalDateTime.now()) > 0)
		.sorted((e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
		.forEach(event -> {
			event.convertToDateType();
			upcEventsAsParticips.add(new Participation(null, event.getId(), NOTREQUESTED, null, event));
		});
		return upcEventsAsParticips;
	}
	
}
