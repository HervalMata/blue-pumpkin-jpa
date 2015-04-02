package bluepumpkin;

import static bluepumpkin.domain.EventType.*;
import static bluepumpkin.domain.ParticipationStatus.WAITING;
import static bluepumpkin.domain.Account.ROLE_ADMIN;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bluepumpkin.domain.Account;
import bluepumpkin.domain.Employee;
import bluepumpkin.domain.Event;
import bluepumpkin.domain.EventType;
import bluepumpkin.domain.Participation;
import bluepumpkin.domain.Team;
import bluepumpkin.repository.AccountRepository;
import bluepumpkin.repository.EmployeeRepository;
import bluepumpkin.repository.EventRepository;
import bluepumpkin.repository.ParticipationRepository;
import bluepumpkin.repository.TeamRepository;
import bluepumpkin.service.AccountService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;

@SpringBootApplication
public class BluePumpkinJpaApplication {

	@Bean
	CommandLineRunner init(AccountService accountService, EmployeeRepository employeeRepository,
			EventRepository eventRepository, ParticipationRepository participationRepository,
			TeamRepository teamRepository) {
		return (evt) -> {
			final String ac = "Accountant";
			final String pr = "Programmer";
			
			final String ac_d = "Accounting Department";
			final String it_d = "IT Department";
			
			final List<String> emails = Arrays.asList("sam.brown@bluepumpkin.com,barry.firefly@bluepumpkin.com".split(","));
			
			final List<String> firstNames = Arrays.asList("Sam,Barry".split(","));
			final List<String> lastNames = Arrays.asList("Brown,Firefly".split(","));
			final List<String> positions = Arrays.asList(pr,ac);
			final List<String> departments = Arrays.asList(it_d,ac_d);
			final List<String> telephoneNumbers = Arrays.asList("+191 708 679 497,+191 708 654 343".split(","));
			final List<LocalDate> datesOfBirth = Arrays.asList(
					LocalDate.of(1974, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()),
					LocalDate.of(1976, 9, 16));
			
			List<EventType> types = Arrays.asList(SPORTSEVENT,MEETING,TRAINING,TRIP,SPORTSEVENT,SPORTSEVENT);
			List<String> names = Arrays.asList("Floorball,Annual Meeting,Microservices Training,Sightseeing Trip,Football,Baseball".split(","));
			List<String> places = Arrays.asList("Springfield Geneseo,Meeting Room,Conference Hall,Blue Lake,Near Stadium,Outside Premises".split(","));
			List<Integer> months = Arrays.asList(3,5,6,7,2,8);
			
			List<Account> accounts = new ArrayList<>();
			List<Employee> employees = new ArrayList<>();
			List<Event> events = new ArrayList<>();
			List<Participation> participations = new ArrayList<>();
			
			IntStream.range(0, emails.size())
			.forEach(idx -> {
				accounts.add(accountService.save(new Account(emails.get(idx), "user")));
			});
			accountService.save(new Account("admin", "admin", ROLE_ADMIN));
			
			IntStream.range(0, firstNames.size())
			.forEach(idx -> {
				employees.add(employeeRepository.save(new Employee(accounts.get(idx).getId(),
						firstNames.get(idx), lastNames.get(idx), positions.get(idx), departments.get(idx),
						telephoneNumbers.get(idx), datesOfBirth.get(idx))));
			});
			
			IntStream.range(0, types.size())
			.forEach(idx -> {
				events.add(eventRepository.save(new Event(types.get(idx), names.get(idx), 
						places.get(idx), LocalDateTime.of(2015, months.get(idx), 1, 9, 30), null)));
			});
		
			IntStream.rangeClosed(0, 2)
			.forEach(idx -> {
				participations.add(participationRepository.save(
						new Participation(employees.get(0).getId(), events.get(idx).getId(), WAITING)));
			});
			
			participations.add(participationRepository.save(
					new Participation(employees.get(1).getId(), events.get(3).getId(), WAITING)));
			
			IntStream.rangeClosed(0, 1)
			.forEach(i -> {
				teamRepository.save(new Team(events.get(0), new HashSet<>(Arrays.asList(employees.get(i))), 10));
				teamRepository.save(new Team(events.get(4), new HashSet<>(Arrays.asList(employees.get(i))), 4));
				teamRepository.save(new Team(events.get(5), new HashSet<>(Arrays.asList(employees.get(i))), null));
			});
		};
	}
	
    public static void main(String[] args) {
//    	Collections.
        SpringApplication.run(BluePumpkinJpaApplication.class, args);
    }
}

@Configuration
class WebConfigurer implements ServletContextInitializer {
	private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		initH2Console(servletContext);
	}

	/**
	 * Initializes the H2 console.
	 *
	 * @param servletContext
	 *         the servlet context of the application
	 */
	private void initH2Console(final ServletContext servletContext) {
		log.debug("Initialize H2 console");
		final org.h2.server.web.WebServlet h2Servlet = new org.h2.server.web.WebServlet();
		final ServletRegistration.Dynamic h2ConsoleServlet = servletContext
				.addServlet("H2Console", h2Servlet);
		h2ConsoleServlet.addMapping("/dbconsole/*");
		h2ConsoleServlet.setLoadOnStartup(1);
		h2ConsoleServlet.setInitParameter("webAllowOthers",
				Boolean.TRUE.toString());
	}
}
