package Reward;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;


@EnableFeignClients
@SpringBootApplication
@Configuration
public class RewardApplication {
    public static void main(String[] args) {
        SpringApplication.run(RewardApplication.class, args);
    }

    @Bean
    public RewardCentral getRewardCentral() {
        return new RewardCentral();
    }
}
