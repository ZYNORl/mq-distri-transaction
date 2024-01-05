package top.zynorl.transaction.fund.payment.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @RequestMapping("/pay/{amount}")
    public void post(@PathVariable Double amount) throws SchedulerException {
        paymentService.reduceAmount(amount);
    }

}
