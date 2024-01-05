package top.zynorl.transaction.fund.collection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.zynorl.transaction.fund.collection.service.CollectionService;
import top.zynorl.transaction.fund.collection.sqlServer.entity.Bankcard2DO;
import top.zynorl.transaction.fund.collection.sqlServer.service.IBankcard2DOService;

import java.util.List;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
@Service
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    private IBankcard2DOService bankcard2DOService;
    @Override
    public void addAmount(Double amount) throws SchedulerException {
        // 对bankcard1减去金额
        QueryWrapper<Bankcard2DO> queryWrapperByOrder = new QueryWrapper<Bankcard2DO>().orderByDesc("deal_Time");
        List<Bankcard2DO> bankcard2DOS = bankcard2DOService.list(queryWrapperByOrder);
        if(!CollectionUtils.isEmpty(bankcard2DOS)) {
            Bankcard2DO bankcard2DO = Bankcard2DO.builder()
                    .dealAmount(amount).amount(bankcard2DOS.get(0).getAmount() - amount).transactionId("tran_id").build();
            bankcard2DOService.save(bankcard2DO);
        }
    }
}
