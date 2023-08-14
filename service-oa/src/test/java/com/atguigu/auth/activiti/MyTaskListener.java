package com.atguigu.auth.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author zijianLi
 * @create 2023- 03- 21- 22:27
 */
public class MyTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
//        //当前任务的名字是getName()
//        if(delegateTask.getName().equals("经理审批")){
//            //这里指定任务负责人
//            delegateTask.setAssignee("jack");
//        } else if(delegateTask.getName().equals("人事审批")){
//            //这里指定任务负责人
//            delegateTask.setAssignee("rose");
//        }
    }
}
