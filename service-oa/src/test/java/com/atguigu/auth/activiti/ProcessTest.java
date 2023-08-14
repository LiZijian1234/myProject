package com.atguigu.auth.activiti;

import org.springframework.boot.test.context.SpringBootTest;

/**流程定义的部署测试
 * @author zijianLi
 * @create 2023- 03- 21- 20:59
 */

@SpringBootTest
public class ProcessTest {

//    //注入RepositoryService
//    @Autowired
//    private RepositoryService repositoryService;
//
//    @Autowired
//    private RuntimeService runtimeService;
//
//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private HistoryService historyService;
//
//    /**
//     * 挂起单个流程实例
//     */
//    @Test
//    public void SingleSuspendProcessInstance() {
//        String processInstanceId = "8bdff984-ab53-11ed-9b17-f8e43b734677";
//        ProcessInstance processInstance = runtimeService
//                .createProcessInstanceQuery()
//                .processInstanceId(processInstanceId)
//                .singleResult();
//        //获取到当前流程定义是否为暂停状态   suspended方法为true代表为暂停   false就是运行的
//        boolean suspended = processInstance.isSuspended();
//        if (suspended) {
//            runtimeService.activateProcessInstanceById(processInstanceId);
//            System.out.println("流程实例:" + processInstanceId + "激活");
//        } else {
//            runtimeService.suspendProcessInstanceById(processInstanceId);
//            System.out.println("流程实例:" + processInstanceId + "挂起");
//        }
//    }
//
//
//    @Test
//    /**
//     * 挂起全部流程实例
//     */
//    public void suspendProcessInstanceAll() {
//        //获取流程实例的对象
//        ProcessDefinition qingjia = repositoryService
//                .createProcessDefinitionQuery()
//                .processDefinitionKey("process")
//                .singleResult();
//        // 获取到当前流程定义是否为暂停状态 suspended方法为true是暂停的，suspended方法为false是运行的
//        boolean suspended = qingjia.isSuspended();
//        if (suspended) {
//            // 暂定状态,那就可以激活
//            //                                    参数1:流程定义的id  参数2:是否要激活    参数3:时间点
//            repositoryService.activateProcessDefinitionById(qingjia.getId(), true, null);
//            System.out.println("流程定义:" + qingjia.getId() + "激活");
//        } else {
//            repositoryService.suspendProcessDefinitionById(qingjia.getId(), true, null);
//            System.out.println("流程定义:" + qingjia.getId() + "挂起");
//        }
//    }
//
//
//    /**
//     * 启动流程实例，添加businessKey
//     */
//    @Test
//    public void startUpProcessAddBusinessKey(){
//        String businessKey = "1";
//        // 启动流程实例，指定业务标识businessKey，也就是请假申请单id
//        ProcessInstance processInstance = runtimeService.
//                startProcessInstanceByKey("process",businessKey);
//        // 输出
//        System.out.println("对应的业务id:"+processInstance.getBusinessKey());
//    }
//
//
//    /**
//     * 查询已处理历史任务
//     */
//    @Test
//    public void findProcessedTaskList() {
//        //张三已处理过的历史任务
//        List<HistoricTaskInstance> list = historyService
//                .createHistoricTaskInstanceQuery()
//                .taskAssignee("zhangsan")
//                .finished()
//                .list();
//        for (HistoricTaskInstance historicTaskInstance : list) {
//            System.out.println("流程实例id：" + historicTaskInstance.getProcessInstanceId());
//            System.out.println("任务id：" + historicTaskInstance.getId());
//            System.out.println("任务负责人：" + historicTaskInstance.getAssignee());
//            System.out.println("任务名称：" + historicTaskInstance.getName());
//        }
//    }
//
//    /**
//     * 完成任务
//     */
//    @Test
//    public void completTask(){
//        //获取负责人需要处理的任务对象：task
//        Task task = taskService.createTaskQuery()
//                .taskAssignee("zhangsan")  //要查询的负责人
//                .singleResult();//返回一条
//
//        //完成任务,方法的参数：任务id
//        taskService.complete(task.getId());
//    }
//
//
//    /**
//     * 查询当前个人待执行的任务
//     */
//    @Test
//    public void findPendingTaskList() {
//        //任务负责人
//        String assignee = "zhangsan";
//        //获取Task的集合
//        List<Task> list = taskService.createTaskQuery()
//                .taskAssignee(assignee)//只查询该任务负责人的任务
//                .list();
//        for (Task task : list) {
//            System.out.println("流程实例id：" + task.getProcessInstanceId());
//            System.out.println("任务id：" + task.getId());
//            System.out.println("任务负责人：" + task.getAssignee());
//            System.out.println("任务名称：" + task.getName());
//        }
//    }
//
//
//    //开启流程的实例
//    @Test
//    public void startProcess(){
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process");
//        System.out.println(processInstance.getProcessDefinitionId());
//        System.out.println(processInstance.getProcessInstanceId());
//        System.out.println(processInstance.getActivityId());
//    }
//
//
//
//    //部署方式1：单个文件部署
//    @Test
//    public void deployProcess(){
//        Deployment deploy = repositoryService.createDeployment()
//                .addClasspathResource("process/qingjia.bpmn20.xml")
//                .addClasspathResource("process/qingjia.png")
//                .name("请假申请流程")
//                .deploy();
//        System.out.println(deploy.getId());
//        System.out.println(deploy.getName());
//    }
}
