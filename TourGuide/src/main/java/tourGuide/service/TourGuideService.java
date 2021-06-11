package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tourGuide.domain.*;
import tourGuide.exceptions.InvalidLocationException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.proxies.GPSServiceProxy;
import tourGuide.proxies.RewardServiceProxy;
import tourGuide.tracker.Tracker;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService implements ITourGuideService{
	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GPSServiceProxy gpsServiceProxy;
	private final RewardServiceProxy rewardServiceProxy;

	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;

	private final ExecutorService executorService = Executors.newFixedThreadPool(1000);

	public TourGuideService(GPSServiceProxy gpsServiceProxy, RewardServiceProxy rewardServiceProxy) {
		this.gpsServiceProxy = gpsServiceProxy;
		this.rewardServiceProxy = rewardServiceProxy;

		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) throws InvalidLocationException {
		List<VisitedLocation> allVisitedLocations = user.getVisitedLocations();

		if (allVisitedLocations.size() == 0) {
			throw new InvalidLocationException(
					"Not location available.");
		}
		return allVisitedLocations.get(allVisitedLocations.size() - 1);
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return new ArrayList<>(internalUserMap.values());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	@Async
	public CompletableFuture<?> trackUserLocation(User user) {
		return CompletableFuture.supplyAsync(() -> {
				return gpsServiceProxy.getUserLocation(user.getUserId());
			}, executorService)
				.thenAccept(visitedLocation -> { user.addToVisitedLocations(new VisitedLocation(visitedLocation.userId, visitedLocation.location, visitedLocation.timeVisited));
				}).thenAccept(reward -> {
					rewardServiceProxy.calculateRewards(user);
				});
	}

	public List<VisitedLocation> getAllCurrentLocations() {
		return getAllUsers().stream().map(user ->
		{
			VisitedLocation visitedLocation = user.getLastVisitedLocation();
			return visitedLocation;
		}).collect(Collectors.toList());
	}

	public boolean updateUserPreferences(String userName, UserPreferences userPreferences) {
		User user = internalUserMap.get(userName);
		if (user == null) {
			return false;
		}
		user.setUserPreferences(userPreferences);
		return true;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime())));
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
}
