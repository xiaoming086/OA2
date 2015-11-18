package cn.itcast.oa.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessDefinitionQuery;
import org.jbpm.api.ProcessEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.oa.service.ProcessDefinitionService;

@Service
@Transactional
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {
	@Resource
	private ProcessEngine processEngine ;

	
	public void deploy(ZipInputStream zipInputStream) {
		processEngine.getRepositoryService().createDeployment().addResourcesFromZipInputStream(zipInputStream).deploy();
	}
	
	public List<ProcessDefinition> findAllLatestVersions() {
		List<ProcessDefinition> all = processEngine.getRepositoryService().createProcessDefinitionQuery().orderAsc(ProcessDefinitionQuery.PROPERTY_VERSION).list();
		Map<String , ProcessDefinition> map = new HashMap<String, ProcessDefinition>();
		for (ProcessDefinition pd : all) {
			map.put(pd.getKey(), pd);
		}
		return new ArrayList<ProcessDefinition>(map.values());
	}
	
	public void deleteByKey(String key) {
		List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(key).list();
		for(ProcessDefinition pd : list){
			processEngine.getRepositoryService().deleteDeploymentCascade(pd.getDeploymentId());
		}
	}

	public InputStream getProcessImageResourceAsStream(String processDefinitionId) {
		ProcessDefinition pd  = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(processDefinitionId).uniqueResult() ;
		return processEngine.getRepositoryService().getResourceAsStream(pd.getDeploymentId(), pd.getImageResourceName());
		 
	}

}
