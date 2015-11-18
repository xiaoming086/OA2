package cn.itcast.oa.service.impl;


import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.oa.base.BaseDaoImpl;
import cn.itcast.oa.domain.ApplicationTemplate;
import cn.itcast.oa.service.ApplicationTemplateService;

@Service
@Transactional
public class ApplicationTemplateServiceImpl extends BaseDaoImpl<ApplicationTemplate> implements ApplicationTemplateService {

	public void delete(Long id) {
		ApplicationTemplate applicationTemplate = getById(id);
		getSession().delete(applicationTemplate);
		File file = new File(applicationTemplate.getPath());
		if(file.exists()){
			file.delete();
		}
	}
	
}
