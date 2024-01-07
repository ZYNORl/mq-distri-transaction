package top.zynorl.transaction.fund.payment.service;

import top.zynorl.transaction.fund.payment.pojo.req.DealAmountReq;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
public interface PaymentService {
    /**
     * 支付交易
     * @param dealAmountReq
     */
    void doDeal(DealAmountReq dealAmountReq);
}
