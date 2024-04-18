package org.egov.im.service;


import org.egov.im.entity.Incident;
import org.egov.im.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {
	@Autowired
	private IncidentRepository incidentRepository;
	
	public Incident save(Incident incident) {
		// TODO Auto-generated method stub
		return incidentRepository.save(incident);
	}
}
