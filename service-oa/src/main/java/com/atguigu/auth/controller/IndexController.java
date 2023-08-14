package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.md5.MD5;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zijianLi
 * @create 2023- 03- 15- 20:40
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;
    /**
     * 登录,分为6步
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
//        //登录的时候返回给前端的结果：code:200,data:{token:admin-token}
//        Map<String, Object> map = new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);
        //1  获取前端的用户名，密码
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        //2  根据用户名查询数据库
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(lambdaQueryWrapper);
        //3  判断用户信息是否存在
        if (sysUser == null){
            throw new GuiguException(201, "用户不存在");
        }
        //4  判断密码
        //数据库的密码是MD5加密，不可逆
        String password_db = sysUser.getPassword();
        String password_input = MD5.encrypt(password);
        if(!password_db.equals(password_input)){
            throw new GuiguException(201, "密码错误");
        }

        //5  判断用户是否被禁用
        if(sysUser.getStatus().intValue()==0){
            throw new GuiguException(201, "用户已被禁用，请联系管理员");
        }
        //6 使用jwt根据用户id和username生成token字符串返回给前端，
        // 下次前端点击登录的时候看有没有token，如果有的话直接登录，不调用login方法。
        // 请求其他接口的时候带着token来，这个token前端要保存在请求头的token中，
        // 然后请求后端时请求头加上这个token
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
    /**
     * 获取用户的信息，权限菜单等信息,返回用户可以操作的菜单、按钮
     * @return
     */
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //1. 从请求头里面获取用户信息（获取token）
        String token = request.getHeader("token");
        //2. 从token中获取用户id和用户名称
        Long userId = JwtHelper.getUserId(token);
        //3. 根据用户id查询数据库，把用户信息获取到
        SysUser sysUser = sysUserService.getById(userId);
        //4. 根据用户id获取可以操作的菜单列表：数据库的path和component
        //查询数据库，动态构建路由结构
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);
        //5  根据用户id获取获取可以操作的按钮列表：数据库的perms
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);
        //6  返回相应的数据
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("routers", routerList);
        map.put("buttons", permsList);
        return Result.ok(map);
    }
    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }



}
