package com.atguigu.auth.service.impl;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.model.system.SysUser;
import com.atguigu.security.custom.CustomUser;
import com.atguigu.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zijianLi
 * @create 2023- 03- 19- 13:58
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    /**
     * 通过用户名加载用户的类，里面有用户对象和权限数据
     * 创建出CustomUser对象，内部一个是实体类sysUser对象，另一个是权限的list：List<SimpleGrantedAuthority>
     */
    //必须重写这个方法，因为要调用我们自己定义的符合项目的方法
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查阅获取User对象，返回自定义的用户对象UserDetails
        SysUser sysUser = sysUserService.getUserByUserName(username);
        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }
        //根据用户id获取权限的数据（sysMenuService.findUserPermsByUserId(sysUser.getId())），这个方法是之前写过的
        // 然后把权限封装成SimpleGrantedAuthority对象,因为new CustomUser的第二个参数要求是SimpleGrantedAuthority对象
        List<String> userPermsList = sysMenuService.findUserPermsByUserId(sysUser.getId());
        //创建SimpleGrantedAuthority对象的list集合
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        //把查询的权限数据进行遍历
        for(String perm : userPermsList){
            authorityList.add(new SimpleGrantedAuthority(perm.trim()));
        }
        //返回自定义的用户对象UserDetails，第二个参数为SimpleGrantedAuthority对象的list集合
        return new CustomUser(sysUser, authorityList);
    }
}
