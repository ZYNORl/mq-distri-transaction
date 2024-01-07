package top.zynorl.transaction.fund.payment.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import top.zynorl.transaction.fund.payment.pojo.enums.TransactionStatusEnum;
import top.zynorl.transaction.fund.payment.pojo.dto.TransactionAmountDataDTO;
import top.zynorl.transaction.fund.payment.pojo.req.DealAmountReq;
import top.zynorl.transaction.fund.payment.service.PaymentService;
import top.zynorl.transaction.fund.payment.sqlServer.entity.Bankcard1DO;
import top.zynorl.transaction.fund.payment.sqlServer.entity.TransactionRecordDO;
import top.zynorl.transaction.fund.payment.sqlServer.service.IBankcard1DOService;
import top.zynorl.transaction.fund.payment.sqlServer.service.ITransactionRecordService;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private IBankcard1DOService bankcard1DOService;
    @Autowired
    private ITransactionRecordService transactionRecordService;
    @Override
    @Transactional
    public void doDeal(DealAmountReq dealAmountReq) {
        String transactionId = UUID.randomUUID().toString(true);
        // 对bankcard1减去金额
        Bankcard1DO bankcard1DO = bankcard1DOService.getOne(new QueryWrapper<Bankcard1DO>().eq("card_number", dealAmountReq.getCardNumber1()));
        if(bankcard1DO!=null && bankcard1DO.getAmount()-dealAmountReq.getAmount()>=0){
            bankcard1DO.setAmount(bankcard1DO.getAmount()-dealAmountReq.getAmount());
            bankcard1DO.setTransactionId(transactionId);
            bankcard1DOService.updateById(bankcard1DO);
            // 对transactionRecord新增事务记录
            TransactionAmountDataDTO dataDTO = TransactionAmountDataDTO.builder()
                    .cardNumber1(dealAmountReq.getCardNumber1()).cardNumber2(dealAmountReq.getCardNumber2()).amount(dealAmountReq.getAmount()).build();
            String jsonStrData = JSONUtil.toJsonStr(dataDTO);
            TransactionRecordDO transactionRecordDO = TransactionRecordDO.builder()
                    .transactionId(transactionId).data(jsonStrData).status(TransactionStatusEnum.STARTED.getCode()).build();
            transactionRecordService.save(transactionRecordDO);
        }else{
            throw new TransactionSystemException("无银行卡1信息或小于待操作amount："+dealAmountReq.getAmount());
        }
    }
}
