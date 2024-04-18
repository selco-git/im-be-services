package org.egov.im.service;


import org.egov.im.entity.State;
import org.egov.im.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateService {
	@Autowired
	private StateRepository stateRepository;
	
	public State save(State state) {
		// TODO Auto-generated method stub
		return stateRepository.save(state);
	}
}
