package top.zynorl.transaction.fund.collection.listener;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import top.zynorl.transaction.fund.collection.pojo.dto.TransactionAmountDataDTO;
import top.zynorl.transaction.fund.collection.pojo.enums.TransactionStatusEnum;
import top.zynorl.transaction.fund.collection.service.CollectionService;
import top.zynorl.transaction.fund.collection.sqlServer.entity.TransactionRecordDO;

import java.util.concurrent.atomic.AtomicInteger;

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
            // 处理成功后确认消息
            ack.acknowledge();
        } catch (DuplicateKeyException e) {
            log.warn("重复消费，TranId=" + TranId);
            // 对于重复消费的情况，直接确认消息并返回true（因为这是预期情况而非错误）
            ack.acknowledge();
        } catch (Exception e){
            // 其他异常时，记录错误信息并触发重试
            log.error(e.getMessage());
            // TODO
//            if (!retryService.retry(TranId, message)) {
//                // 如果重试服务决定不再重试，或者重试次数已达到上限，则确认消息
//                ack.acknowledge();
//            }
        }
    }
}
