package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/** 给用户分配角色及更改用户状态
 * @author zijianLi
 * @create 2023- 03- 14- 10:05
 */
//api文档Swagger的注解
@Api(tags = "角色管理接口")
@RestController
//@RestController注解是springMVC提供的一个复合注解，标识在控制器的类上，就相当于为类添加了@responseBody和@controller
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;
    //http://localhost:8800/admin/system/sysRole/findAll
    //http://localhost:8800/doc.html
//    @GetMapping("/findAll")
//    public List<SysRole> findAll(){
//        List<SysRole> list = sysRoleService.list();
//        return list;
//    }
    //完成统一返回数据
    //查询所有角色
    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result findAll(){
//        try {
//            int a = 10/0;
//        }catch(Exception e) {
//            throw new GuiguException(20001,"执行自定义异常处理方法");
//        }
        List<SysRole> list = sysRoleService.list();//mp自带的方法
        return Result.ok(list);
    }

    /**
     * 条件和分页的综合查询
     * @param page 当前页数
     * @param limit 每页显示记录数
     * @param sysRoleQueryVo 条件对象(如果有条件的话就有这个参数)，在前端就是一个string ：rolename
     * @return
     */
    //PreAuthorize：权限管理3.7相关的注解，当查阅权限的时候，里面有这个值的话就可以操作这个方法，没有这个值的话就不能操作方法
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){
        //调用service的方法实现
        //1 创建Page对象，传递分页相关参数.Page对象是mp自带的
        //page 当前页  limit 每页显示记录数
        Page<SysRole> pageParam = new Page<>(page,limit);

        //2 封装mp查询条件，判断条件是否为空，不为空进行封装。就是mp的条件构造器
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)) {
            //mp的条件构造器的封装 根据sysRoleQueryVo的roleName来like模糊查询
            //SysRole::getRoleName相当于获取实体类SysRole的roleName对应在数据库中的role_name字段名
            wrapper.like(SysRole::getRoleName,roleName);
        }

        //3 调用方法实现
        IPage<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);//mp提供给service的方法
        // ，返回一个page类，page类作为data返回result
        return Result.ok(pageModel);
    }


    //添加角色
    //权限管理3.7相关的注解，当查阅权限的时候，里面有这个值的话就可以操作这个方法，没有这个值的话就不能操作方法
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("save")//RequestBody可以把ajax赋值给形参
    public Result save(@RequestBody SysRole sysRole){
        boolean is_success = sysRoleService.save(sysRole);//save是mp提供给service的
        return is_success ? Result.ok() : Result.fail();
    }

    //根据id查询
    //权限管理3.7相关的注解，当查阅权限的时候，里面有这个值的话就可以操作这个方法，没有这个值的话就不能操作方法
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")//通过@PathVariable注解，将占位符所表示的数据赋值给控制器方法的形参
    public Result getById(@PathVariable Long id){
        SysRole sys = sysRoleService.getById(id);//getById是mp提供给service的
        return Result.ok(sys);
    }
    //根据id修改
    //权限管理3.7相关的注解，当查阅权限的时候，里面有这个值的话就可以操作这个方法，没有这个值的话就不能操作方法
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole sysRole){
        boolean is_success = sysRoleService.updateById(sysRole);//updateById是mp提供给service的
        return is_success ? Result.ok() : Result.fail();
    }

    //根据id删除
    //权限管理3.7相关的注解，当查阅权限的时候，里面有这个值的话就可以操作这个方法，没有这个值的话就不能操作方法
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean is_success = sysRoleService.removeById(id);//removeById是mp提供给service的
        return is_success ? Result.ok() : Result.fail();
    }

    //批量删除
    //批量删除需要传入所有的id，前端传入数组格式，经过json来到后端转为list格式
    //权限管理3.7相关的注解，当查阅权限的时候，里面有这个值的话就可以操作这个方法，没有这个值的话就不能操作方法
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("批量根据id的List删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean is_success = sysRoleService.removeByIds(idList);//removeByIds是mp提供给service的
        return is_success ? Result.ok() : Result.fail();
    }


    /**
     * 查询所有角色，以及查询当前用户对应的角色
     * @param userId
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysUser.assignRole')")
    @ApiOperation(value = "查询用户id对应的角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        //roleMap中有两个key：
        //key： assginRoleList   value： 分配给当前用户的角色对象的集合assginRoleList
        //key： allRolesList     value： 所有的角色对象的集合 sysRoleList
        Map<String, Object> roleMap = sysRoleService.findRoleByAdminId(userId);
        return Result.ok(roleMap);
    }

    /**
     * 修改当前用户的角色
     * @param assginRoleVo
     * @return
     */
    @ApiOperation(value = "根据用户分配角色")
    @PreAuthorize("hasAuthority('bnt.sysUser.assignRole')")
    @PostMapping("/doAssign")
    //AssginRoleVo是实体类
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }


}
