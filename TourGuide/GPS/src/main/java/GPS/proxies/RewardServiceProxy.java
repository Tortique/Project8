package GPS.proxies;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@FeignClient(value = "rewardService", url = "localhost:9002")
public interface RewardServiceProxy {
    @RequestMapping("/getAttractionRewardsPoints/{attractionId}/{userId}")
    int getAttractionRewardsPoints(@PathVariable("attractionId") UUID attractionId,
                                    @PathVariable("userId") UUID userId);
}
