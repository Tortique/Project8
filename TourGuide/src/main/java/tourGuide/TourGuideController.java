package tourGuide;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import tourGuide.domain.UserPreferences;
import tourGuide.domain.VisitedLocation;
import tourGuide.exceptions.InvalidLocationException;
import tourGuide.proxies.GPSServiceProxy;
import tourGuide.service.TourGuideService;
import tourGuide.domain.User;
import tripPricer.Provider;

@RestController
public class TourGuideController {
    private GPSServiceProxy gpsServiceProxy;

	@Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) throws InvalidLocationException {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }

    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) {
    	return gpsServiceProxy.getNearByAttraction(userName);
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	return JsonStream.serialize(tourGuideService.getAllCurrentLocations());
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }

    @RequestMapping("/updateUserPreferences")
    public String updateUserPreferences(@RequestParam String userName, @RequestBody UserPreferences userPreferences) throws Exception {
        if (!tourGuideService.updateUserPreferences(userName,userPreferences)) {
            throw new Exception("User not found" + userName);
        }
        return "User preferences updated";
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}