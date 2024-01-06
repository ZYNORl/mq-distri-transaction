package top.zynorl.transaction.fund.collection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zynorl.transaction.fund.collection.enums.TransactionStatusEnum;
import top.zynorl.transaction.fund.collection.pojo.dto.TransactionAmountDataDTO;
import top.zynorl.transaction.fund.collection.service.CollectionService;
import top.zynorl.transaction.fund.collection.sqlServer.entity.Bankcard2DO;
import top.zynorl.transaction.fund.collection.sqlServer.entity.TransactionRecordDO;
import top.zynorl.transaction.fund.collection.sqlServer.service.IBankcard2DOService;
import top.zynorl.transaction.fund.collection.sqlServer.service.ITransactionRecordService;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
@Slf4j
@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private IBankcard2DOService bankcard2DOService;
    @Autowired
    private ITransactionRecordService transactionRecordService;


    @Override
    public TransactionRecordDO getTransactionByTranId(String tranUuid) {
        QueryWrapper<TransactionRecordDO> transactionId = new QueryWrapper<TransactionRecordDO>()
                .eq("transaction_id", tranUuid);
        return transactionRecordService.getOne(transactionId);
    }

    @Override
    @Transactional
    public void doDeal(String tranId, TransactionAmountDataDTO dataDTO) {
        Bankcard2DO bankcard2DO = bankcard2DOService.getOne(new QueryWrapper<Bankcard2DO>().eq("card_number", dataDTO.getCardNumber2()));
        bankcard2DO.setAmount(bankcard2DO.getAmount()+ dataDTO.getAmount());
        bankcard2DO.setTransactionId(tranId);
        bankcard2DOService.updateById(bankcard2DO);
        TransactionRecordDO transactionRecordDO = getTransactionByTranId(tranId);
        if(transactionRecordDO!=null){
            if(TransactionStatusEnum.SUCCESS.getCode().equals(transactionRecordDO.getStatus())){
                throw new DuplicateKeyException("重复消费异常");
            }
            transactionRecordDO.setStatus(TransactionStatusEnum.SUCCESS.getCode());
            transactionRecordService.updateById(transactionRecordDO);
            String logMeg = String.format("%s事务状态更新为:%s",tranId, TransactionStatusEnum.SUCCESS.getCode());
            log.info(logMeg);
        }else {
            throw new RuntimeException("无事务信息，消费失败");
        }
    }
}
