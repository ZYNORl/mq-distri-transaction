package top.zynorl.transaction.fund.collection.service;

import org.quartz.SchedulerException;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
public interface CollectionService {
    /**
     * bankcard2加上金额
     * @param amount
     * @return
     */
    void addAmount(Double amount) throws SchedulerException;
}
