package cn.itcast.oa.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;

import cn.itcast.oa.domain.User;
import cn.itcast.oa.service.ApplicationTemplateService;
import cn.itcast.oa.service.DepartmentService;
import cn.itcast.oa.service.FlowService;
import cn.itcast.oa.service.ForumService;
import cn.itcast.oa.service.PrivilegeService;
import cn.itcast.oa.service.ProcessDefinitionService;
import cn.itcast.oa.service.ReplyService;
import cn.itcast.oa.service.RoleService;
import cn.itcast.oa.service.TopicService;
import cn.itcast.oa.service.UserService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport {

	@Resource
	protected RoleService roleService;
	@Resource
	protected DepartmentService departmentService;
	@Resource
	protected UserService userService;
	@Resource
	protected PrivilegeService privilegeService;

	@Resource
	protected ForumService forumService;
	@Resource
	protected TopicService topicService;
	@Resource
	protected ReplyService replyService;
	@Resource
	protected ProcessDefinitionService processDefinitionService;
	@Resource
	protected ApplicationTemplateService applicationTemplateService;
	@Resource
	protected FlowService flowService;

	/**
	 * 获取当前登录的用户
	 * 
	 * @return
	 */
	protected User getCurrentUser() {
		return (User) ActionContext.getContext().getSession().get("user");
	}

	// 页码默认为第1页
	protected int pageNum = 1;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * 保存上传的文件，并返回在服务器端真实的存储路径
	 * @param upload
	 * @return
	 */
	protected String saveUploadFile(File upload) {
		// >> 1, 得到在保存的文件路径的真实地址
		SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
		String basePath = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload_files");
		String datePath = sdf.format(new Date());
		
		// >> 2, 如果文件夹不存在，就创建
		File dir = new File(basePath +datePath);
		if(!dir.exists()){
			dir.mkdirs();  
		}
		String path = basePath+ datePath + UUID.randomUUID().toString(); // 注意同名的问题，可以使用uuid做为文件名
		
		
		// >> 3, 移动文件
		upload.renameTo(new File(path));
		
		
//		String basePath = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload_files");
//		String path = basePath + "/" + UUID.randomUUID().toString();
//		upload.renameTo(new File(path));
		
		return path;
	}

}
