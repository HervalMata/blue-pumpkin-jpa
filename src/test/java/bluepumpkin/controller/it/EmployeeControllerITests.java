package bluepumpkin.controller.it;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import bluepumpkin.BluePumpkinJpaApplication;
import bluepumpkin.controller.EmployeeController;
import bluepumpkin.controller.ThrowableCaptor;
import bluepumpkin.domain.EmployeeNotFoundException;
import bluepumpkin.domain.Participation;
import bluepumpkin.domain.web.Birthday;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BluePumpkinJpaApplication.class)
@WebAppConfiguration
//@Transactional
public class EmployeeControllerITests {
	
	private static final int YEAR_FUTURE = 2025;
	private static final long EMP_ID = 1L;
	private static final String EMP_NAME = "Sam";
	private static final long MEETING_ID = 2L;
	private static final String MEETING_NAME = "Annual Meeting";
	private static final String TRAINING_NAME = "Microservices Training";
	private static final int UPC_EVENTS_SIZE = 4;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	private MockMvc mockMvc;
	
	private MvcResult mvcResult;
	
	@Before
	public void setUp() {
		this.mockMvc = webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.defaultRequest(get("/")
						.with(csrf())
						.with(user("sam.brown@bluepumpkin.com")
								.password("user")
								.roles("USER")))
				.build();
	}

	@Test
	public void employeeHome() throws Exception {
	
		mvcResult = mockMvc.perform(get("/"))
//				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().size(4))
				.andExpect(model().attribute("navigation", "pages"))
				.andExpect(model().attribute("participations", hasSize(2)))
				.andExpect(model().attribute("birthdays", hasSize(1)))
				.andExpect(model().attribute("sportsEvent", notNullValue()))
				.andExpect(view().name("employee/home"))
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("View status of your participation requests")))
				.andReturn();
		
		@SuppressWarnings("unchecked")
		List<Participation> participations = (List<Participation>) mvcResult.getModelAndView()
				.getModel().get("participations");
		
		assertThat(participations.get(0).getEvent().getConvertedDateTime().toString())
		.isEqualTo("Thu May 01 09:30:00 CEST " + YEAR_FUTURE);
		
		assertThat(participations.get(1).getEmployee().getFirstName()).isEqualTo(EMP_NAME);
		assertThat(participations.get(1).getEvent().getName()).isEqualTo(TRAINING_NAME);
		assertThat(participations.get(1).getEvent().getConvertedDateTime().toString())
			.containsOnlyOnce("Jun 01 09:30:00 CEST " + YEAR_FUTURE);
		
		Birthday samBirthday = new Birthday(EMP_NAME, 41);
		List<?> mAttrBirthdays = (List<?>) mvcResult.getModelAndView().getModel().get("birthdays");
		Birthday mAttrSamBirthday = (Birthday) mAttrBirthdays.get(0);
		
		assertThat(mAttrSamBirthday).isEqualToComparingOnlyGivenFields(samBirthday, "firstName", "age");
	}
	
	@Test
	public void employeeHomeEmpId() throws Exception {
	
		mvcResult = mockMvc.perform(get("/{empId}", EMP_ID))
				.andExpect(status().isOk())
				.andExpect(model().size(2))
//				.andExpect(model().attributeExists("participations"))
				.andExpect(model().attribute("participations", hasSize(2)))
				.andExpect(view().name("employee/home"))
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("View status of your participation requests")))
				.andReturn();
		
		@SuppressWarnings("unchecked")
		List<Participation> participations = (List<Participation>) mvcResult.getModelAndView()
				.getModel().get("participations");
		assertThat(participations.get(0).getEmployee().getFirstName()).isEqualTo(EMP_NAME);
		assertThat(participations.get(0).getEvent().getName()).isEqualTo(MEETING_NAME);
	}
	
	@Test
	public void empHomeEmpId_UserNotExists_throwsException() throws Exception {
		
		mvcResult = 
				mockMvc.perform(get("/{empId}", 20L))
				.andDo(print())
				.andExpect(handler().handlerType(EmployeeController.class))
				.andExpect(handler().methodName("employeeHome"))
				.andExpect(status().is(404))
				.andExpect(model().attribute("error", "Could not find employee '" + 20L + "'."))
				.andExpect(view().name("error"))
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().contentType("text/html;charset=UTF-8"))
				.andExpect(content().string(containsString("<strong>Could not find employee &#39;" + 20L + "&#39;.</strong>")))
				.andReturn();
		
		assertThat(mvcResult.getResolvedException().toString()).containsOnlyOnce("EmployeeNotFoundException");
//		assertThat(mvcResult.getResolvedException().getClass().getName()).isEqualTo("bluepumpkin.domain.EmployeeNotFoundException");
		assertThat(mvcResult.getResolvedException().getMessage()).isEqualTo("Could not find employee '" + 20L + "'.");
	}
	
	@Test
	public void getUpcomingEvents() throws Exception {
		mockMvc.perform(get("/upcomingEvents"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("upcomingEvents", hasSize(UPC_EVENTS_SIZE)));
	}
	
	@Test
	public void requestForParticipation_pageRequestParamNotRequired() throws Exception {
		mockMvc.perform(get("/participations/{eventId}", MEETING_ID)
			.param("action", "doRequest"))
			.andDo(print())
			.andExpect(status().is(302));
	}
	
}
