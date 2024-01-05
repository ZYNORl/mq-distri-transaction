package top.zynorl.transaction.fund.collection.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
@Component
public class CollectionListener {
    private static final String TOPIC = "mq-transaction";
    @KafkaListener(containerFactory = "manualImmediateListenerContainerFactory", topics = "TOPIC")
    public void grantCoupon(List<Object> message, Acknowledgment ack) {

    }
}
