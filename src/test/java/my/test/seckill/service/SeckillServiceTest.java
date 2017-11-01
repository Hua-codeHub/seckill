package my.test.seckill.service;

import my.test.seckill.dto.Exposer;
import my.test.seckill.dto.SeckillExecution;
import my.test.seckill.entity.Seckill;
import my.test.seckill.exception.RepeatKillException;
import my.test.seckill.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by HUA on 2017/10/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
    }

    @Test
    public void getById() throws Exception {
        Long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}", exposer);
    }

    @Test
    public void executeSeckill() throws Exception {
        long id = 1000L;
        long phone = 18598355269L;
        String md5 = "654be20b81f7473a62be92f2c97ed622";
        try {
            SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
            logger.info("result={}", execution);
        } catch (RepeatKillException e) {
            logger.info(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.info(e.getMessage());
        }
    }

    //测试代码完整逻辑，注意可重复执行
    @Test
    public void testSeckillLogic() throws Exception{
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()){
            logger.info("exposer={}", exposer);
            long phone = 18598355279L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("result={}", execution);
            } catch (RepeatKillException e) {
                logger.info(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.info(e.getMessage());
            }
        }else {
            //秒杀未开启
            logger.warn("exposer={}", exposer);
        }
    }

}