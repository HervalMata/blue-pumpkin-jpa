package bluepumpkin.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import bluepumpkin.domain.Employee;
import bluepumpkin.domain.Event;
import bluepumpkin.domain.Participation;
import bluepumpkin.domain.ParticipationId;
import bluepumpkin.domain.ParticipationStatus;
import bluepumpkin.repository.EmployeeRepository;
import bluepumpkin.repository.EventRepository;
import bluepumpkin.repository.ParticipationRepository;
import static bluepumpkin.domain.ParticipationStatus.*;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Service
public class AdminService {

	private final ParticipationRepository participationRepository;
	private final EventRepository eventRepository;
	
	@Autowired
	public AdminService(ParticipationRepository participationRepository,
			EventRepository eventRepository) {
		this.participationRepository = participationRepository;
		this.eventRepository = eventRepository;
	}

	public List<Participation> getFutureWaitingParticipations() {
		List<Participation> participations = participationRepository.findByStatus(WAITING).stream()
		.filter(p -> p.getEvent().getDateTime().compareTo(LocalDateTime.now()) > 0)
		.collect(collectingAndThen(toList(), this::convertEventDateTimesToDateType));
		Collections.reverse(participations);
		return participations;
	}
	private List<Participation> convertEventDateTimesToDateType(List<Participation> participations) {
		participations.forEach(p -> p.getEvent().convertToDateType());
		return participations;
	}
	
	public void changeParticipationStatus(Long eventId, Long empId, ParticipationStatus status) {
		Participation p = participationRepository.findOne(new ParticipationId(empId, eventId));
		p.setStatus(status);
		participationRepository.save(p);
	}
	
	public List<Event> getUpcomingEvents() { 
		return eventRepository.findAll().stream()
			.filter(e -> e.getDateTime().compareTo(LocalDateTime.now()) > 0)
			.collect(collectingAndThen(toList(), CommonService::sortEventsByDateTime));
	}

	public void createEvent(Event event) {
		event.convertToLocalDateTimeType();
		eventRepository.save(event);
	}

	public Event getEvent(Long eventId) {
		Event event = eventRepository.findOne(eventId);
		event.convertToDateType();
		return event;
	}

	public void updateEvent(Event event) {
		event.convertToLocalDateTimeType();
		eventRepository.save(event);
	}

	public void deleteEvent(Long id) {
		eventRepository.delete(id);
	}

}
