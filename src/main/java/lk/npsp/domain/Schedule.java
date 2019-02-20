package lk.npsp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Schedule.
 */
@Entity
@Table(name = "schedule")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "schedule")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Route route;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Location startLocation;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Location endLocation;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "schedule_weekdays",
               joinColumns = @JoinColumn(name = "schedules_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "weekdays_id", referencedColumnName = "id"))
    private Set<Weekday> weekdays = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "schedule_vehicle_facility",
               joinColumns = @JoinColumn(name = "schedules_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "vehicle_facilities_id", referencedColumnName = "id"))
    private Set<VehicleFacility> vehicleFacilities = new HashSet<>();

    @OneToMany(mappedBy = "schedule")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Trip> trips = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Schedule startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Schedule endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Route getRoute() {
        return route;
    }

    public Schedule route(Route route) {
        this.route = route;
        return this;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Schedule startLocation(Location location) {
        this.startLocation = location;
        return this;
    }

    public void setStartLocation(Location location) {
        this.startLocation = location;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public Schedule endLocation(Location location) {
        this.endLocation = location;
        return this;
    }

    public void setEndLocation(Location location) {
        this.endLocation = location;
    }

    public Set<Weekday> getWeekdays() {
        return weekdays;
    }

    public Schedule weekdays(Set<Weekday> weekdays) {
        this.weekdays = weekdays;
        return this;
    }

    public Schedule addWeekdays(Weekday weekday) {
        this.weekdays.add(weekday);
        return this;
    }

    public Schedule removeWeekdays(Weekday weekday) {
        this.weekdays.remove(weekday);
        return this;
    }

    public void setWeekdays(Set<Weekday> weekdays) {
        this.weekdays = weekdays;
    }

    public Set<VehicleFacility> getVehicleFacilities() {
        return vehicleFacilities;
    }

    public Schedule vehicleFacilities(Set<VehicleFacility> vehicleFacilities) {
        this.vehicleFacilities = vehicleFacilities;
        return this;
    }

    public Schedule addVehicleFacility(VehicleFacility vehicleFacility) {
        this.vehicleFacilities.add(vehicleFacility);
        vehicleFacility.getSchedules().add(this);
        return this;
    }

    public Schedule removeVehicleFacility(VehicleFacility vehicleFacility) {
        this.vehicleFacilities.remove(vehicleFacility);
        vehicleFacility.getSchedules().remove(this);
        return this;
    }

    public void setVehicleFacilities(Set<VehicleFacility> vehicleFacilities) {
        this.vehicleFacilities = vehicleFacilities;
    }

    public Set<Trip> getTrips() {
        return trips;
    }

    public Schedule trips(Set<Trip> trips) {
        this.trips = trips;
        return this;
    }

    public Schedule addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setSchedule(this);
        return this;
    }

    public Schedule removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setSchedule(null);
        return this;
    }

    public void setTrips(Set<Trip> trips) {
        this.trips = trips;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Schedule schedule = (Schedule) o;
        if (schedule.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), schedule.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Schedule{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
