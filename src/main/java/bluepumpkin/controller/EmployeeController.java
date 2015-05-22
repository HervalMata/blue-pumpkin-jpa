package bluepumpkin.controller;

import java.security.Principal;

import org.assertj.core.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bluepumpkin.domain.Employee;
import bluepumpkin.domain.EmployeeNotFoundException;
import bluepumpkin.service.CommonService;
import bluepumpkin.service.EmployeeService;
import bluepumpkin.service.AccountService;
import bluepumpkin.support.web.MessageHelper;

@Controller
@RequestMapping
public class EmployeeController {

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);
	
	private final AccountService accountService;
	private final EmployeeService employeeService;
	private final CommonService commonService;
	
	@Autowired
	public EmployeeController(AccountService accountService, EmployeeService employeeService,
			CommonService commonService) {
		this.accountService = accountService;
		this.employeeService = employeeService;
		this.commonService = commonService;
	}
	
	@ModelAttribute("navigation")
	private String getNavigationType() {
		return "pages";
	}
	
	private Employee getEmployee(Principal principal) {
		Assert.notNull(principal);
		return employeeService.findByEmail(principal.getName());
	}

	@RequestMapping("{empId}")
	public String employeeHome(Model model, @PathVariable Long empId) {
		Employee emp = employeeService.validateAndGetEmployee(empId);
		
		LOG.info("Employee's participation requests to home view");
		model.addAttribute("participations", employeeService.getFutureSortedParticipations(emp));
		return "employee/home";
	}
	
	@RequestMapping
	public String employeeHome(Principal principal, Model model) {
		if (accountService.findByEmail(principal.getName()).isAdmin()) {
			return "redirect:/admin";
		}
		model.addAttribute("birthdays", commonService.getBirthdays());
		model.addAttribute("sportsEvent", employeeService.getLatestSportsEvent());
		LOG.info("Employee's participation requests to home view");
		model.addAttribute("participations", employeeService.getFutureSortedParticipations(getEmployee(principal)));
		return "employee/home";
	}
	
	@RequestMapping(value = "participations/{eventId}", method = RequestMethod.GET)
	public String requestForOrCancelParticipation(@PathVariable final Long eventId, @RequestParam final String action,
			 @RequestParam(required = false) final String page, final Principal principal, final RedirectAttributes redirectAttrs) {
		if (action.equals("doRequest")) {
			employeeService.createParticipationRequest(eventId, getEmployee(principal).getId());
			MessageHelper.addSuccessAttribute(redirectAttrs, "You have requested for the participation!");
		}
		else if (action.equals("cancel")) {
			employeeService.deleteParticipationRequest(eventId, getEmployee(principal).getId());
			MessageHelper.addSuccessAttribute(redirectAttrs, "Your participation request has been canceled!");
			if (page.equals("home"))
				return "redirect:/";
		}
		return "redirect:/upcomingEvents";
	}
	
	@RequestMapping("upcomingEvents")
	public String getUpcomingEvents(Model model, Principal principal) {
		LOG.info("Upcoming events for the employee");
		model.addAttribute("upcomingEvents", employeeService.getUpcomingEvents(getEmployee(principal)));
		return "employee/upcomingEvents";
	}
	
	@RequestMapping("pastEvents")
	public String getPastEvents(Model model) {
		LOG.info("All past events");
		model.addAttribute("pastEvents", commonService.getPastEvents());
		return "employee/pastEvents";
	}
	
	@RequestMapping("contacts")
	public String getContacts(Model model) {
		LOG.info("All contacts");
		model.addAttribute("contacts", commonService.getAccounts());
		return "employee/contacts";
	}
}

@ControllerAdvice()
class EmployeeControllerAdvice {
	@ExceptionHandler(value = EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	ModelAndView employeeNotFoundExceptionHandler(EmployeeNotFoundException ex, WebRequest request) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("error", ex.getMessage());
		mav.addObject("status", "404");
		return mav;
	}
}


