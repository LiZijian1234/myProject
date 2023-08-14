package com.atguigu.code;


public class CodeGet {

//    public static void main(String[] args) {
//
//        // 1、创建代码生成器
//        AutoGenerator mpg = new AutoGenerator();
//
//        // 2、全局配置
//        // 全局配置
//        GlobalConfig gc = new GlobalConfig();
//        //配置为service-oa的java文件夹路径
//        gc.setOutputDir("C:\\Users\\123\\Desktop\\Javanote\\oa_bangongproject\\guigu-oa-parent1\\service-oa\\src\\main\\java");
//        gc.setServiceName("%sService");	//去掉Service接口的首字母I
//        gc.setAuthor("atguigu");
//        gc.setOpen(false);
//        mpg.setGlobalConfig(gc);
//
//        // 3、数据源配置
//        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setUrl("jdbc:mysql://localhost:3306/guigu-oa?serverTimezone=GMT%2B8&useSSL=false");
//        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
//        dsc.setUsername("root");
//        dsc.setPassword("a251013211a");
//        dsc.setDbType(DbType.MYSQL);
//        mpg.setDataSource(dsc);
//
//        // 4、包配置
//        PackageConfig pc = new PackageConfig();
//        pc.setParent("com.atguigu");
//        pc.setModuleName("wechat"); //模块名
//        pc.setController("controller");
//        pc.setService("service");
//        pc.setMapper("mapper");
//        mpg.setPackageInfo(pc);
//
//        // 5、策略配置
//        StrategyConfig strategy = new StrategyConfig();
////数据库名称
////        strategy.setInclude("sys_user_role");
//        //strategy.setInclude("sys_menu","sys_role_menu");
//        //审批的两个表自动生成代码
//
////        strategy.setInclude("oa_process_type","oa_process_template");
////        strategy.setInclude("oa_process");
////        strategy.setInclude("oa_process_record");
//        strategy.setInclude("wechat_menu");
//
//        //去掉文件名的wechat前缀
//        strategy.setTablePrefix("wechat_");
//
//
//        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//
//        strategy.setRestControllerStyle(true); //restful api风格控制器
//        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//
//        mpg.setStrategy(strategy);
//
//        // 6、执行
//        mpg.execute();
//    }
}
