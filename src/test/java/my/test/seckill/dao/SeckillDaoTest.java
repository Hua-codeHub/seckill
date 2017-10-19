package my.test.seckill.dao;

import my.test.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by HUA on 2017/10/19.
 * <p>
 * 配置Spring 和 JUnit整合，JUnit启动时加载SpringIOC容器
 * spring-test,junit
 */

@RunWith(SpringJUnit4ClassRunner.class)
//告诉JUnit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入DAO实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void testQueryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void testQueryAll() throws Exception {
        /*错误
            org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException: Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
           接口： List<Seckill> queryAll(int offet, int limit);
           原因：Java没有保存形参的记录：queryAll(int offet, int limit) --> queryAll(arg0, arg1);
           ** 多个参数时，需要告诉MyBatis参数的名字，这样通过 #{} 取参数时才可以识别
           * 解决方法： 使用@Param 注解
           * List<Seckill> queryAll(@Param("offset") int offet, @Param("limit") int limit);
         */

        List<Seckill> seckillList = seckillDao.queryAll(0, 100);
        for (Seckill seckill : seckillList) {
            System.out.println(seckill);
        }
    }

    @Test
    public void testReduceNumber() throws Exception {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L,killTime);
        System.out.println("updateCount" + updateCount);
    }


}