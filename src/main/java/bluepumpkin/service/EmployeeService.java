package bluepumpkin.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bluepumpkin.domain.Employee;
import bluepumpkin.domain.EmployeeNotFoundException;
import bluepumpkin.domain.Event;
import bluepumpkin.domain.Participation;
import bluepumpkin.domain.Team;
import bluepumpkin.repository.EmployeeRepository;
import bluepumpkin.repository.EventRepository;
import static bluepumpkin.domain.EventType.*;

@Service
public class EmployeeService {
	
	private final EmployeeRepository employeeRepository;
	
	private final EventRepository eventRepository;
	
	@Autowired
	public EmployeeService(final EmployeeRepository employeeRepository,
			final EventRepository eventRepository) {
		this.employeeRepository = employeeRepository;
		this.eventRepository = eventRepository;
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
				.collect(Collectors.toList()); 
		
		convertEventDateTimesToDateType(participations);
		
		return participations;
	}
	
	private void convertEventDateTimesToDateType(List<Participation> participations) {
		participations.forEach(p -> p.getEvent().convertToDateType());
	}
	
	public Event getLatestSportsEvent() {
		return eventRepository.findByType(SPORTSEVENT).stream()
			.filter(e -> { 
				List<Team> teams = new ArrayList<>(e.getTeams());
				return teams.get(0).getScore() != null && teams.get(1).getScore() != null; 
			})
			.max((e1,e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
			.map(e -> {
				e.convertToDateType();
				return e;
			})
			.orElse(null);
	}
	
}
