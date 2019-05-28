package lk.npsp.web.rest;

import jdk.nashorn.internal.runtime.options.Option;
import lk.npsp.domain.*;
import lk.npsp.repository.*;
import lk.npsp.service.ResourceLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * REST controller for managing DataImport.
 */
@RestController
@RequestMapping("/api/data-import")
public class DataImportResource {

    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;
    private final RouteLocationRepository routeLocationRepository;
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final WeekdayRepository weekdayRepository;
    private final VehicleRepository vehicleRepository;
    private final BayRepository bayRepository;
    private final TransportTypeRepository transportTypeRepository;
    private final ResourceLocator resourceLocator;

    public DataImportResource(TransportTypeRepository transportTypeRepository,BayRepository bayRepository,VehicleRepository vehicleRepository, WeekdayRepository weekdayRepository, ScheduleTemplateRepository scheduleTemplateRepository, RouteLocationRepository routeLocationRepository, LocationRepository locationRepository, RouteRepository routeRepository, ResourceLocator resourceLocator) {
        this.locationRepository = locationRepository;
        this.routeRepository = routeRepository;
        this.routeLocationRepository = routeLocationRepository;
        this.scheduleTemplateRepository = scheduleTemplateRepository;
        this.weekdayRepository = weekdayRepository;
        this.resourceLocator = resourceLocator;
        this.vehicleRepository = vehicleRepository;
        this.bayRepository=bayRepository;
        this.transportTypeRepository=transportTypeRepository;
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
        List<List<String>> dictionaryArray = resourceLocator.locateResource(
            "import-data/locations.csv", ",");

        for (List<String> dictionaryItem : dictionaryArray) {
            Location location = new Location();
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
        List<List<String>> dictionaryArray = resourceLocator.locateResource(
            "import-data/routes.csv", ",");

        for (List<String> dictionaryItem : dictionaryArray) {
            Route route = new Route();
            route.setId(Long.parseLong(dictionaryItem.get(0)));
            route.setRouteName(dictionaryItem.get(1));
            Route result = routeRepository.save(route);


            //add route locations
            Optional<Location> location1 = locationRepository.findById(Long.parseLong(dictionaryItem.get(2)));
            Optional<Location> location2 = locationRepository.findById(Long.parseLong(dictionaryItem.get(3)));

            if (location1.isPresent()) {
                RouteLocation routeLocation1 = new RouteLocation();
                routeLocation1.setRoute(result);
                routeLocation1.setSequenceNumber(Long.parseLong("1"));
                routeLocation1.setLocation(location1.get());
                routeLocationRepository.save(routeLocation1);
            }

            if (location2.isPresent()) {
                RouteLocation routeLocation2 = new RouteLocation();
                routeLocation2.setRoute(result);
                routeLocation2.setSequenceNumber(Long.parseLong("2"));
                routeLocation2.setLocation(location2.get());
                routeLocationRepository.save(routeLocation2);
            }

        }

        return ResponseEntity.ok().body(routeRepository.findAll());
    }

    /**
     * GET  /schedules : import schedules from the resource directory
     *
     * @return the ResponseEntity with status 200 (OK) and success message
     * @throws IOException if reading resource file is not successful
     */
    @CrossOrigin
    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleTemplate>> importScheduleData(HttpServletRequest request) throws IOException {
        List<List<String>> dictionaryArray = resourceLocator.locateResource(
            "import-data/schedules.csv", ",");

        for (List<String> dictionaryItem : dictionaryArray) {
            ScheduleTemplate scheduleTemplate = new ScheduleTemplate();

            // add weekdays
            String[] weekdays = dictionaryItem.get(1).split(";");
            for (String weekday : weekdays) {
                Optional<Weekday> day = weekdayRepository.findById(Long.parseLong(weekday));
                if (day.isPresent()) {
                    scheduleTemplate.addWeekday(day.orElse(null));
                }
            }

            //add route
            Optional<Route> route = routeRepository.findById(Long.parseLong(dictionaryItem.get(2)));
            if (route.isPresent()) {
                scheduleTemplate.setRoute(route.orElse(null));
            }
            //add vehicle
            Optional<Vehicle> vehicle = vehicleRepository.findOneByRegistrationNumber(dictionaryItem.get(3));
            if (vehicle.isPresent()) {
                scheduleTemplate.setVehicle(vehicle.orElse(null));
            }else{
                Vehicle newVehicle= new Vehicle();
                Optional<TransportType> transportType= transportTypeRepository.findById(Long.parseLong("1"));
                newVehicle.setRegistrationNumber(dictionaryItem.get(3));
                newVehicle.setNumberOfSeats(52);
                newVehicle.setTransportType(transportType.orElse(null));
                vehicleRepository.save(newVehicle);
                scheduleTemplate.setVehicle(newVehicle);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            //departure time and arrival time
            String departureDate = "2019-02-08 " + dictionaryItem.get(4);
            String arrivalDate = "2019-02-08 " + dictionaryItem.get(5);

            Instant departure = LocalDateTime.parse(departureDate, formatter)
                .atZone(
                    ZoneId.systemDefault()
                )
                .toInstant();

            Instant arrival = LocalDateTime.parse(arrivalDate, formatter)
                .atZone(
                    ZoneId.systemDefault()
                )
                .toInstant();

            scheduleTemplate.setStartTime(departure);
            scheduleTemplate.setEndTime(arrival);

            //bay
            Optional<Bay> bay = bayRepository.findById(Long.parseLong(dictionaryItem.get(6)));
            if(bay.isPresent()){
                scheduleTemplate.setBay(bay.orElse(null));
            }

            scheduleTemplate.setIsActive(true);
            scheduleTemplateRepository.save(scheduleTemplate);
        }

        return ResponseEntity.ok().body(scheduleTemplateRepository.findAll());
    }

}
