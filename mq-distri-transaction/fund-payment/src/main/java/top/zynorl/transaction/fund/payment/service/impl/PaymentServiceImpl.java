package top.zynorl.transaction.fund.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.zynorl.transaction.fund.payment.enums.TransactionStatusEnum;
import top.zynorl.transaction.fund.payment.service.PaymentService;
import top.zynorl.transaction.fund.payment.sqlServer.entity.Bankcard1DO;
import top.zynorl.transaction.fund.payment.sqlServer.entity.Bankcard2DO;
import top.zynorl.transaction.fund.payment.sqlServer.entity.TransactionRecordDO;
import top.zynorl.transaction.fund.payment.sqlServer.service.IBankcard1DOService;
import top.zynorl.transaction.fund.payment.sqlServer.service.IBankcard2DOService;
import top.zynorl.transaction.fund.payment.sqlServer.service.ITransactionRecordService;

import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private IBankcard1DOService bankcard1DOService;
    @Autowired
    private ITransactionRecordService transactionRecordService;
    @Override
    @Transactional
    public void reduceAmount(Double amount) throws SchedulerException {
        String transaction_id = UUID.randomUUID().toString();
        // 对bankcard1减去金额
        QueryWrapper<Bankcard1DO> queryWrapperByOrder = new QueryWrapper<Bankcard1DO>().orderByDesc("deal_Time");
        List<Bankcard1DO> bankcard1DOS = bankcard1DOService.list(queryWrapperByOrder);
        if(!CollectionUtils.isEmpty(bankcard1DOS) && bankcard1DOS.get(0).getAmount()-amount>=0){
            Bankcard1DO bankcard1DO = Bankcard1DO.builder()
                    .dealAmount(amount).amount(bankcard1DOS.get(0).getAmount() - amount).transactionId(transaction_id).build();
            bankcard1DOService.save(bankcard1DO);
            // 对transactionRecord新增事务记录
            TransactionRecordDO transactionRecordDO = TransactionRecordDO.builder()
                    .transactionId(transaction_id).data(amount.toString()).status(TransactionStatusEnum.STARTED.getCode()).build();
            transactionRecordService.save(transactionRecordDO);
        }else{
            throw new SchedulerException("银行卡1无金额或小于待操作amount："+amount);
        }
    }
}
