package my.test.seckill.dto;

import my.test.seckill.entity.SuccessKilled;
import my.test.seckill.enums.SeckillStatEnum;

/**
 * 封装秒杀执行后的结果
 * Created by HUA on 2017/10/23.
 */
public class SeckillExecution {

    private long seckillId;

    //秒杀执行结果状态
    private int state;

    //状态标识
    private String stateInfo;

    //秒杀成功对象
    private SuccessKilled successKilled;

    public  SeckillExecution(){}

    public SeckillExecution(long seckillId, SeckillStatEnum stateEnum,SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public SeckillExecution(long seckillId, SeckillStatEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }
}
