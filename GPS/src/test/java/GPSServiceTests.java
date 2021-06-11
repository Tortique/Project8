import GPS.domain.NearByAttraction;
import GPS.domain.User;
import GPS.domain.VisitedLocation;
import GPS.proxies.RewardServiceProxy;
import GPS.service.GPSService;
import GPS.util.GetDistance;
import GPS.util.GpsUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class GPSServiceTests {
    @Test
    public void getNearbyAttractions() {
        RewardServiceProxy rewardServiceProxy = (attractionId, userId) -> 0;
        GetDistance getDistance = new GetDistance();
        GpsUtil gpsUtil = new GpsUtil();
        GPSService gpsService = new GPSService(rewardServiceProxy, getDistance, gpsUtil);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());

        List<NearByAttraction> attractions = gpsService.getNearByAttractions(visitedLocation);

        assertEquals(5, attractions.size());
    }
}
