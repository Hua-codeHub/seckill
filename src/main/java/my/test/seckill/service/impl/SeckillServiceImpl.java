package my.test.seckill.service.impl;

import my.test.seckill.dao.SeckillDao;
import my.test.seckill.dao.SuccessKilledDao;
import my.test.seckill.dto.Exposer;
import my.test.seckill.dto.SeckillExecution;
import my.test.seckill.entity.Seckill;
import my.test.seckill.entity.SuccessKilled;
import my.test.seckill.enums.SeckillStatEnum;
import my.test.seckill.exception.RepeatKillException;
import my.test.seckill.exception.SeckillCloseException;
import my.test.seckill.exception.SeckillException;
import my.test.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by HUA on 2017/10/24.
 */
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SeckillDao seckillDao;

    private SuccessKilledDao successKilledDao;

    //md5盐值字符串，用于混淆md5
    private final String slat = "302rowaejfljljO(U*(@YI$KHJFI&)^&*OYHKSDFHNkldh";


    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);

        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {

        if (md5 == null || md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑：减库存 + 记录购物行为
        Date nowTime = new Date();
        try {
            //减库存
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                //没有更新到记录
                throw new SeckillCloseException("seckill is closed");
            } else {
                //记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated")
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常，转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
