package top.zynorl.transaction.fund.payment.sqlServer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.zynorl.transaction.fund.payment.sqlServer.dao.TransactionRecordDAO;
import top.zynorl.transaction.fund.payment.sqlServer.entity.TransactionRecordDO;
import top.zynorl.transaction.fund.payment.sqlServer.service.ITransactionRecordService;

/**
 * <p>
 * 事务记录表 服务实现类
 * </p>
 *
 * @author zynorl/niuzy
 * @since 2024-01-05
 */
@Service
public class TransactionRecordServiceImpl extends ServiceImpl<TransactionRecordDAO, TransactionRecordDO> implements ITransactionRecordService {

}
