package lk.npsp.web.rest;

import jdk.nashorn.internal.runtime.options.Option;
import lk.npsp.domain.Location;
import lk.npsp.domain.Route;
import lk.npsp.domain.RouteLocation;
import lk.npsp.repository.LocationRepository;
import lk.npsp.repository.RouteLocationRepository;
import lk.npsp.repository.RouteRepository;
import lk.npsp.service.ResourceLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing DataImport.
 */
@RestController
@RequestMapping("/api/data-import")
public class DataImportResource {

    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;
    private final RouteLocationRepository routeLocationRepository;
    private final ResourceLocator resourceLocator;

    public DataImportResource(RouteLocationRepository routeLocationRepository,LocationRepository locationRepository, RouteRepository routeRepository, ResourceLocator resourceLocator) {
        this.locationRepository = locationRepository;
        this.routeRepository = routeRepository;
        this.routeLocationRepository= routeLocationRepository;
        this.resourceLocator = resourceLocator;
    }

    /**
     * GET  /locations : import locations from the resource directory
     *
     * @return the ResponseEntity with status 200 (OK) and success message
     * @throws IOException if reading resource file is not successful
     */
    @CrossOrigin
    @GetMapping("/locations")
    public ResponseEntity<List<Location>> importLocationData(HttpServletRequest request) throws IOException {
        List<List<String>> dictionaryArray= resourceLocator.locateResource(
            "import-data/locations.csv",",");

        for(List<String> dictionaryItem : dictionaryArray){
            Location location= new Location();
            location.setId(Long.parseLong(dictionaryItem.get(0)));
            location.setLocationName(dictionaryItem.get(1));
            location.setLocationNameSinhala(dictionaryItem.get(2));
            location.setLocationNameTamil(dictionaryItem.get(3));

            locationRepository.save(location);
        }

        return ResponseEntity.ok().body(locationRepository.findAll());
    }

    /**
     * GET  /routes : import routes from the resource directory
     *
     * @return the ResponseEntity with status 200 (OK) and success message
     * @throws IOException if reading resource file is not successful
     */
    @CrossOrigin
    @GetMapping("/routes")
    public ResponseEntity<List<Route>> importRouteData(HttpServletRequest request) throws IOException {
        List<List<String>> dictionaryArray= resourceLocator.locateResource(
            "import-data/routes.csv",",");

        for(List<String> dictionaryItem : dictionaryArray){
            Route route= new Route();
            route.setId(Long.parseLong(dictionaryItem.get(0)));
            route.setRouteName(dictionaryItem.get(1));
            Route result=routeRepository.save(route);

            Optional<Location> location1= locationRepository.findById(Long.parseLong(dictionaryItem.get(2)));
            Optional<Location> location2= locationRepository.findById(Long.parseLong(dictionaryItem.get(3)));

            if (location1.isPresent()){
                RouteLocation routeLocation1= new RouteLocation();
                routeLocation1.setRoute(result);
                routeLocation1.setSequenceNumber(Long.parseLong("1"));
                routeLocation1.setLocation(location1.get());
                routeLocationRepository.save(routeLocation1);
            }

            if (location2.isPresent()){
                RouteLocation routeLocation2= new RouteLocation();
                routeLocation2.setRoute(result);
                routeLocation2.setSequenceNumber(Long.parseLong("2"));
                routeLocation2.setLocation(location2.get());
                routeLocationRepository.save(routeLocation2);
            }

        }

        return ResponseEntity.ok().body(routeRepository.findAll());
    }

}
