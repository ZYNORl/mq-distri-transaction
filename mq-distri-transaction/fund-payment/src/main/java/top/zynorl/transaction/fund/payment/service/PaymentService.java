package top.zynorl.transaction.fund.payment.service;

import org.quartz.SchedulerException;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
public interface PaymentService {
    /**
     * bankcard1减去金额
     * @param amount
     * @return
     */
    void reduceAmount(Double amount) throws SchedulerException;
}
