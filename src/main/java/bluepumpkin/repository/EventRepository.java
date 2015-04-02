package bluepumpkin.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bluepumpkin.domain.Event;
import bluepumpkin.domain.EventType;

@Repository								
public interface EventRepository extends JpaRepository<Event, Long> {
	
		Set<Event> findByType(EventType type);
		
		Event findByName(String name);
		
		List<Event> findByParticipationsEmployeeIdNotIn(Long empId);
		
		List<Event> findByParticipationsEmployeeAccountEmail(String email);
}
