package com.atguigu.auth.utils;

import com.atguigu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zijianLi
 * @create 2023- 03- 18- 13:45
 */
public class MenuHelper {

    /**
     * 递归建树形菜单
     * @param sysMenuList
     * @return
     */
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        List<SysMenu> trees = new ArrayList<>();

        //
        for(SysMenu sysMenu : sysMenuList ){
            //parentId=0代表此时的sysMenu是入口，此时从入口开始list.add
            if(sysMenu.getParentId().longValue()==0){
                //下面开始调用add方法
                trees.add(getChildren(sysMenu,sysMenuList));
            }
        }
        return trees;
    }

    /**
     * 递归来获取参数sysMenu的所有的树形的结构
     * @param sysMenu
     * @param sysMenuList
     * @return
     */
    private static SysMenu getChildren(SysMenu sysMenu,
                                       List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<SysMenu>());
        //遍历所有菜单数据，判断id和parentId的对应关系
        for(SysMenu it:sysMenuList){
            if(sysMenu.getId().longValue() == it.getParentId().longValue()){
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(it, sysMenuList));
            }
        }
        return sysMenu;
    }

}
