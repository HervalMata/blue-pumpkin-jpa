package bluepumpkin.domain.web;

public class Birthday {

	private String firstName, lastName, position, department;

    private int age;
	
    protected Birthday() {	
    }
    
	public Birthday(String firstName, String lastName, String position,
			String department, int age) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.position = position;
		this.department = department;
		this.age = age;
	}
	
//	for testing
	public Birthday(String firstName, int age) {
		this.firstName = firstName;
		this.age = age;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPosition() {
		return position;
	}

	public String getDepartment() {
		return department;
	}
	
	public int getAge() {
		return age;
	}
    
}
