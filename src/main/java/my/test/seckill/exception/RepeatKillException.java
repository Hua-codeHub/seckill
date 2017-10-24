package my.test.seckill.exception;

/**
 * 重复秒杀异常（运行期异常）
 * Created by HUA on 2017/10/23.
 */
public class RepeatKillException extends SeckillException{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
