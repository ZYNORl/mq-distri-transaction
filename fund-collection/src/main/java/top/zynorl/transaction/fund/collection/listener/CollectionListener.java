package top.zynorl.transaction.fund.collection.listener;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import top.zynorl.transaction.fund.collection.enums.TransactionStatusEnum;
import top.zynorl.transaction.fund.collection.pojo.dto.TransactionAmountDataDTO;
import top.zynorl.transaction.fund.collection.service.CollectionService;
import top.zynorl.transaction.fund.collection.sqlServer.entity.TransactionRecordDO;

import java.util.List;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
@Component
@Slf4j
public class CollectionListener {
    @Autowired
    private CollectionService collectionService;
    private static final String TOPIC = "mq-transaction";
    @KafkaListener(containerFactory = "manualImmediateListenerContainerFactory", topics = TOPIC)
    public void mq_transaction_listen(TransactionRecordDO message, Acknowledgment ack) {
        String TranId = message.getTransactionId();
        TransactionAmountDataDTO dataDTO = JSONUtil.toBean(message.getData(), TransactionAmountDataDTO.class);
        try {
            collectionService.doDeal(TranId, dataDTO);
        } catch (DuplicateKeyException e) {
            log.warn("重复消费，TranId=" + TranId);
        } finally {
            TransactionRecordDO transactionByTranId = collectionService.getTransactionByTranId(TranId);
            if(transactionByTranId!=null&&TransactionStatusEnum.SUCCESS.getCode().equals(transactionByTranId.getStatus())){
                ack.acknowledge();
            }
        }
    }
}
