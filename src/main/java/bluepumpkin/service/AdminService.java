package bluepumpkin.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bluepumpkin.domain.Event;
import bluepumpkin.domain.Participation;
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
		.collect(collectingAndThen(toList(), this::convertPartEventDateTimesToDateType));
		Collections.reverse(participations);
		return participations;
	}
	private List<Participation> convertPartEventDateTimesToDateType(List<Participation> participations) {
		participations.forEach(p -> p.getEvent().convertToDateType());
		return participations;
	}

	public List<Event> getUpcomingEvents() {
		return eventRepository.findAll().stream()
			.filter(e -> e.getDateTime().compareTo(LocalDateTime.now()) > 0)
			.collect(collectingAndThen(toList(), this::sortEventsByDateTime));
	}
	private List<Event> sortEventsByDateTime(List<Event> events) {
		return events.stream()
			.sorted((e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
			.collect(collectingAndThen(toList(), this::convertEventDateTimesToDateType));	
	}
	private List<Event> convertEventDateTimesToDateType(List<Event> events) {
		events.forEach(e -> e.convertToDateType());
		return events;
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

	public List<Event> getPastEvents() {
		List<Event> pastEvents = eventRepository.findAll().stream()
			.filter(e -> e.getDateTime().compareTo(LocalDateTime.now()) < 0)
			.collect(collectingAndThen(toList(), this::sortEventsByDateTime));
		Collections.reverse(pastEvents);
		return pastEvents;
	}

}
