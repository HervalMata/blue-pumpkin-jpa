package bluepumpkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bluepumpkin.domain.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
	
}
