package org.egov.im.service;


import org.egov.im.entity.Event;
import org.egov.im.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
	@Autowired
	private EventRepository eventRepository;
	
	public Event save(Event event) {
		// TODO Auto-generated method stub
		return eventRepository.save(event);
	}
}
