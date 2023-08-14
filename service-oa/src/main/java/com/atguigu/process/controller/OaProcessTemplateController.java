package com.atguigu.process.controller;


import com.atguigu.common.result.Result;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-03-25
 */
@Api(value = "审批模板管理", tags = "审批模板管理")
@RestController
@RequestMapping(value = "/admin/process/processTemplate")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    /**
     * 分页查询
     * @param page
     * @param limit
     * @return
     */
    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit){
        Page<ProcessTemplate> pageParam = new Page<>(page, limit);
        //分页查询审批模板，由于数据库的是typeId对应的前端的String的类型，所以要根据id类型查询出具体的类型
        IPage<ProcessTemplate> pageModel =
                oaProcessTemplateService.selectPageProcessTemplate(pageParam);
        return Result.ok(pageModel);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        oaProcessTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        oaProcessTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        oaProcessTemplateService.removeById(id);
        return Result.ok();
    }

    /**
     * 上传流程文件的定义接口，把前端上传的文件上传到后端的文件夹中去。本接口是在新增审批的最后一步调用
     * @param file MultipartFile类型的
     * @return
     * @throws FileNotFoundException
     */
    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "上传流程定义")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException {
        //直接把前端上传的文件添加到编译的文件：target/classes/process里面去
        //首先要获取classes文件的目录位置
        String absolutePath = new File(ResourceUtils.getURL("classpath:").getPath())
                .getAbsolutePath();
        //把上传的文件放到一个文件夹里面去
        File tempFile = new File(absolutePath + "/processes/");
        if (!tempFile.exists()){
            tempFile.mkdirs();
        }
        //在当前目录下创建空的文件，来实现上传文件的写入
        String originalFilename = file.getOriginalFilename();
        File saveFile = new File(absolutePath + "/processes/" + originalFilename);
        //开始把上传的文件file写入到空文件saveFile中去
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail();
        }
        System.out.println("添加文件成功！！！！！！！！！！！！！！！！");
        //向前端返回相关的结果
        Map<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "processes/" + originalFilename);
        map.put("processDefinitionKey", originalFilename.substring(0, originalFilename.lastIndexOf(".")));
        return Result.ok(map);
    }


    /**
     * 前端上传的流程的部署
     * @param id 流程的id   数据库的字段status是1表示已经发布了，所以需要把status改为1
     * @return
     */
//    @PreAuthorize("hasAuthority('bnt.processTemplate.publish')")
    @ApiOperation(value = "发布")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id) {
        //publish是需要自己写的方法
        oaProcessTemplateService.publish(id);
        return Result.ok();
    }

//    public static void main(String[] args) {
//        try {
//
//            System.out.println(absolutePath);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }


}

