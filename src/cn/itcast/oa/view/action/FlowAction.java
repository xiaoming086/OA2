package cn.itcast.oa.view.action;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.oa.base.BaseAction;
import cn.itcast.oa.domain.Application;
import cn.itcast.oa.domain.ApplicationTemplate;
import cn.itcast.oa.domain.ApproveInfo;
import cn.itcast.oa.domain.TaskView;
import cn.itcast.oa.util.HqlHelper;

@Controller
@Scope("prototype")
public class FlowAction extends BaseAction {
	
	private Long applicationTemplateId ;
	
	private File upload ;
	
	private Long applicationId ;
	
	private String status ;
	
	private String taskId ;//JPBM中taskId是String 
	
	private String comment ;
	
	private boolean approval ;
	
	private String outcome ;
	
	//===================申请人的功能============================
	/** 起草申请 */
	public String applicationTemplateList() throws Exception {
		List<ApplicationTemplate> templateList = applicationTemplateService.findAll();
		ActionContext.getContext().put("templateList",templateList);
		return "applicationTemplateList";
	}
	/** 提交申请页面 */
	public String submitUI() throws Exception {
		
		return "submitUI";
	}
	/** 提交申请 */
	public String submit() throws Exception {
		//封装对象
		Application application = new Application();
		application.setApplicationTemplate(applicationTemplateService.getById(applicationTemplateId));
		application.setPath(saveUploadFile(upload));
		application.setApplicant(getCurrentUser());

		flowService.submit(application);
		return "toMyApplicationList";
	}
	/** 我的申请查询 */
	public String myApplicationList() throws Exception {
		//准备分页信息
		new HqlHelper(Application.class,"a")//
			.addCondition("a.applicant=?", getCurrentUser())//
			.addCondition((applicationTemplateId != null), "a.applicationTemplate.id=?", applicationTemplateId)//
			.addCondition((status != null && status.trim().length() > 0), "a.status=?", status)//
			.addOrder("a.applyTime", false)//
			.buildPageBeanForStruts2(pageNum, userService);
		
		//准备模板列表
		List<ApplicationTemplate> applicationTemplateList = applicationTemplateService.findAll();
		ActionContext.getContext().put("applicationTemplateList",applicationTemplateList);
		return "myApplicationList";
	}
	
	//======================审批人的功能=========================
	
	/** 待我审批 */
	public String myTaskList() throws Exception {
		List<TaskView> taskViewList = flowService.findMyTaskViewList(getCurrentUser());
		ActionContext.getContext().put("taskViewList",taskViewList);
		return "myTaskList";
	}
	/** 审批处理页面 */
	public String approveUI() throws Exception {
		//准备数据 
		Collection<String> outcomes = flowService.getOutcomeByTaskId(taskId);
		ActionContext.getContext().put("outcomes",outcomes);
		return "approveUI";
	}
	/** 审批处理 */
	public String approve() throws Exception {
		//封装对象
		ApproveInfo approveInfo = new ApproveInfo();
		approveInfo.setApproval(approval);
		approveInfo.setComment(comment);
		approveInfo.setApprover(getCurrentUser());
		approveInfo.setApplication(flowService.getApplicationById(applicationId));
		approveInfo.setApproveTime(new Date());
		
		//调用业务方法(保存审批信息，完成当前任务，维护申请的状态)
		flowService.approve(approveInfo,taskId,outcome);
		
		return "toMyTaskList";
	}
	/** 查看流转记录 */
	public String approveHistory() throws Exception {
		List<ApproveInfo> approveInfoList = flowService.getApproveInfosByApplicationId(applicationId);
		ActionContext.getContext().put("approveInfoList",approveInfoList);
		return "approveHistory";
	}

	
	//-----------------------------------
	public Long getApplicationTemplateId() {
		return applicationTemplateId;
	}
	public void setApplicationTemplateId(Long applicationTemplateId) {
		this.applicationTemplateId = applicationTemplateId;
	}
	public File getUpload() {
		return upload;
	}
	public void setUpload(File upload) {
		this.upload = upload;
	}
	public Long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public boolean isApproval() {
		return approval;
	}
	public void setApproval(boolean approval) {
		this.approval = approval;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

}
