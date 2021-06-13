package Reward.proxies;

import Reward.domain.Attraction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "gpsService", url = "localhost:9001")
public interface GPSServiceProxy {
    @RequestMapping("/getAttractions")
    List<Attraction> getAttractions();
}
