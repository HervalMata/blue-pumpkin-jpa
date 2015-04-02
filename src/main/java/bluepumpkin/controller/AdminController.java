package bluepumpkin.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bluepumpkin.domain.Event;
import bluepumpkin.domain.EventType;
import bluepumpkin.service.AdminService;
import bluepumpkin.service.CommonService;
import bluepumpkin.support.web.MessageHelper;

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
	
	@RequestMapping("upcomingEvents")
	public String getUpcomingEvents(Model model) {
		LOG.info("All upcoming events from the earliest to the latest one");
		model.addAttribute("upcomingEvents", adminService.getUpcomingEvents());
		return "admin/upcomingEvents";
	}
	
	@RequestMapping("addEvent")
	public String initAddEventForm(Model model) {
		model.addAttribute("form", "addEvent");
		model.addAttribute("eventForm", new Event());
		model.addAttribute("allTypes", EventType.values());
		return "admin/eventForm";
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
	
}
