package top.zynorl.transaction.fund.collection.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import top.zynorl.transaction.fund.collection.sqlServer.entity.TransactionRecordDO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
public class TransactionRecordDeserializer implements Deserializer<TransactionRecordDO> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public TransactionRecordDO deserialize(String topic, byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (TransactionRecordDO) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Error deserializing byte array to TransactionRecordDO", e);
        }
    }

    @Override
    public void close() {

    }
}
