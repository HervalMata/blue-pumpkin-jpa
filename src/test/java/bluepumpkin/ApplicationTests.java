package bluepumpkin;

import javax.transaction.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import bluepumpkin.domain.Account;
import bluepumpkin.domain.Employee;
import bluepumpkin.repository.AccountRepository;
import bluepumpkin.repository.EmployeeRepository;
import bluepumpkin.repository.TeamRepository;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BluePumpkinJpaApplication.class)
//@WebAppConfiguration
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTests {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private TeamRepository teamRepository;
	
	@Test
	public void _01contextLoads() {
		
		Account account = new Account("sam.brown@bluepumpkin.com");

		Employee emp = new Employee("Sam", "Brown");
		emp.setAccount(account);
		
		assertThat(accountRepository.count()).isEqualTo(3);
		
		assertThat(employeeRepository.count()).isEqualTo(2);
		assertThat(employeeRepository.findOne(1L)).isEqualToComparingOnlyGivenFields(
				emp, "firstName", "lastName", "account.email");
	}
	
	@Test
	public void _02accountModifiedByReferencingEmployee() {
		Employee emp = employeeRepository.findOne(1L);
		Account empAccount = emp.getAccount();
		empAccount.setEmail("test@bp.com");
		empAccount.setPassword("test");
		emp.setAccount(empAccount);
		employeeRepository.save(emp);
		Account account = accountRepository.findOne(1L);
		
		assertThat(account).isEqualToComparingFieldByField(empAccount);
	}
	
	@Test
	public void _03deletesAccountWhenDeletedReferencingEmployee() {
		Employee emp = employeeRepository.findOne(1L);
		emp.getTeams().forEach(t -> {
			t.getEmployees().remove(emp);
			teamRepository.save(t);
		});
		
		employeeRepository.delete(1L);
		
		assertThat(accountRepository.findOne(1L)).isNull();
	}
}
