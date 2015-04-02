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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import bluepumpkin.domain.Employee;
import bluepumpkin.domain.EmployeeNotFoundException;
import bluepumpkin.service.CommonService;
import bluepumpkin.service.EmployeeService;
import bluepumpkin.service.AccountService;

@Controller
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

	@RequestMapping("/{empId}")
	public String employeeHome(Model model, @PathVariable Long empId) {
		Employee emp = employeeService.validateAndGetEmployee(empId);
		
		LOG.info("Employee's participation requests to home view");
		model.addAttribute("participations", employeeService.getFutureSortedParticipations(emp));
		return "employee/home";
	}
	
	@RequestMapping("/")
	public String employeeHome(Principal principal, Model model) {
		if (accountService.findByEmail(principal.getName()).isAdmin()) {
			return "redirect:/admin";
		}
		Employee emp = employeeService.findByEmail(principal.getName());
		model.addAttribute("navigation", "pages");
		model.addAttribute("birthdays", commonService.getBirthdays());
		model.addAttribute("sportsEvent", employeeService.getLatestSportsEvent());
		LOG.info("Employee's participation requests to home view");
		model.addAttribute("participations", employeeService.getFutureSortedParticipations(emp));
		return "employee/home";
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


