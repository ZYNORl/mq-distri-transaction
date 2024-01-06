package top.zynorl.transaction.fund.collection.sqlServer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 银行卡2，用于收款
 * </p>
 *
 * @author zynorl/niuzy
 * @since 2024-01-05
 */
@Data
@Builder
@ApiModel(value = "Bankcard2对象", description = "银行卡2，用于收款")
public class Bankcard2DO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("交易自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("银行卡号")
    private String cardNumber;

    @ApiModelProperty("总金额")
    private Double amount;

    @ApiModelProperty("关联的事务uuid")
    private String transactionId;

}
