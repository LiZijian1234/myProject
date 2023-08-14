package com.atguigu.auth.mapper;

import com.atguigu.model.system.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author zijianLi
 * @create 2023- 03- 13- 19:52
 */
@Repository
@Mapper
//加的泛型是实体类
    //BaseMapper是mp的基础mapper，只要让mapper接口继承就行
public interface SysRoleMapper extends BaseMapper<SysRole> {

}
