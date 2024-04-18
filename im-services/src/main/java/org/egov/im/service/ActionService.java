package org.egov.im.service;


import org.egov.im.repository.ActionRepository;
import org.egov.im.entity.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionService {
	@Autowired
	private ActionRepository actionRepository;
	
	public Action save(Action action) {
		// TODO Auto-generated method stub
		return actionRepository.save(action);
	}
}
