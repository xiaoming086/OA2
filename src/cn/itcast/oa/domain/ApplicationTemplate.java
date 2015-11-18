package cn.itcast.oa.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * 实体：申请模板
 * 
 */
public class ApplicationTemplate {
	private Long id;
	private String name;
	private String processDefinitionKey;
	private String path; // 文件在服务器端存储的路径
	private Set<Application> applications = new HashSet<Application>();
	
	

	public Set<Application> getApplications() {
		return applications;
	}

	public void setApplications(Set<Application> applications) {
		this.applications = applications;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
