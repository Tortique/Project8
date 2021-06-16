package tourGuide;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.domain.*;
import tourGuide.exceptions.InvalidLocationException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.proxies.GPSServiceProxy;
import tourGuide.proxies.RewardServiceProxy;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestTourGuideService {
    @MockBean
    GPSServiceProxy gpsServiceProxy;

    @MockBean
    RewardServiceProxy rewardServiceProxy;

    Location location;
    List<NearByAttraction> attractions;

    @Before
    public void setUp() {
        location = new Location(48.858331, 2.294481);

        NearByAttraction tourEiffel = new NearByAttraction("Tour Eiffel",
                new Location(48.858331, 2.294481), location, 0.10, 100);
        NearByAttraction louvre = new NearByAttraction("Musée du Louvre",
                new Location(48.861147, 2.338028), location, 1.65, 200);
        NearByAttraction lesInvalides = new NearByAttraction("Hôtel des Invalides",
                new Location(48.853241, 2.312107), location, 2.33, 300);
        NearByAttraction arcDeTriomphe = new NearByAttraction("L'arc de Triomphe",
                new Location(48.846012, 2.345924), location, 4.87, 400);
        NearByAttraction disneylandParis = new NearByAttraction("Disneyland Paris",
                new Location(48.872448, 2.776794), location, 21.18, 500);


        attractions = new ArrayList<>();
        attractions.add(tourEiffel);
        attractions.add(louvre);
        attractions.add(lesInvalides);
        attractions.add(arcDeTriomphe);
        attractions.add(disneylandParis);
    }

    @Test
    public void getUserLocation() throws InvalidLocationException {
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        Location location = new Location(
                ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D),
                ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D));

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), location, new Date()));

        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        tourGuideService.tracker.stopTracking();

        assertEquals(visitedLocation.userId, user.getUserId());
    }

    @Test
    public void getUserLocationThrowInvalidLocationException () {
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        assertThrows(InvalidLocationException.class, () -> tourGuideService.getUserLocation(user));

    }
    @Test
    public void addUser() {
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrievedUser = tourGuideService.getUser(user.getUserName());
        User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

        tourGuideService.tracker.stopTracking();

        assertEquals(user, retrievedUser);
        assertEquals(user2, retrievedUser2);
    }

    @Test
    public void addUserAlreadyExisting() {
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        boolean result = tourGuideService.addUser(user);

        assertFalse(result);
    }

    @Test
    public void getAllUsers() {
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        tourGuideService.tracker.stopTracking();

        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void getAllCurrentLocations() {
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        Location location = new Location(
                ThreadLocalRandom.current().nextDouble(-85.05112878D,
                        85.05112878D),
                ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D));

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), location, new Date()));

        List<VisitedLocation> result = tourGuideService.getAllCurrentLocations();
        tourGuideService.tracker.stopTracking();

        assertEquals(1, result.size());
    }

    @Test
    public void updateUserPreferences() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        tourGuideService.addUser(user);
        UserPreferences userPreferences = new UserPreferences(7, 5, 3, 2);

        boolean result = tourGuideService.updateUserPreferences("jon", userPreferences);
        tourGuideService.tracker.stopTracking();

        assertTrue(result);
        assertEquals(7, user.getUserPreferences().getTripDuration());
        assertEquals(5, user.getUserPreferences().getTicketQuantity());
        assertEquals(3, user.getUserPreferences().getNumberOfAdults());
        assertEquals(2, user.getUserPreferences().getNumberOfChildren());
    }

    @Test
    public void updateUserPreferencesInvalidUsername() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        tourGuideService.addUser(user);
        UserPreferences userPreferences = new UserPreferences(7, 5, 3, 2);

        boolean result = tourGuideService.updateUserPreferences("bob", userPreferences);
        tourGuideService.tracker.stopTracking();

        assertFalse(result);
        assertEquals(1, user.getUserPreferences().getTripDuration());
        assertEquals(1, user.getUserPreferences().getTicketQuantity());
        assertEquals(1, user.getUserPreferences().getNumberOfAdults());
        assertEquals(0, user.getUserPreferences().getNumberOfChildren());

    }

    @Test
    public void getTripDeals() {
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> providers = tourGuideService.getTripDeals(user);

        tourGuideService.tracker.stopTracking();

        assertEquals(5, providers.size());
    }

    @Test
    public void trackUserLocationTest() throws InterruptedException {
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsServiceProxy, rewardServiceProxy);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date());

		when(gpsServiceProxy.getUserLocation(user.getUserId())).thenReturn(visitedLocation);

        assertEquals(0, user.getVisitedLocations().size());

        tourGuideService.trackUserLocation(user);
        Thread.sleep(100);

        assertEquals(1, user.getVisitedLocations().size());
    }
}
