package bluepumpkin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bluepumpkin.domain.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

		Optional<Employee> findById(Long id);
		
		Employee findByAccountEmail(String email);
}
