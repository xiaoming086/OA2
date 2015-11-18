package cn.itcast.oa.view.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import org.apache.struts2.ServletActionContext;
import org.jbpm.api.ProcessDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.oa.base.ModelDrivenBaseAction;
import cn.itcast.oa.domain.ApplicationTemplate;

import com.opensymphony.xwork2.ActionContext;

@Controller
@Scope("prototype")
public class ApplicationTemplateAction extends ModelDrivenBaseAction<ApplicationTemplate> {

	private InputStream inputStream;
	
	private File upload ;
	

	/** 列表 */
	public String list() throws Exception {
		List<ApplicationTemplate> applicationTemplateList = applicationTemplateService.findAll();
		ActionContext.getContext().put("applicationTemplateList",applicationTemplateList);
		return "list";
	}

	/** 删除 */
	public String delete() throws Exception {
		applicationTemplateService.delete(model.getId());
		return "toList";
	}

	/** 添加页面 */
	public String addUI() throws Exception {
		List<ProcessDefinition> processDefinitionList = processDefinitionService.findAllLatestVersions();
		ActionContext.getContext().put("processDefinitionList", processDefinitionList);
		return "saveUI";
	}

	/** 添加 */
	public String add() throws Exception {
//		String basePath = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload_files");
//		String path = basePath + "/" + UUID.randomUUID().toString();
//		upload.renameTo(new File(path));
		String path = saveUploadFile(upload);
		model.setPath(path);
		applicationTemplateService.save(model);
		return "toList";	}


	

	/** 修改页面 */
	public String editUI() throws Exception {
		List<ProcessDefinition> processDefinitionList = processDefinitionService.findAllLatestVersions();
		ActionContext.getContext().put("processDefinitionList", processDefinitionList);
		ApplicationTemplate applicationTemplate = applicationTemplateService.getById(model.getId());
		ActionContext.getContext().getValueStack().push(applicationTemplate);
		return "saveUI";
	}

	/** 修改 */
	public String edit() throws Exception {
		ApplicationTemplate applicationTemplate = applicationTemplateService.getById(model.getId());
		applicationTemplate.setName(model.getName());
		applicationTemplate.setProcessDefinitionKey(model.getProcessDefinitionKey());
		if(upload != null){
			File file = new File(applicationTemplate.getPath());
			if(file.exists()){
				file.delete(); 
			}
			String path = saveUploadFile(upload);
			applicationTemplate.setPath(path);
		}
		return "toList";
	}

	/** 下载 */
	public String download() throws Exception {
		ApplicationTemplate applicationTemplate = applicationTemplateService.getById(model.getId());
		inputStream = new FileInputStream(applicationTemplate.getPath());
//		String fileName = URLEncoder.encode(applicationTemplate.getName(),"utf-8"); //这方法貌似不行啊！！！！
		String fileName = new String(applicationTemplate.getName().getBytes("gbk"),"iso8859-1");
		ActionContext.getContext().put("fileName", fileName);
		return "download";
	}

	// ---

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}


}
