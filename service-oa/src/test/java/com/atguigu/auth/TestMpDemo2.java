package com.atguigu.auth;

//一定要引入这个，不能引入别的类

import org.springframework.boot.test.context.SpringBootTest;

/** 测试Service接口的mp
 * @author zijianLi
 * @create 2023- 03- 13- 19:54
 */
@SpringBootTest
public class TestMpDemo2 {
//    @Autowired
//    private SysRoleService sysRoleService;
//    @Test
//    //查询所有记录,直接在service层调用dao就可以
//    public void testSelectList(){
//        List<SysRole> list = sysRoleService.list();
//        System.out.println(list);
//    }
//
//    @Test
//    public void testInsert(){
//        SysRole sysRole = new SysRole();
//        sysRole.setRoleName("角色管理员");
//        sysRole.setRoleCode("role");
//        sysRole.setDescription("角色管理员");
//
//        boolean result = sysRoleService.save(sysRole);
//        System.out.println(result); //影响的行数
//        System.out.println(sysRole); //id自动回填
//    }
//
//    @Test
//    public void testUpdateById(){
//        SysRole sysRole = new SysRole();
//        sysRole.setId(1L);
//        sysRole.setRoleName("角色管理员1");
//
//        boolean result = sysRoleService.updateById(sysRole);
//        System.out.println(result);
//    }
//
//    @Test
//    public void testDeleteById(){
//        boolean result = sysRoleService.removeById(2L);
//        System.out.println(result);
//    }
//
//    @Test
//    public void testSelect1() {
//        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
//        queryWrapper.ge("role_code", "role");
//        List<SysRole> users = sysRoleService.list(queryWrapper);
//        System.out.println(users);
//    }
//
//    @Test
//    public void testSelect2() {
//        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.ge(SysRole::getRoleCode, "role");
//        List<SysRole> users = sysRoleService.list(queryWrapper);
//        System.out.println(users);
//    }
//
//
//    @Test
//    public int[] getNext()
//    {
//        String s = "ababaaab";
//        int[] next = new int[s.length()];
////        i :后缀末尾
////        j :前缀末尾,同时也是前缀的长度
//        int j = 0;
//        next[0] = 0;
//        for(int i = 1;i<s.length();i++){// 注意i从1开始
//            while(j>0 && s.charAt(i)!=s.charAt(j)){ // 前后缀不相同了
//                j = next[j-1];// 向前回退
//            }
//            if(s.charAt(i) == s.charAt(j)){// 找到相同的前后缀
//                j++;
//            }
//            next[i] = j;// 将j（前缀的长度）赋给next[i]
//        };
//        for (int i =0;i<= next.length-1;i++){
//            System.out.println(next[i]);
//        }
//
//        return next;
//    }
}
