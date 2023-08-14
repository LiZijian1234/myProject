package com.atguigu.auth.service.impl;


import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.mapper.SysRoleMenuMapper;
import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.utils.MenuHelper;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.atguigu.vo.system.MetaVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author Li Zijian
 * @since 2023-03-18
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    //递归查询当前菜单下的父菜单和子菜单
    public List<SysMenu> findNodes() {
        //1.获取所有数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        //2.构建树形结构，使用工具类
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    @Override
    /**
     * 根据id删除菜单
     * 需要判断一下有没有子菜单，有的话就不能删除
     */
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(lambdaQueryWrapper);
        if(count > 0){
            //不符合条件，抛出异常
            throw new GuiguException(201, "有子菜单，不能删除");
        }
        baseMapper.deleteById(id);
    }


    /** 这个重点
     * 查询所有菜单  查询当前角色id对应的菜单
     * @param roleId
     * @return
     */
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //1.查询所有菜单   且status=1：表示可用
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(lambdaQueryWrapper);
        //2. 根据角色id查询对应的菜单id
        LambdaQueryWrapper<SysRoleMenu> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.selectList(lambdaQueryWrapper1);
        //根据菜单id对应的对象获取到所有的菜单id
        List<Long> menuIdList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());
        //3.根据查到的菜单id与所有的菜单对象进行比较，然后把是查到的菜单id的select设置为true
        allSysMenuList.stream().forEach(item ->{
            if(menuIdList.contains(item.getId())){
                item.setSelect(true);
            }else {
                item.setSelect(false);
            }
        });
        //4.返回规定树形格式的菜单列表
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }

    /**
     * 为当前用户id修改对应的菜单
     * @param assginMenuVo
     */
    @Override
    @Transactional
    public void doAssign(AssginMenuVo assginMenuVo) {
        //1.删除当前角色id对应的菜单
        LambdaQueryWrapper<SysRoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysRoleMenu::getRoleId, assginMenuVo.getRoleId());
        sysRoleMenuMapper.delete(lambdaQueryWrapper);
        //2.为当前角色添加新的菜单,从assginMenuVo的属性里面获取新的菜单id，遍历添加到角色菜单表
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        for(Long menuId:menuIdList){
            if(StringUtils.isEmpty(menuId)){
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }

    /**
     * 根据用户id获取可以操作的菜单列表：即数据库的path和component
     * @param userId
     * @return
     */
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        //1. 判断当前用户是不是管理员,userId=1是管理员
        if(userId.longValue()==1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            //条件是所有的菜单升序显示
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        }else {
            //2. 不是管理员的话，就用户id获取可以操作的菜单列表
            //    3张表关联查询：用户角色关系表，角色菜单关系表，菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }


        //3.  把查询出的菜单列表构建成路由格式的数据格式
        //使用之前用过的菜单工具类构建成树形结构,树形结构和路由格式不太一样
        List<SysMenu> sysMenuListBuildTree = MenuHelper.buildTree(sysMenuList);
        //构建成路由格式的数据格式
        List<RouterVo> routerVoList = this.buildRounter(sysMenuListBuildTree);

        return routerVoList;
    }

    /**
     *  把List<SysMenu> menus修改成List<RouterVo>的路由格式，并返回
     * @param menus List<SysMenu> menus
     * @return
     */
    private List<RouterVo> buildRounter(List<SysMenu> menus) {
        List<RouterVo> routerVoList = new ArrayList<>();
        // menus遍历
        for(SysMenu menu : menus){
            //把menus里面的每个SysMenu都改造为routerVo格式的
            RouterVo routerVo = new RouterVo();
            routerVo.setHidden(false);
            routerVo.setAlwaysShow(false);
            routerVo.setPath(getRouterPath(menu));
            routerVo.setComponent(menu.getComponent());
            routerVo.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<SysMenu> children = menu.getChildren();
            if (menu.getType().intValue()==1){
                //说明是第1层，但是下一层也有隐藏的路由，隐藏路由的hidden是true，需要把这个隐藏的路由找出来
                List<SysMenu> hiddenList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                //是隐藏路由的不一定是一个，需要遍历，然后把这个SysMenu对象修改为router路由的格式
                for(SysMenu hiddenMenu : hiddenList){
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    //因为是隐藏路由，所以为false
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routerVoList.add(hiddenRouter);
                }
            }else {
                //children是原来的menus里面的menu的属性，如果children不为空的话，routerVo就把这个children添加进去
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        routerVo.setAlwaysShow(true);
                    }
                    //注意设置children的时候，要考虑children这一层进行递归。
                    routerVo.setChildren(buildRounter(children));
                }
            }
            routerVoList.add(routerVo);
        }
        return routerVoList;
    }

    /**
     * 根据用户id获取获取可以操作的按钮列表：数据库的perms
     * @param userId
     * @return
     */
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        //1.判断是否是管理员，如果是的话，就查询所有的
        List<SysMenu> sysMenuList = null;
        //1. 判断当前用户是不是管理员,userId=1是管理员
        if(userId.longValue()==1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            sysMenuList = baseMapper.selectList(wrapper);
        }else {
            //2. 不是管理员的话，就用户id获取可以操作的菜单列表
            //    3张表关联查询：用户角色关系表，角色菜单关系表，菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        //3.  把数据构建成list集合返回,只返回type等于2的，因为目前是操作的按钮列表，type为2
        List<String> permsList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(item -> item.getPerms())
                .collect(Collectors.toList());
        return permsList;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}
