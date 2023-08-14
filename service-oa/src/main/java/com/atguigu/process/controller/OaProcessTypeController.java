package com.atguigu.process.controller;


import com.atguigu.common.result.Result;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author lizijian
 * @since 2023-03-25
 */
@Api(value = "审批类型", tags = "审批类型")//这个是swagger的注解
@RestController
@RequestMapping(value = "/admin/process/processType")//设置路径
public class OaProcessTypeController {
    @Autowired
    private OaProcessTypeService oaProcessTypeService;


    /**
     * 分页查询，根据页数和每页记录数来查询数据
     * @return
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit){
        Page<ProcessType> pageParam = new Page<>(page,limit);
        IPage<ProcessType> pageModel = oaProcessTypeService.page(pageParam);
        return Result.ok(pageModel);
    }

    //@PreAuthorize("hasAuthority('bnt.processType.list')")  spring security做接口权限的注解
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessType processType = oaProcessTypeService.getById(id);
        return Result.ok(processType);
    }

    //@PreAuthorize("hasAuthority('bnt.processType.add')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessType processType) {
        oaProcessTypeService.save(processType);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processType.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessType processType) {
        oaProcessTypeService.updateById(processType);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processType.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        oaProcessTypeService.removeById(id);
        return Result.ok();
    }

    /**
     * 获取当前全部的审批分类，用于前端增加审批的时候使用
     * @return
     */
    @ApiOperation(value = "获取全部审批分类")
    @GetMapping("findAll")
    public Result findAll() {
        return Result.ok(oaProcessTypeService.list());
    }


}

