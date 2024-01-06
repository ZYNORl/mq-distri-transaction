package top.zynorl.transaction.fund.collection.service;

import top.zynorl.transaction.fund.collection.pojo.dto.TransactionAmountDataDTO;
import top.zynorl.transaction.fund.collection.sqlServer.entity.TransactionRecordDO;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
public interface CollectionService {

    /**
     * 根据事务id查询事务实体
     */
    TransactionRecordDO getTransactionByTranId(String tranUuid);

    /**
     * 入账处理
     * @param tranId
     * @param dataDTO
     */
    void doDeal(String tranId, TransactionAmountDataDTO dataDTO);

}
