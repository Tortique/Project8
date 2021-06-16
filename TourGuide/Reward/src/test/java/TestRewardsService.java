import Reward.domain.Attraction;
import Reward.domain.Location;
import Reward.domain.User;
import Reward.domain.VisitedLocation;
import Reward.proxies.GPSServiceProxy;
import Reward.service.RewardsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rewardCentral.RewardCentral;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class TestRewardsService {
    @MockBean
    GPSServiceProxy gpsServiceProxy;

    Location location;
    Location tourEiffelLocation;
    Location arcDeTriompheLocation;
    List<Attraction> attractions;

    @Before
    public void setUp() {
        location = new Location(47.254552, 2.365215);

        Attraction tourEiffel = new Attraction("Tour Eiffel", "Paris", "France", 47.254552, 2.365215);
        tourEiffelLocation = new Location(47.254552, 2.365215);
        arcDeTriompheLocation = new Location(47.500000, 2.360000);
        attractions = new ArrayList<>();
        attractions.add(tourEiffel);

    }

    @Test
    public void calculateRewards() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), location, new Date()));

        when(gpsServiceProxy.getAttractions()).thenReturn(attractions);
        RewardsService rewardsService = new RewardsService(gpsServiceProxy, new RewardCentral());

        assertEquals(0, user.getUserRewards().size());

        rewardsService.calculateRewards(user);

        assertEquals(1, user.getUserRewards().size());
    }

    @Test
    public void isWithinAttractionProximity() {
        List<Attraction> attractions = new ArrayList<>();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        Attraction attraction = attractions.get(0);
        RewardsService rewardsService = new RewardsService(gpsServiceProxy, new RewardCentral());
        assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

    @Test
    public void getDistance() {
        RewardsService rewardsService = new RewardsService(gpsServiceProxy, new RewardCentral());
        double result = rewardsService.getDistance(tourEiffelLocation, arcDeTriompheLocation);

        assertThat(result, equalTo(16.94914486690851));
    }
}
