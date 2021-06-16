package GPS.controller;

import GPS.domain.Attraction;
import GPS.domain.User;
import GPS.domain.VisitedLocation;
import GPS.proxies.UserServiceProxy;
import GPS.service.GPSService;
import GPS.util.GpsUtil;
import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/gps")
public class GPSController {
    @Autowired
    UserServiceProxy userServiceProxy;

    @Autowired
    public GpsUtil gpsUtil;

    @Autowired
    public GPSService gpsService;

    @GetMapping("/getNearByAttractions/{username}")
    public String getNearByAttractions(@PathVariable("username") String userName) {
        User user = userServiceProxy.getUser(userName);
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        return JsonStream.serialize(gpsService.getNearByAttractions(visitedLocation));
    }

    @GetMapping("/getUserLocation/{userId}")
    public VisitedLocation getUserLocation(@PathVariable("userId") UUID userID) {
        return gpsUtil.getUserLocation(userID);
    }

    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return gpsUtil.getAttractions();
    }
}
