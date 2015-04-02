package bluepumpkin.domain;

public class EmployeeNotFoundException extends RuntimeException {

	public EmployeeNotFoundException(Long empId) {
		super("Could not find employee '" + empId + "'.");
	}
}
