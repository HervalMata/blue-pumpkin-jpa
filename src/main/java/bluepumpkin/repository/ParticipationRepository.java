package bluepumpkin.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bluepumpkin.domain.Participation;
import bluepumpkin.domain.ParticipationId;
import bluepumpkin.domain.ParticipationStatus;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
	
	Set<Participation> findByStatus(ParticipationStatus status);
}
