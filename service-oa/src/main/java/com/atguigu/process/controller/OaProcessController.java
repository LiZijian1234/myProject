package com.atguigu.process.controller;


import com.atguigu.common.result.Result;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-03-26
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping(value = "/admin/process")
public class OaProcessController {
    @Autowired
    private OaProcessService oaProcessService;

    /**
     * //审批管理的条件和分页列表方法
     * @param page
     * @param limit
     * 传入processQueryVo对象作为条件查询的参数
     * @return
     */
//    @PreAuthorize("hasAuthority('bnt.process.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        ProcessQueryVo processQueryVo
                        ){
        //自定义的条件分页查询方法，不适应mp的默认的条件分页查询方法。
        //将查询结果封装到ProcessVo中
        Page<ProcessVo> processPage = new Page<>(page,limit);
        //需要自己写的方法
        IPage<ProcessVo> pageModel = oaProcessService.selectPage(processPage,processQueryVo);
        return Result.ok(pageModel);
    }









}

