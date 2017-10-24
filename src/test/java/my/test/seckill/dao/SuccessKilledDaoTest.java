package my.test.seckill.dao;

import my.test.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by HUA on 2017/10/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉JUnit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    //注入DAO实现类依赖
    @Resource
    private SuccessKilledDao successKilledDao;


    @Test
    public void testInsertSuccessKilled() throws Exception {
        long id = 1001L;
        long phone = 18298355267L;
        int insertCount = successKilledDao.insertSuccessKilled(id,phone);
        System.out.println("insertCount = " + insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        long id = 1001L;
        long phone = 18298355267L;
        SuccessKilled successKilled =  successKilledDao.queryByIdWithSeckill(id,phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}