package top.zynorl.transaction.fund.payment.sqlServer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * 银行卡1，用于支付
 * </p>
 *
 * @author zynorl/niuzy
 * @since 2024-01-05
 */
@Data
@Builder
@ApiModel(value = "Bankcard1对象", description = "银行卡1，用于支付")
public class Bankcard1DO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("交易自增Id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("银行卡余额")
    private Double amount;

    @ApiModelProperty("单次交易额度")
    private Double dealAmount;

    @ApiModelProperty("交易时间")
    private LocalDateTime dealTime;

    @ApiModelProperty("关联事务uuid")
    private String transactionId;

}
