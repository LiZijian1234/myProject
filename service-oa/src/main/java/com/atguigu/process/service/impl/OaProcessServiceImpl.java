package com.atguigu.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessRecord;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.system.SysUser;
import com.atguigu.process.mapper.OaProcessMapper;
import com.atguigu.process.service.MessageService;
import com.atguigu.process.service.OaProcessRecordService;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.security.custom.LoginUserInfoHelper;
import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-26
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {
    @Autowired
    //activity的service注入
    private RepositoryService repositoryService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OaProcessRecordService oaProcessRecordService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MessageService messageService;

    //审批管理的条件和分页列表方法
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> processPage, ProcessQueryVo processQueryVo) {
        //自定义条件分页查询方法，因为mp默认的条件分页查询方法的参数和我们需要的参数不一样
        IPage<ProcessVo> pageModel = baseMapper.selectPage(processPage, processQueryVo);
        return pageModel;
    }

    /**
     * 根据文件部署流程定义
     * @param deployPath
     */
    @Override
    public void deployByZip(String deployPath) {
        //获取输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        //创建zipInputStream对象
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //activity部署流程定义的方法
        Deployment deploy = repositoryService
                .createDeployment()
                .addZipInputStream(zipInputStream).deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * 启动流程实例，一共6步
     * @param processFormVo
     */
    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //1.根据用户id查询用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        //2.根据审批模板id来获取模板相关信息
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processFormVo.getProcessTemplateId());
        //3.保存员工提交的审批信息到业务表oa-process
        Process process = new Process();
        //把processFormVo中的属性值赋值到process对象的属性中去
        BeanUtils.copyProperties(processFormVo, process);

        process.setStatus(1);//1表示审批中，2代表通过，-1代表驳回
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        //表单的json字符串直接就是数据库的一个字段的属性
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        baseMapper.insert(process);

        //4.启动流程实例,用到了runtimeservice，
        // 启动流程实例的startProcessInstanceByKey参数有:
        // (1)流程定义的key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        // (2)业务的key（即oa-process的id）
        String businessKey = String.valueOf(process.getId());
        // (3)流程参数，这个是员工提交的表单,需要把原来的json数据转为map集合
        String formValues = processFormVo.getFormValues();
        //员工提交的表单json的key是fromData
        JSONObject jsonObject = JSON.parseObject(formValues);
        //把json转为对象之后，获取fromData属性
        JSONObject formData = jsonObject.getJSONObject("formData");
        //遍历formData转为map，key是属性名，value是属性值
        Map<String,Object> map = new HashMap<>();
        //这种方式遍历formData
        for(Map.Entry<String,Object> entry : formData.entrySet()){
            map.put(entry.getKey(), entry.getValue());
        }

        Map<String,Object> variables = new HashMap<>();
        variables.put("data", map);
        //3个参数都有了，启动流程实例
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        //5.查询当前流程的下一个审批人，要给下一个审批人发送推送消息
        //审批人可能有多个
        //getCurrentTaskList方法是自己写的方法
        List<Task> list = this.getCurrentTaskList(processInstance.getId());
        //下面要推送的真实姓名存在nameList里面
        List<String> nameList = new ArrayList<>();
        for (Task task:list){
            String assigneeName = task.getAssignee();//这个assigneeName是登录名称，推送消息要获取用户
            // 的实际名称
            //user是下一个审批人
            SysUser user = sysUserService.getUserByUserName(assigneeName);
            String realName = user.getName();
            nameList.add(realName);
            // 通过公众号推送消息，后序完善
            messageService.pushPendingMessage(process.getId(), user.getId(), task.getId());
        }


        //6.把业务和流程进行最终的关联，把数据保存在数据库oa-process中，
        // 因为之前insert的process对象很多属性没添加
        process.setProcessInstanceId(processInstance.getId());
        //StringUtils.join(nameList.toArray(),",")可以把list变为数组，然后每个元素之间添加一个逗号
        process.setDescription(
                "等待"+
                        StringUtils.join(nameList.toArray(),",")
                        + "审批");
        //数据保存在数据库oa-process中
        baseMapper.updateById(process);
        //启动流程实例要把记录放到record里面去
        oaProcessRecordService.record(process.getId(),1,"发起申请");
    }



    /**
     * 根据流程实例的id来查询任务列表，这个方法有上面的startUp来调用
     * @param id
     * @return
     */
    private List<Task> getCurrentTaskList(String id) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(id).list();
        return list;
    }


    /**
     * 审批人查询用户待处理的任务列表
     * @param pageParam
     * @return
     */
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        //1. 查询审批人是谁。登录用户。根据登录用户名查询
        TaskQuery taskQuery = taskService.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();//降序
        //2. 调用方法分页条件查询，返回list集合
        //listPage的第一个参数是开始位置，第二个是每页记录数
        int start = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int)pageParam.getSize();
        //分页获得待处理列表的集合
        List<Task> taskList = taskQuery.listPage(start, size);
        long count = taskQuery.count();//查询的总数据有多少条。这个要作为IPage的第三个参数
        //3. 把list集合封装到List<ProcessVo>
        //List<ProcessVo> ->  List<Task>
        List<ProcessVo> processVoList = new ArrayList<>();
        for(Task task: taskList){
            //1. 从task获取流程实例id
            String processInstanceId = task.getProcessInstanceId();
            //2. 根据流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            //3. 从流程实例对象获取业务key,这个业务key就是process表中的id
            String businessKey = processInstance.getBusinessKey();
            //4. 根据业务key获取process对象
            Long processId = Long.parseLong(businessKey);
            Process process = baseMapper.selectById(processId);

            //5. process对象转为processVo对象
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(task.getId());

            processVoList.add(processVo);
        }

        //4. 封装返回IPage对象
        //参数1：当前页   参数2：每页记录数  参数3：
        IPage<ProcessVo> iPage = new Page<>
                (pageParam.getCurrent(),pageParam.getSize(),count);
        iPage.setRecords(processVoList);
        return iPage;
    }

    /**
     * 根据流程id查看审批的详情信息的方法
     * @param id 流程id
     * @return
     */
    @Override
    public Map<String, Object> show(Long id) {
        //1.根据流程id获取流程信息Process，查oa-process表
        Process process = baseMapper.selectById(id);
        //2. 根据流程id获取流程记录信息，查oa-processrecord
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId,id);
        List<ProcessRecord> processRecordList = oaProcessRecordService.list(wrapper);
        //3.Process内有一个模板id，根据模板id查询模板信息，查oa-process-template
        ProcessTemplate processTemplate =
                oaProcessTemplateService.getById(process.getProcessTemplateId());
        //4. 需要判断当前用户是否可以审批，判断审批人是不是当前用户
        //可以看到信息的不一定能审批，且不能重复审批
        Boolean isApprove = false;//当前用户是不是可以审批
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        for(Task task:taskList){
            if (task.getAssignee().equals(LoginUserInfoHelper.getUsername())){
                isApprove = true;
            }
        }
        //5. 封装到map中
        Map<String,Object> map = new HashMap<>();
        map.put("isApprove", isApprove);
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        return map;
    }

    /**
     * 点击审批通过或者审批拒绝的接口
     * @param approvalVo
     */
    @Override
    public void approve(ApprovalVo approvalVo) {
        //1. 从approvalVo获取到任务id，根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String,Object> entry:variables.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        //2. 判断审批状态值
        //    =1 表示审批通过
        if (approvalVo.getStatus()==1){
            //通过
            Map<String, Object> variable = new HashMap<>();
            taskService.complete(taskId,variable);
        }else {
            //不通过//   =-1驳回，流程直接结束
            this.endTask(taskId);//自己调用的方法
        }
        //3. 记录审批相关过程信息，到oa-process-record
        String description = approvalVo.getStatus().intValue() == 1 ? "审批人通过" : "审批人驳回";
        oaProcessRecordService.record(approvalVo.getProcessId(),
                                    approvalVo.getStatus(),
                                    description);
        //4. 查询当前流程中的下一个审批人是谁,更新process表的记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        //判断currentTaskList是不是空
        if (!CollectionUtil.isEmpty(currentTaskList)){
            //不为空，有下一个审批人，可能有多个
            List<String> assignList = new ArrayList<>();
            for(Task task:currentTaskList){
                String assignee = task.getAssignee();//得到审批人的登录名称username，审批是通过登录名称来审批的
                //根据username获取真实姓名 name
                SysUser sysUser = sysUserService.getUserByUserName(assignee);
                assignList.add(sysUser.getName());
                //5.  微信公众号方式推送消息
                // TODO
                //推送消息给下一个审批人
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }
            //更新process流程信息
            process.setDescription("等待" + StringUtils.join(assignList.toArray(), ",") + "审批");
            process.setStatus(1);
        }else {
            //此时没有下一个审批人了
            //此时流程完成，同意的话就通过，-1的话驳回
            if (approvalVo.getStatus().intValue() == 1){
                process.setDescription("审批流程全部完成，通过");
                process.setStatus(2);
            }else{
                process.setDescription("审批流程全部完成，驳回");
                process.setStatus(-1);
            }
        }
        //推送消息给申请人
        messageService.pushProcessedMessage(process.getId(), process.getUserId(), approvalVo.getStatus());
        //更新process表的记录
        baseMapper.updateById(process);
    }



    /**
     * 审批不通过时直接结束流程
     * @param taskId
     */
    private void endTask(String taskId) {
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }

    /**
     * 查询已处理的任务，通过activity的historyservice可以查询当前用户操作的历史信息
     * @param pageParam
     * @return
     */
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        //1. 封装查询条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskAssignee(LoginUserInfoHelper
                .getUsername()).finished().orderByTaskCreateTime().desc();
        //调用方法来条件分页查询，返回list
        int begin = (int)((pageParam.getCurrent()-1)*pageParam.getSize());
        int size = (int) pageParam.getSize();
                //这个listPage是activiti的方法,两个参数，一个是开始页数，一个是每页记录数
        List<HistoricTaskInstance> list = query.listPage(begin, size);
        long totalCount = query.count();
        //3.遍历list，封装结果IPage<ProcessVo>
        List<ProcessVo> processVoList = new ArrayList<>();
        for(HistoricTaskInstance item : list){
            //获取流程实例的id
            String processInstanceId = item.getProcessInstanceId();
            //根据流程实例的id查询获取的process的信息
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId,processInstanceId);
            Process process = baseMapper.selectOne(wrapper);

            //把process转换为processVo
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);

            processVoList.add(processVo);
        }
        //3个参数，current，size，total
        IPage<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(),pageParam.getSize(),totalCount);
        pageModel.setRecords(processVoList);//把已处理的数据放进去
        return pageModel;
    }

    @Override
    /**
     * 查询当前已发起的流程list
     */
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        //selectPage这个是之前写的条件分页查询的，可以在这个方法里面调用
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam, processQueryVo);//ProcessQueryVo
        return pageModel;
    }




}
