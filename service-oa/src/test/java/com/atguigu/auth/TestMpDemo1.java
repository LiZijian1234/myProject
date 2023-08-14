package com.atguigu.auth;

//一定要引入这个，不能引入别的类

import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zijianLi
 * @create 2023- 03- 13- 19:54
 */
@SpringBootTest
public class TestMpDemo1 {
//    @Autowired
//    private SysRoleMapper mapper;
//    @Test
//    //查询所有记录
//    public void getAll(){
//        List<SysRole> sysRoleList = mapper.selectList(null);
//        System.out.println(sysRoleList);
//    }
//    @Test
//    public void testInsert(){
//        SysRole sysRole = new SysRole();
//        sysRole.setRoleName("角色管理员");
//        sysRole.setRoleCode("role");
//        sysRole.setDescription("角色管理员");
//        int rows = mapper.insert(sysRole);
//        System.out.println(rows);
//        System.out.println(sysRole);
//    }
//    @Test
//    public void testUpdateById(){
//        SysRole sysRole = mapper.selectById(10L);
//        sysRole.setRoleName("atguigu管理员");
//        int result = mapper.updateById(sysRole);//返回修改影响的行数
//        System.out.println(result);
//    }
//    @Test
//    public void deleteByIds(){
//        //根据id删除
//        int rows = mapper.deleteById(10);
//        System.out.println(rows);
//        //批量删除
//        List<Integer> integers = Arrays.asList(10);
//        mapper.deleteBatchIds(integers);
//    }
//    @Test
//    public void testQuery1(){
//        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.eq("role_name", "管理员");
//        List list = mapper.selectList(queryWrapper);
//        System.out.println(list);
//    }
//
//    @Test
//    public void testSelect1() {
//        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("role_code", "role");
//        List<SysRole> users = mapper.selectList(queryWrapper);
//        System.out.println(users);
//    }
//
//    @Test
//    public void testSelect2() {
//        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(SysRole::getRoleCode, "role");
//        List<SysRole> users = mapper.selectList(queryWrapper);
//        System.out.println(users);
//    }


}
