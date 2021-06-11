import Reward.domain.Attraction;
import Reward.proxies.GPSServiceProxy;
import Reward.service.RewardsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rewardCentral.RewardCentral;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
public class TestRewardsService {
    @MockBean
    GPSServiceProxy gpsServiceProxy;

    @Test
    public void isWithinAttractionProximity() {
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        Attraction attraction = attractions.get(0);
        RewardsService rewardsService = new RewardsService(gpsServiceProxy, new RewardCentral());
        assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }
}
