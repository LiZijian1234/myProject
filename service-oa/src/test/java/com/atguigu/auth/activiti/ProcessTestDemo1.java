package com.atguigu.auth.activiti;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zijianLi
 * @create 2023- 03- 21- 22:03
 */
@SpringBootTest
public class ProcessTestDemo1 {
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
//    ////////////////监听器方法/////////////////////////////////////////////////
//    /**
//     * 部署流程定义
//     */
//    @Test
//    public void deployProcess02(){
//        // 流程部署
//        Deployment deploy = repositoryService.createDeployment()
//                .addClasspathResource("process/jiaban03.bpmn20.xml")
//                .name("加班申请流程03")
//                .deploy();
//        System.out.println(deploy.getId());
//        System.out.println(deploy.getName());
//    }
//    @Test
//    /**
//     * 创建有特定执行人的实例
//     */
//    public void startProcessInstance02(){
//        Map<String, Object> variables = new HashMap<>();
//        //设置任务人
//        variables.put("assignee1","lucy");
//        variables.put("assignee2","mary");
//        //创建流程实例,我们需要知道流程定义的key
//        ProcessInstance processInstance =
//                runtimeService.startProcessInstanceByKey("jiaban03", variables);
//        //输出实例的相关信息
//        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
//        System.out.println("流程实例id：" + processInstance.getId());
//    }
//
//
//    /////////////////////////////////////////////////////////////////////////////////
//
//
//
//    /**
//     * 部署流程定义
//     */
//    @Test
//    public void deployProcess(){
//        // 流程部署
//        Deployment deploy = repositoryService.createDeployment()
//                .addClasspathResource("process/jiaban.bpmn20.xml")
//                .name("加班申请流程")
//                .deploy();
//        System.out.println(deploy.getId());
//        System.out.println(deploy.getName());
//    }
//    @Test
//    /**
//     * 创建有特定执行人的实例
//     */
//    public void startProcessInstance(){
//        Map<String, Object> variables = new HashMap<>();
//        //设置任务人
//        variables.put("assignee1","lucy");
//        variables.put("assignee2","mary");
//        //创建流程实例,我们需要知道流程定义的key
//        ProcessInstance processInstance =
//                runtimeService.startProcessInstanceByKey("jiaban", variables);
//        //输出实例的相关信息
//        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
//        System.out.println("流程实例id：" + processInstance.getId());
//    }
}
