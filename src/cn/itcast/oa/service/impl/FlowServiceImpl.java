package cn.itcast.oa.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.inject.ProvidedBy;

import cn.itcast.oa.domain.Application;
import cn.itcast.oa.domain.ApproveInfo;
import cn.itcast.oa.domain.TaskView;
import cn.itcast.oa.domain.User;
import cn.itcast.oa.service.FlowService;
@Service
@Transactional
public class FlowServiceImpl implements FlowService {
	@Resource
	private  SessionFactory sessionFactory ;
	@Resource
	private ProcessEngine processEngine ;
	
	private SimpleDateFormat sdf = new SimpleDateFormat() ;
	
	public void submit(Application application) {
		//设置属性并保存
		application.setTitle(application.getApplicationTemplate().getName()//
				+ "_" + application.getApplicant().getName()//
				+ "_" + sdf.format(application.getApplyTime()));//
		application.setApplyTime(new Date());
		application.setStatus(Application.STATUS_RUNNING);
		sessionFactory.getCurrentSession().save(application);
		
		//启动流程实例开始流转
		//>>启动流程实例，设置流程变量application
		String processDefinitionKey = application.getApplicationTemplate().getProcessDefinitionKey();
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("application", application);
		ProcessInstance pi = processEngine.getExecutionService().startProcessInstanceByKey(processDefinitionKey, variables);
		//>>办理完第一个任务（提交申请）
		Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(pi.getId()).uniqueResult() ;
		processEngine.getTaskService().completeTask(task.getId());
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public List<TaskView> findMyTaskViewList(User currentUser) {
		//查询我的任务列表
		String userId = currentUser.getLoginName() ;//使用loginName作为jbpm中的用户标识符
		List<Task> taskList = processEngine.getTaskService().findPersonalTasks(userId);
		
		//获取每个任务对应的申请信息
		List<TaskView> returnList = new ArrayList<TaskView>();
		for(Task t : taskList){
			Application application = (Application) processEngine.getTaskService().getVariable(t.getId(), "application");
			TaskView tv = new TaskView(t,application);
			returnList.add(tv);
		}
		return returnList;
	}

	public void approve(ApproveInfo approveInfo, String taskId,String outcome) {
		//保存审批信息
		sessionFactory.getCurrentSession().save(approveInfo);
		//完成当前任务
		Task task = processEngine.getTaskService().getTask(taskId) ;//获取任务的代码一定要写到完成任务前
		if (outcome == null ) {
			processEngine.getTaskService().completeTask(taskId);//使用默认的Transition
		}else {
			processEngine.getTaskService().completeTask(taskId,outcome);//使用指定名称的Transition
			
		}
		
		//获取任务所属的流程实例（正在执行的表中的信息），如果流程已经结束了，则返回null
		ProcessInstance pi = (ProcessInstance) processEngine.getExecutionService().findExecutionById(task.getExecutionId());
		//维护申请状态
		Application application = approveInfo.getApplication();
		if(!approveInfo.isApproval()){
			//如果本环节不同意，则流程直接结束，申请的状态为 未通过
			//>>如果本环节不是最后一个，我们就要手工结束这个流程实例
			if(pi != null){
				processEngine.getExecutionService().endProcessInstance(pi.getId(), ProcessInstance.STATE_ENDED);
			}
			application.setStatus(Application.STATUS_REJECTED);
		}else {
			if(pi == null){
				//如果本环节同意，并且本环节是最后一个审批，则流程正常结束，申请的状态为 已通过
				//>>
				application.setStatus(Application.STATUS_APPROVED);
			}
		}
		sessionFactory.getCurrentSession().update(application);
	}

	public Application getApplicationById(Long applicationId) {
		return (Application) sessionFactory.getCurrentSession().get(Application.class, applicationId);
	}

	public Collection<String> getOutcomeByTaskId(String taskId) {
		return processEngine.getTaskService().getOutcomes(taskId);
	}

	public List<ApproveInfo> getApproveInfosByApplicationId(Long applicationId) {
		return sessionFactory.getCurrentSession().createQuery("FROM Approveinfo a WHERE a.application.id=? ORDER BY a.approveTime ASC")//
				.setParameter(0, applicationId)//
				.list();
	}


	
}
