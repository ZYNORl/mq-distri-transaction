package top.zynorl.transaction.fund.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zynorl.transaction.fund.payment.pojo.req.DealAmountReq;
import top.zynorl.transaction.fund.payment.service.PaymentService;

/**
 * @version 1.0
 * @Author niuzy
 * @Date 2024/01/05
 **/
@RestController
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/deal")
    public void deal(@RequestBody DealAmountReq dealAmountReq) {
        paymentService.doDeal(dealAmountReq);
    }

}
