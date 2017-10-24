# Java高并发藐视API

- 使用SpringMVC + Spring + MyBatis 的原因

1. 框架易于使用
2. 低代码侵入性
3. 成熟的社区和用户群

---

- Restful接口设计和使用
- 框架运作流程
- Controller开发技巧

https://github.com/codingXiaxw/seckill

## 创建项目

- 使用Maven命令创建web骨架项目

`
mvn archetype:create -DgroupId=my.test.seckill -DartifactId=seckill -DarchetypeArtifactId=maven-archetype-webapp
`
> 注意：Maven后期版本的archetype插件取消了create命令，改为：generate
`
mvn archetype:generate -DgroupId=my.test.seckill -DartifactId=seckill -DarchetypeArtifactId=maven-archetype-webapp
`

- 替换`web.xml`，新建的项目使用的是Servlet2.3,此版本jsp默认的el表达式不工作
解决方案：从tomcat的webapps目录下找一个示例粘贴过来

## 业务分析

- 秒杀业务流程

![秒杀业务流程](./images/1.png)

秒杀业务的核心 -->> 库存处理

- 用户针对库存业务分析

![用户针对库存业务分析](./images/2.png)

- 用户购买行为

![用户针对库存业务分析](./images/3.png)

- 关于数据落地

MySQL VS NoSQL

事物机制依然是目前最可靠的落地方案

## 秒杀系统的难点

- 难点问题-- “竞争”

![](./images/4.png)

对于MySQL，难点在于 **事物 + 行级锁**
- 事物

![事物](./images/5.png)

问题存在于 update 减库存

- 行级锁

![行级锁](./images/6.png)

- 实现功能

![天猫秒杀系统](./images/7.png)


- 只实现以下秒杀相关功能
    - 秒杀接口暴露
    - 执行秒杀
    - 相关查询

## 数据库设计

```sql
-- 数据库初始化脚本

-- 创建数据库

CREATE DATABASE seckill;

-- 使用数据库
use seckill;

-- 创建秒杀库存表
CREATE TABLE seckill(
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
`name` VARCHAR(120) NOT NULL  COMMENT '商品名称',
`number` INT NOT NULL  COMMENT '库存数量',
`start_time` TIMESTAMP NOT NULL COMMENT '秒杀开始时间',
`end_time` TIMESTAMP  NOT NULL COMMENT '秒杀结束时间',
`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id),
KEY idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- 初始化数据
INSERT  INTO seckill(name,number,start_time,end_time)
VALUES
('1000元秒杀iphone',100,'2017-10-14 00:00:00','2017-10-15 00:00:00'),
('500元秒杀ipad',200,'2017-10-14 00:00:00','2017-10-15 00:00:00'),
('300元秒杀小米4',300,'2017-10-14 00:00:00','2017-10-15 00:00:00'),
('200元秒杀红米note',400,'2017-10-14 00:00:00','2017-10-15 00:00:00');

-- 秒杀成功明细表
-- 用户登陆认证相关信息
CREATE TABLE success_killed(
`seckill_id` BIGINT NOT NULL COMMENT '秒杀商品ID',
`user_phone` BIGINT NOT NULL COMMENT '用户手机号',
`state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识：-1：无效 0：成功 1：已付款',
`create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
PRIMARY KEY (seckill_id,user_phone),/*联合主键*/
KEY idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- 为什么手写DDL
-- 记录每次上线的DDL修改
-- 上线V0.1
ALTER TABLE seckill
  DROP INDEX idx_create_time,
  ADD INDEX  idx_c_s(start_time,create_time);

-- 上线V1.2
```

## DAO层实体和接口编码

- MyBatis

![OR Mapping](./images/8.png)

- MyBatis特点

![MyBatis](./images/8.png)

- SQL写在哪？
    1. **XML提供SQL**
    2. 注解提供SQL

- 如何实现DAO接口
    1. **Mapper自动实现DAO接口**
    2. API编程方式实现DAO接口


> MyBatis的核心点之一：可以自由控制SQL

## MyBatis与Spring整合

整合目标：
1. 更少的编码
    - 只写接口，不写实现
2. 更少的配置
    - 别名 -- 包扫描
    - 配置扫描
        >自动扫描配置文件
    - dao实现
        >自动实现DAO接口
        >自动注入spring容器
3. 足够的灵活
    - 自己定制SQL
    - 自由传参
    - 结果集自动赋值

## 测试

/*错误
org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException: Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
接口： List<Seckill> queryAll(int offet, int limit);
原因：Java没有保存形参的记录：queryAll(int offet, int limit) --> queryAll(arg0, arg1);
** 多个参数时，需要告诉MyBatis参数的名字，这样通过 #{} 取参数时才可以识别
* 解决方法： 使用@Param 注解
* List<Seckill> queryAll(@Param("offset") int offet, @Param("limit") int limit);
*/

## service层

- 业务接口：站在“使用者”的角度设计接口
三个方面：方法定义粒度、参数、返回类型(return 类型/异常)


dto数据传输层  关注的是web和service的数据传递
entity 对业务的封装