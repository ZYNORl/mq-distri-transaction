package top.zynorl.transaction.fund.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FundPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(FundPaymentApplication.class, args);
    }

}
