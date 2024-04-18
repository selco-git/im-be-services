package org.egov.im.service;


import org.egov.im.entity.ProcessInstance;
import org.egov.im.repository.ProcessInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessInstanceService {
	@Autowired
	private ProcessInstanceRepository processInstanceRepository;
	
	public ProcessInstance save(ProcessInstance processInstance) {
		// TODO Auto-generated method stub
		return processInstanceRepository.save(processInstance);
	}
}
