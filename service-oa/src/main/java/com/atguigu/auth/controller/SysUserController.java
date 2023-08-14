package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.md5.MD5;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.SysUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-03-15
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    //用户的条件分页查询

    /**
     *
     * @param page 页数
     * @param limit 每页记录数
     * @param sysUserQueryVo 用户查询实体,内有条件查询的关键词
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysUser.list')")
    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo){
        ////创建page对象,这个Page对象是mp的对象。
        Page<SysUser> pageParam = new Page<>(page,limit);
        //封装条件，判断条件值不为空
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        //获取条件值
        String keyword = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        //判断条件值不为空
        //like模糊查询  ge大于等于 le小于等于
        if(!StringUtils.isEmpty(keyword)){
            wrapper.like(SysUser::getName,keyword);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le(SysUser::getCreateTime,createTimeEnd);
        }
        //调用方法，条件分页查询获取数据，这个page方法是mp的方法
        IPage<SysUser> page1 = sysUserService.page(pageParam, wrapper);
        return Result.ok(page1);
    }

    @ApiOperation(value = "根据id获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        //这个getById是mp的方法
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    /**
     *
     * @param user 用户类，内部有用户名密码姓名手机等
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysUser.add')")
    @ApiOperation(value = "添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        //密码在数据库存储时要加密存储,使用MD5
        String password = user.getPassword();
        String encryptPassword = MD5.encrypt(password);
        user.setPassword(encryptPassword);
        sysUserService.save(user);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.update')")
    @ApiOperation(value = "更新用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.remove')")
    @ApiOperation(value = "删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean removeById = sysUserService.removeById(id);
        if(removeById){
            return Result.ok();
        }else{
            return Result.ok("无此用户，无法删除");
        }
    }

//    用户状态：状态（1：正常 0：停用），当用户状态为正常时，可以访问后台系统，当用户状态停用后，不可以登录后台系统
    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }
}

