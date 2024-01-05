package top.zynorl.transaction.fund.payment.sqlServer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ApiModelProperty("总金额")
    private Double amount;

    @ApiModelProperty("交易金额")
    private Double dealAmount;

    @ApiModelProperty("交易时间")
    private LocalDateTime dealTime;

    @ApiModelProperty("关联的事务uuid")
    private String transactionId;

}
