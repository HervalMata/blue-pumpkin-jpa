package bluepumpkin.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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
	
	@Autowired
	public CommonService(final EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
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
	
}
