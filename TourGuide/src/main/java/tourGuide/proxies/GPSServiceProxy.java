package tourGuide.proxies;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tourGuide.domain.Attraction;
import tourGuide.domain.VisitedLocation;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "gpsService",url = "localhost:9001" )
public interface GPSServiceProxy {
    @GetMapping("/getNearByAttractions/{username}")
    String getNearByAttraction(@PathVariable("username") String username);

    @GetMapping("/getUserLocation/{userId}")
    VisitedLocation getUserLocation(@PathVariable("userId") UUID userID);

    @GetMapping("/getAttractions")
    List<Attraction> getAttractions();
}
