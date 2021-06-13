package tourGuide.proxies;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tourGuide.domain.User;

@FeignClient(value = "rewardService", url = "localhost:9002")
public interface RewardServiceProxy {
    @RequestMapping("/calculateRewards/{user}")
    void calculateRewards(@PathVariable("user") User user);
}
