package tourGuide.proxies;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tourGuide.domain.Attraction;
import tourGuide.domain.VisitedLocation;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "gpsService",url = "localhost:9001" )
public interface GPSServiceProxy {
    @RequestMapping("/getNearByAttraction/{username}")
    String getNearByAttraction(@PathVariable("username") String username);

    @RequestMapping("/getUserLocation/{userId}")
    VisitedLocation getUserLocation(@PathVariable("userId") UUID userID);

    @RequestMapping("/getAttractions")
    List<Attraction> getAttractions();
}
