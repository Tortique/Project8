package GPS.service;

import GPS.domain.NearByAttraction;
import GPS.domain.VisitedLocation;
import java.util.List;

public interface IGPSService {
    List<NearByAttraction> getNearByAttractions(VisitedLocation visitedLocation);
}
