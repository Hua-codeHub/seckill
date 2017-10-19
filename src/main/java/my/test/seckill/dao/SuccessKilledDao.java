package my.test.seckill.dao;

import my.test.seckill.entity.SuccessKilled;

/**
 * Created by HUA on 2017/10/14.
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细，可过滤重复
     *
     * @param seckillId
     * @param userPhone
     * @return 插入的结果集数量（插入的行数）
     */
    int insertSuccessKilled(long seckillId, long userPhone);

    /**
     * 根据ID查询SuccessKilld并携带秒杀产品对象实体
     *
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(long seckillId);


}
