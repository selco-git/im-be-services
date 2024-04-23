package org.egov.im.service;


import org.egov.im.entity.Assignee;
import org.egov.im.repository.AssigneeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssigneeService {
	@Autowired
	private AssigneeRepository assigneeRepository;
	
	public Assignee save(Assignee assignee) {
		// TODO Auto-generated method stub
		return assigneeRepository.save(assignee);
	}
}
