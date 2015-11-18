package cn.itcast.oa.view.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.jbpm.api.ProcessDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.oa.base.BaseAction;
@Controller
@Scope("prototype")
public class ProcessDefinitionAction extends BaseAction {
	
	private InputStream inputStream ;
	
	private String key ;
	
	private File upload ;
	
	private String id ;
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String list() throws Exception{
		List<ProcessDefinition> processDefinitionList = processDefinitionService.findAllLatestVersions();
		ActionContext.getContext().put("processDefinitionList", processDefinitionList);
		return "list";
	}
	
	public String delete() throws Exception{
//		key = new String(key.getBytes("iso8859-1"),"utf-8");
		key = URLDecoder.decode(key,"utf-8");//第二次url编码
		processDefinitionService.deleteByKey(key);
		return "toList";
	}
	
	public String addUI() throws Exception{
		return "addUI";
	}
	
	public String add() throws Exception{
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(upload)) ;
		try{
		processDefinitionService.deploy(zipInputStream);
		} finally{
			zipInputStream.close();
		}
		return "toList";
	}
	
	public String downloadProcessImage() throws Exception{
		id = URLDecoder.decode(id,"utf-8");//解码
		inputStream = processDefinitionService.getProcessImageResourceAsStream(id);
		return "downloadProcessImage";
	}
}
