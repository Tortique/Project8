package Reward.service;

import Reward.domain.Attraction;
import Reward.domain.Location;
import Reward.domain.User;

public interface IRewardService {
    void calculateRewards(User user);
    boolean isWithinAttractionProximity(Attraction attraction, Location location);
    double getDistance(Location loc1, Location loc2);
    void setProximityBuffer(int proximityBuffer);
    void setDefaultProximityBuffer();

}
