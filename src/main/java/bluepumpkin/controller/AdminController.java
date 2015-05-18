package bluepumpkin.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bluepumpkin.domain.Event;
import bluepumpkin.domain.EventType;
import bluepumpkin.service.AdminService;
import bluepumpkin.service.CommonService;
import bluepumpkin.support.web.MessageHelper;

import static bluepumpkin.domain.ParticipationStatus.*;

@Controller
@RequestMapping("admin")
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
	
	private final AdminService adminService;
	private final CommonService commonService;
	
	@Autowired
	public AdminController(AdminService adminService,
			CommonService commonService) {
		this.adminService = adminService;
		this.commonService = commonService;
	}
	
	@ModelAttribute("navigation")
	private String getNavigationType() { // Model
		return "adminPages";
	}

	@RequestMapping
	public String adminHome(Model model) {
		model.addAttribute("birthdays", commonService.getBirthdays());
		LOG.info("Waiting participation requests to admin home view");
		model.addAttribute("participations", adminService.getFutureWaitingParticipations());
		return "admin/home";
	}
	
	@RequestMapping(value = "participations/{eventId}/{empId}", method = RequestMethod.GET)
	public String approveParticipation(@PathVariable Long eventId, @PathVariable Long empId, 
			@RequestParam String action, RedirectAttributes redirectAttrs) { //@RequestParam("action")
		StringBuilder sb = new StringBuilder("The participation request has been ");
		if (action.equalsIgnoreCase("approve")) {
			adminService.changeParticipationStatus(eventId, empId, APPROVED);
			sb.append("approved!");
		} 
		else if (action.equalsIgnoreCase("deny")) {
			adminService.changeParticipationStatus(eventId, empId, DENIED);
			sb.append("denied!");
		}
		MessageHelper.addSuccessAttribute(redirectAttrs, sb.toString());
		return "redirect:/admin";
	}
	
	@RequestMapping("upcomingEvents")
	public String getUpcomingEvents(Model model) {
		LOG.info("All upcoming events from the earliest to the latest one");
		model.addAttribute("upcomingEvents", adminService.getUpcomingEvents());
		return "admin/upcomingEvents";
	}
	
	@RequestMapping("addEvent")
	public String initAddEventForm(Model model) {
		return initEventForm("addEvent", null, model);
	}
	
	@RequestMapping(value = "addEvent", method = RequestMethod.POST)
	public ModelAndView processAddEventForm(@Valid @ModelAttribute("eventForm") Event event,
			Errors errors, RedirectAttributes redirectAttrs) {
		if (errors.hasErrors()) {
			ModelAndView mv = new ModelAndView("admin/eventForm");
			mv.addObject("form", "addEvent");
			mv.addObject("allTypes", EventType.values());
			return mv;
		}
		LOG.info("No errors, continue with creating of event: {}", event.getName());
		adminService.createEvent(event);
		MessageHelper.addSuccessAttribute(redirectAttrs, "Event has been created!");
		ModelAndView mv = new ModelAndView("redirect:/admin/upcomingEvents");
		return mv;
	}
	
	@RequestMapping(value = "updateEvent/{eventId}", method = RequestMethod.GET)
	public String initUpdateEventForm(@PathVariable Long eventId, Model model) {
		return initEventForm("updateEvent", eventId, model);
	}
	
	@RequestMapping(value = "updateEvent", method = RequestMethod.POST) // PUT
	public ModelAndView processUpdateEventForm(
			@Valid @ModelAttribute("eventForm") Event event, // TODO EventDTO
			Errors errors, RedirectAttributes redirectAttrs) {
		if (errors.hasErrors()) {
			ModelAndView mv = new ModelAndView("admin/eventForm");
			mv.addObject("form", "updateEvent");
			mv.addObject("allTypes", EventType.values());
			return mv;
		}
		LOG.info("No errors, continue with updating of event :{}", event.getName());
		adminService.updateEvent(event);
		MessageHelper.addSuccessAttribute(redirectAttrs, "Event has been updated!");
		ModelAndView mv = new ModelAndView("redirect:/admin/upcomingEvents");
		return mv;
	}
	
	@RequestMapping(value = "deleteEvent/{id}", method = RequestMethod.GET)
	public String deleteEvent(@PathVariable Long id, @RequestParam("page") String page, RedirectAttributes redirectAttrs) {
		adminService.deleteEvent(id);
		MessageHelper.addSuccessAttribute(redirectAttrs, "Event has been deleted!");
		if (page.equals("upcomingEvents"))
			 return "redirect:/admin/upcomingEvents";
		return "redirect:/admin/pastEvents";
	}
	
	@RequestMapping(value = "pastEvents", method = RequestMethod.GET)
	public String getPastEvents(Model model) {
		LOG.info("All past events");
		model.addAttribute("pastEvents", adminService.getPastEvents());
		return "admin/pastEvents";
	}	
	
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public String getAccounts(Model model) {
		LOG.info("All accounts");
		model.addAttribute("accounts", adminService.getAccounts());
		return "admin/accounts";
	}
	
	private String initEventForm(String formType, Long eventId, Model model) {
		if (formType.equals("addEvent"))
			model.addAttribute("eventForm", new Event());
		else // if equals "updateEvent"
//			TODO get EventDTO
			model.addAttribute("eventForm", adminService.getEvent(eventId));
		model.addAttribute("allTypes", EventType.values());	
		model.addAttribute("form", formType);
		return "admin/eventForm";
	}
	
}
