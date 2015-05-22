package bluepumpkin.service;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import bluepumpkin.domain.Employee;
import bluepumpkin.domain.Event;
import bluepumpkin.domain.web.Birthday;
import bluepumpkin.repository.EmployeeRepository;
import bluepumpkin.repository.EventRepository;

@Service
public class CommonService {

	private final EmployeeRepository employeeRepository;
	private final EventRepository eventRepository;
	
	@Autowired
	public CommonService(final EmployeeRepository employeeRepository,
			final EventRepository eventRepository) {
		this.employeeRepository = employeeRepository;
		this.eventRepository = eventRepository;
	}
	
	public List<Birthday> getBirthdays() {
		List<Employee> employees = employeeRepository.findAll();
		List<Birthday> birthdays = new ArrayList<>();
		
		for (Employee e : employees) {
			LocalDate birth = e.getDateOfBirth();
			LocalDate now = LocalDate.now();
			
			if (birth.getDayOfMonth() == now.getDayOfMonth() &&
					birth.getMonthValue() == now.getMonthValue()) {
				
				birthdays.add(new Birthday(e.getFirstName(), e.getLastName(), e.getPosition(),
						e.getDepartment(), now.getYear() - birth.getYear()));
			}
		}
		return birthdays;
	}
	
	public List<Event> getPastEvents() {
		List<Event> pastEvents = eventRepository.findAll().stream()
			.filter(e -> e.getDateTime().compareTo(LocalDateTime.now()) < 0)
			.collect(collectingAndThen(toList(), CommonService::sortEventsByDateTime));
		Collections.reverse(pastEvents);
		return pastEvents;
	}
	static List<Event> sortEventsByDateTime(List<Event> events) {
		return events.stream()
			.sorted((e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
			.collect(collectingAndThen(toList(), CommonService::convertEventDateTimesToDateType));	
	}
	private static List<Event> convertEventDateTimesToDateType(List<Event> events) {
		events.forEach(e -> e.convertToDateType());
		return events;
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
