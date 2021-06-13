package GPS.service;


import GPS.domain.Location;
import GPS.domain.NearByAttraction;
import GPS.domain.VisitedLocation;
import GPS.proxies.RewardServiceProxy;
import GPS.util.GetDistance;
import GPS.util.GpsUtil;
import org.springframework.stereotype.Service;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GPSService implements IGPSService {
    private final RewardServiceProxy rewardServiceProxy;
    private final GetDistance getDistance;
    private final GpsUtil gpsUtil;

    public GPSService(RewardServiceProxy rewardServiceProxy, GetDistance getDistance, GpsUtil gpsUtil) {
        this.rewardServiceProxy = rewardServiceProxy;
        this.getDistance = getDistance;
        this.gpsUtil = gpsUtil;
    }

    public List<NearByAttraction> getNearByAttractions(VisitedLocation visitedLocation) {
        return gpsUtil.getAttractions().stream()
                .map(attraction ->
                {
                    NearByAttraction nearByAttraction = new NearByAttraction();
                    Location attractionLocation = new Location(attraction.longitude, attraction.latitude);
                    nearByAttraction.setName(attraction.attractionName);
                    nearByAttraction.setAttractionLocation(attractionLocation);
                    nearByAttraction.setUserLocation(visitedLocation.location);
                    nearByAttraction.setDistance(getDistance.getDistance(attractionLocation, visitedLocation.location));
                    nearByAttraction.setRewardPoints(rewardServiceProxy.getAttractionRewardsPoints(attraction.attractionId, visitedLocation.userId));
                    return nearByAttraction;
                }).sorted(Comparator.comparingDouble(NearByAttraction::getDistance)).collect(Collectors.toList()).subList(0,5);
    }
}
