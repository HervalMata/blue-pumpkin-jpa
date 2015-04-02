package bluepumpkin.controller;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.*;





import java.util.List;

import org.assertj.core.internal.Throwables;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;

import bluepumpkin.BluePumpkinJpaApplication;
import bluepumpkin.domain.EmployeeNotFoundException;
import bluepumpkin.domain.Participation;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BluePumpkinJpaApplication.class)
//@WebAppConfiguration
@Transactional
public class EmployeeControllerTests {

//	@Rule
//	public ExpectedException thrown = ExpectedException.none();
	
	private ExtendedModelMap model = new ExtendedModelMap();
	
	@Autowired
	private EmployeeController empController;
	
	@Test
	public void employeeHome() {
		String view = empController.employeeHome(model, 1L);
		List<Participation> participations = (List<Participation>) model.get("participations");
		assertThat(participations).hasSize(2);
		assertThat(participations.get(0).getEvent().getName()).isEqualTo("Annual Meeting");
		assertThat(view).isEqualTo("employee/home");
	}
	
	@Test//(expected = EmployeeNotFoundException.class)
	public void employeeHome_throwsEmployeeNotFoundException() {
		
//		thrown.expectMessage(Matchers.equalTo("Could not find employee '" + 2L + "'."));
//		empController.employeeHome(model, 2L);
		
		Throwable throwable = ThrowableCaptor.captureThrowable(() -> empController.employeeHome(model, 20L));
		assertThat(throwable)
			//.isNotNull()
			.isInstanceOf(EmployeeNotFoundException.class)
			.hasMessage("Could not find employee '" + 20L + "'.");
	}

}
