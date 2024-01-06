package top.zynorl.transaction.fund.payment.pojo.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/06
 **/
@Data
@Builder
public class TransactionAmountDataDTO {
    /**
     * 转出卡号
     */
    private String cardNumber1;
    /**
     * 转入卡号
     */
    private String cardNumber2;
    /**
     * 金额
     */
    private Double amount;
}
