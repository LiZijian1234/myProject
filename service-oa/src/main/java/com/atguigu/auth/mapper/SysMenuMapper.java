package com.atguigu.auth.mapper;

import com.atguigu.model.system.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author Li Zijian
 * @since 2023-03-18
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    //不是管理员的话，就用户id获取可以操作的菜单列表
    //    3张表关联查询：用户角色关系表，角色菜单关系表，菜单表
    //这里要使用sql写
    //@Param :如果输入多个参数，必须要加
    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}
