package cn.itcast.oa.service;

import java.util.Collection;
import java.util.List;

import cn.itcast.oa.domain.Application;
import cn.itcast.oa.domain.ApproveInfo;
import cn.itcast.oa.domain.TaskView;
import cn.itcast.oa.domain.User;

public interface FlowService {

	void submit(Application application);

	List<TaskView> findMyTaskViewList(User currentUser);

	/**
	 *  outcome：指定离开本活动要使用的Transition的名称 ，如果为空，就是用默认的Transition离开本活动
	 */
	void approve(ApproveInfo approveInfo, String taskId, String outcome);

	Application getApplicationById(Long applicationId);
	/**
	 * 获取指定任务活动中所有指向后面环节的Transition 的名称
	 */
	Collection<String> getOutcomeByTaskId(String taskId);

	List<ApproveInfo> getApproveInfosByApplicationId(Long applicationId);

}
