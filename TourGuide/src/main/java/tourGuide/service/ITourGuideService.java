package tourGuide.service;

import tourGuide.domain.User;
import tourGuide.domain.UserPreferences;
import tourGuide.domain.UserReward;
import tourGuide.domain.VisitedLocation;
import tourGuide.exceptions.InvalidLocationException;
import tripPricer.Provider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITourGuideService {
    List<UserReward> getUserRewards(User user);
    VisitedLocation getUserLocation(User user) throws InvalidLocationException;
    User getUser(String userName);
    List<User> getAllUsers();
    boolean addUser(User user);
    List<Provider> getTripDeals(User user);
    CompletableFuture<?> trackUserLocation(User user);
    List<VisitedLocation> getAllCurrentLocations();
    boolean updateUserPreferences(String userName, UserPreferences userPreferences);
}
