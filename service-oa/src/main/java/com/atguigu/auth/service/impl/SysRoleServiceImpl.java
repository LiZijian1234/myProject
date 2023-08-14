package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.mapper.SysUserRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zijianLi
 * @create 2023- 03- 13- 23:26
 */
@Service
//交给spring
//ServiceImpl 这是Mybatis-Plus提供的默认Service接口实现。泛型第一个是mapper文件，第二个是实体类对象
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 为用户展示角色列表和当前用户对应的角色
     * map返回，其中一个是查询所有的角色，另一个是当前用户对应的角色id的角色对象对应的集合
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        //查询所有的角色
        //baseMapper就是mp中才有的，是SysRoleServiceImpl自动对应的mapper：SysRoleMapper
        List<SysRole> sysRoleList = baseMapper.selectList(null);


        //根据用户id在用户角色关系表里面查询对应的所有的角色id
        LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.selectList(lambdaQueryWrapper);

        //从sysUserRoleList，即用户id在用户角色关系表里面查询对应的所有的角色id，只取sysUserRoleList中的所有的角色id：existRoleIdList
//        List<Long> list = new ArrayList<>();
//        for(SysUserRole sysUserRole:sysUserRoleList){
//            Long id = sysUserRole.getRoleId();
//            list.add(id);
//        }等价于下面这一行
        List<Long> existRoleIdList = sysUserRoleList.stream().map(c->c.getRoleId()).collect(Collectors.toList());

        //对角色进行分类，把当前用户对应的角色的id集合existRoleIdList与所有角色id集合sysRoleList比较：
        //找到分配给该用户的角色对象，然后放到集合assginRoleList里面去。
        List<SysRole> assginRoleList = new ArrayList<>();
        for (SysRole role : sysRoleList) {
            //已分配，
            if(existRoleIdList.contains(role.getId())) {
                //获取到当前用户对应的角色id对应的集合
                assginRoleList.add(role);
            }
        }

        Map<String, Object> roleMap = new HashMap<>();
        //assginRoleList:当前用户对应的角色id对应的集合
        roleMap.put("assginRoleList", assginRoleList);
        //sysRoleList:所有的角色的集合
        roleMap.put("allRolesList", sysRoleList);
        //返回这个map集合，给controller使用
        return roleMap;
    }

    /**
     * 为用户分配角色
     * @param assginRoleVo:一个参数是用户id，一个参数是角色id列表,这个角色id列表是当前用户新的所有角色
     */
    @Transactional
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //删除用户当前角色
        LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUserRole::getUserId, assginRoleVo.getUserId());
        sysUserRoleMapper.delete(lambdaQueryWrapper);

        //给用户添加新的角色，添加到sysUserRole表里面去，一条一条添加
        for(Long roleId : assginRoleVo.getRoleIdList()) {
            if(StringUtils.isEmpty(roleId)) continue;
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(assginRoleVo.getUserId());
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }
    }
}
