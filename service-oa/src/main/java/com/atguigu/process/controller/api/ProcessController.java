package com.atguigu.process.controller.api;

import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.result.Result;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.OaProcessTypeService;
import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 审批流管理端的controller
 * @author zijianLi
 * @create 2023- 03- 26- 16:45
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping(value="/admin/process")
@CrossOrigin  //跨域,这个要解决跨域
//之前是前端解决跨域的，现在是微信公众号实现，所以要后端解决跨域
public class ProcessController {

    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private OaProcessService oaProcessService;

    @Autowired
    private SysUserService sysUserService;

    //查询所有审批分类和每个审批分类下的模板，以list集合的形式输出
    @GetMapping("findProcessType")
    public Result findProcessType(){
        List<ProcessType> list =  oaProcessTypeService.findProcessType();
        return Result.ok(list);
    }

    /**
     * 根据审批模板的id来获取模板的信息，用来点击模板之后显示这个模板的内容
     */
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable Long processTemplateId){

        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }


//    ProcessFormVo

    /**
     * 启动员工申请的流程的一个实例
     * @param processFormVo 里面含有模板id，typeid和员工填写的表单内容
     * @return
     */
    @ApiOperation(value = "启动流程")
    @PostMapping("/startUp")
    public Result start(@RequestBody ProcessFormVo processFormVo) {
        oaProcessService.startUp(processFormVo);
        return Result.ok();
    }


    /**
     * 查询每个管理端的待处理任务
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page,limit);
        //返回对象是ProcessVo，这个ProcessVo比Process多了个名称，包含的内容更多
        IPage<ProcessVo> pageModel = oaProcessService.findPending(pageParam);
        return Result.ok(pageModel);
    }

    /**
     * 查看审批的详情信息
     * findPending方法返回的ProcessVo对象的流程id
     */
    @GetMapping("show/{id}")
    public Result show(@PathVariable Long id){
         Map<String,Object> map = oaProcessService.show(id);
        return Result.ok(map);
    }

    /**
     * 点击审批通过或者审批拒绝的接口
     */
    @ApiOperation(value = "审批")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo){
        oaProcessService.approve(approvalVo);
        return Result.ok();
    }


    /**
     * 获取当前用户已处理的流程list
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page,limit);
        IPage<ProcessVo> pageModel = oaProcessService.findProcessed(pageParam);
        return Result.ok(pageModel);
    }


    /**
     * 查询当前已发起的流程list
     * @return
     */
    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(@ApiParam(name = "page", value = "当前页码", required = true)
                                  @PathVariable Long page,
                              @ApiParam(name = "limit", value = "每页记录数", required = true)
                                  @PathVariable Long limit){
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = oaProcessService.findStarted(pageParam);
        return Result.ok(pageModel);
    }

    /**
     * 获取当前用户信息接口
     */
    @ApiOperation(value = "用户信息")
    @GetMapping("getCurrentUser")
    public Result getCurrentUser(){
        Map<String,Object> map = sysUserService.getCurrentUser();
        return Result.ok(map);
    }

}
