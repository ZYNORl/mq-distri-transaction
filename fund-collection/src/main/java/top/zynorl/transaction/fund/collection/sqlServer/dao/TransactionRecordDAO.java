package top.zynorl.transaction.fund.collection.sqlServer.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.zynorl.transaction.fund.collection.sqlServer.entity.TransactionRecordDO;

/**
 * <p>
 * 事务记录表 Mapper 接口
 * </p>
 *
 * @author zynorl/niuzy
 * @since 2024-01-05
 */
@Mapper
public interface TransactionRecordDAO extends BaseMapper<TransactionRecordDO> {

}
