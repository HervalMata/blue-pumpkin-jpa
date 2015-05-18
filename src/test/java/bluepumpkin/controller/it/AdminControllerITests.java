package bluepumpkin.controller.it;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;

import bluepumpkin.domain.Employee;
import bluepumpkin.domain.Event;
import bluepumpkin.domain.EventType;
import bluepumpkin.domain.Participation;
import bluepumpkin.domain.ParticipationId;
import bluepumpkin.domain.ParticipationStatus;
import bluepumpkin.domain.web.Birthday;
import bluepumpkin.repository.EventRepository;
import bluepumpkin.repository.ParticipationRepository;
import bluepumpkin.repository.TeamRepository;
import bluepumpkin.support.web.Message;
//import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Transactional
public class AdminControllerITests extends AbstractIntegrationTests {

	private MvcResult mvcResult;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private ParticipationRepository participationRepository;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Test
	public void adminHome() throws Exception {	
		mvcResult = mockMvc.perform(get("/admin"))
			.andExpect(status().isOk())
			.andExpect(model().size(3))
			.andExpect(model().attribute("navigation", "adminPages"))
			.andExpect(model().attribute("birthdays", hasSize(1)))
			.andExpect(model().attribute("participations", hasSize(3)))
			.andExpect(view().name("admin/home"))
			.andExpect(content().contentType(contentType))
//			.andExpect(content().contentType("text/html;charset=UTF-8"))
			.andExpect(content().string(containsString("View all participation requests")))
			.andReturn();
		
		Participation p = new Participation(2L, 4L, ParticipationStatus.WAITING, 
				new Employee("Barry", "Firefly"), 
				new Event("Sightseeing Trip", LocalDateTime.of(2015, 7, 1, 9, 30)));
		@SuppressWarnings("unchecked")
		List<Participation> mAttrParticipations = (List<Participation>) mvcResult.getModelAndView()
				.getModel().get("participations");
		assertThat(mAttrParticipations.get(0)).isEqualToComparingOnlyGivenFields(p, 
				"employeeId,eventId,status,employee.firstName,employee.lastName,event.name,event.dateTime".split(","));
		
		Birthday samBirthday = new Birthday("Sam", 41);
		List<?> mAttrBirthdays = (List<?>) mvcResult.getModelAndView().getModel().get("birthdays");
		Birthday mAttrSamBirthday = (Birthday) mAttrBirthdays.get(0);
		assertThat(mAttrSamBirthday).isEqualToComparingOnlyGivenFields(samBirthday, "firstName", "age");
	}
	
	@Test
	public void getUpcomingEvents() throws Exception {
		mvcResult = mockMvc.perform(get("/admin/upcomingEvents"))
			.andExpect(model().size(2))
			.andExpect(model().attribute("navigation", "adminPages"))
			.andExpect(model().attribute("upcomingEvents", hasSize(4)))
			.andExpect(view().name("admin/upcomingEvents"))
			.andExpect(content().contentType(contentType))
			.andExpect(content().string(containsString("View all upcoming events")))
			.andReturn();
		
		@SuppressWarnings("unchecked")
		List<Event> mAttrEvents = (List<Event>) mvcResult.getModelAndView()
				.getModel().get("upcomingEvents");
		
		assertThat(mAttrEvents.get(0).getName()).isEqualTo("Annual Meeting");
		assertThat(mAttrEvents.get(mAttrEvents.size() - 1).getName()).isEqualTo("Baseball");
	}
	
	@Test
	public void initAddEventForm() throws Exception {
		mockMvc.perform(get("/admin/addEvent"))
//			.andDo(print())
			.andExpect(model().size(4))
			.andExpect(content().string(containsString("<span>Create</span> event")))
			.andExpect(content().string(containsString("<option value=\"MEETING\">Meeting</option>")));
	}
	
	@Test
	public void processAddEventForm() throws Exception {
		mvcResult = mockMvc.perform(post("/admin/addEvent")
			.param("name", "Weekly Meeting")
			.param("place", "Office")
			.param("convertedDateTime", "01-10-2016 13:15")
			.param("type", EventType.MEETING.getId()))
			.andExpect(status().is(302))
			.andExpect(flash().attributeExists("message"))
			.andExpect(redirectedUrl("/admin/upcomingEvents"))
			.andReturn();
		
		assertThat(((Message) mvcResult.getFlashMap().get("message"))
				.getMessage()).isEqualTo("Event has been created!");
		
		assertThat(eventRepository.findByName("Weekly Meeting")).isNotNull();
	}
	
	@Test
	public void processAddEventForm_hasErrorWhenDateTimeNotInFuture() throws Exception {
		final String MODELATTR = "eventForm";
		final String FIELD = "convertedDateTime";
		final String ERROR = "The value \"01-10-2014\" is not in future!";
		
		mvcResult = mockMvc.perform(post("/admin/addEvent")
			.param("name", "Weekly Meeting")
			.param("place", "Office")
			.param(FIELD, "01-10-2014 13:15")
			.param("type", EventType.MEETING.getId()))
			.andDo(print())
			.andExpect(model().size(4))
//			.andExpect(model().errorCount(1))
			.andExpect(model().attributeErrorCount(MODELATTR, 1))
			.andExpect(model().attributeHasFieldErrorCode(MODELATTR, FIELD, "Future"))
			.andExpect(view().name("admin/eventForm"))
			.andExpect(content().string(containsString("The value &quot;01-10-2014&quot; is not in future!")))
			.andReturn();
		
		BindingResult bindingResult = (BindingResult) mvcResult.getModelAndView().getModel()
		.get(BindingResult.MODEL_KEY_PREFIX + MODELATTR);
		
		assertThat(bindingResult.getFieldError(FIELD).getDefaultMessage()).isEqualTo(ERROR);
		
		//another solutions
		assertThat(bindingResult.getFieldErrors().stream()
				.filter(fe -> fe.getDefaultMessage().equals(ERROR))
				.count())
				.isEqualTo(1);
		
		bindingResult.getFieldErrors().stream()
				.filter(fe -> fe.getField().equals(FIELD))
				.forEach(fe -> assertThat(fe.getDefaultMessage()).isEqualTo(ERROR));
	}
	
	@Test
	public void initUpdateEventForm() throws Exception {
		mockMvc.perform(get("/admin/updateEvent/{eventId}", 1L))
			.andDo(print())
			.andExpect(model().size(4))
			.andExpect(content().string(containsString("<span>Update</span> event")))
			.andExpect(content().string(containsString("name=\"convertedDateTime\" value=\"01-03-2015 09:30\"")))
			.andExpect(content().string(containsString("<option value=\"SPORTSEVENT\" selected=\"selected\">Sports Event</option>")))
			.andExpect(content().string(containsString("form=\"eventForm\" formaction=\"/admin/updateEvent\" formmethod=\"post\"")));
	}
	
	@Test
	public void processUpdateEventForm() throws Exception {
		mvcResult = mockMvc.perform(post("/admin/updateEvent")
			.param("id", "1")
			.param("name", "Weekly Meeting")
			.param("place", "Office")
			.param("convertedDateTime", "01-10-2016 13:15")
			.param("type", EventType.MEETING.getId()))
			.andDo(print())
			.andExpect(status().is(302))
			.andExpect(flash().attributeExists("message"))
			.andExpect(redirectedUrl("/admin/upcomingEvents"))
			.andReturn();
		
		assertThat(((Message) mvcResult.getFlashMap().get("message"))
				.getMessage()).isEqualTo("Event has been updated!");
		assertThat(eventRepository.findOne(1L).getName()).isEqualTo("Weekly Meeting");
	}
	
	@Test
	public void deleteEventFloorball() throws Exception {
		mockMvc.perform(get("/admin/deleteEvent/{id}", 1L)
			.param("page", "pastEvents"))
		.andDo(print())
		.andExpect(status().is(302))
		.andExpect(flash().attributeExists("message"))
		.andExpect(redirectedUrl("/admin/pastEvents"))
		.andExpect(header().string("Location", "/admin/pastEvents"));
		
		assertThat(teamRepository.findOne(1L)).isNull();
		assertThat(teamRepository.findOne(4L)).isNull();
		assertThat(teamRepository.findOne(6L).getEvent().getName()).isEqualTo("Baseball");
		assertThat(participationRepository.findOne(new ParticipationId(1L, 1L))).isNull();
		assertThat(participationRepository.findOne(new ParticipationId(1L, 2L))
				.getEvent().getName()).isEqualTo("Annual Meeting");
		assertThat(participationRepository.findAll()).hasSize(3);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getPastEvents() throws Exception {
		mvcResult = mockMvc.perform(get("/admin/pastEvents"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().size(2))
			.andReturn();
		
		assertThat(((List<Event>) mvcResult.getModelAndView().getModel()
				.get("pastEvents")).get(0).getName()).isEqualTo("Floorball");
	}
	
	@Test
	public void getAccounts() throws Exception {
		mockMvc.perform(get("/admin/accounts"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().size(2));
	}
	
}