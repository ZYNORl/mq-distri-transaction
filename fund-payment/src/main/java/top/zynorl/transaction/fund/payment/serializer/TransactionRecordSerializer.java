package top.zynorl.transaction.fund.payment.serializer;


import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import top.zynorl.transaction.fund.collection.sqlServer.entity.TransactionRecordDO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
public class TransactionRecordSerializer implements Serializer<TransactionRecordDO> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, TransactionRecordDO data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(data);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Error serializing TransactionRecordDO to byte array", e);
        }
    }

    @Override
    public void close() {

    }
}
