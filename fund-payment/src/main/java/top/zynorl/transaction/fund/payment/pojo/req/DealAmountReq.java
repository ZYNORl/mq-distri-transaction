package top.zynorl.transaction.fund.payment.pojo.req;

import lombok.Data;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/06
 **/
@Data
public class DealAmountReq {
    /**
     * 转出卡号
     */
    private String cardNumber1;
    /**
     * 转入卡号
     */
    private String cardNumber2;
    /**
     * 交易金额
     */
    private Double amount;
}
