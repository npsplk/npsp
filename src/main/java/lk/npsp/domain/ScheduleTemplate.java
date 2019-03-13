package lk.npsp.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.npsp.domain.enumeration.Weekdays;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A ScheduleTemplate.
 */
@Entity
@Table(name = "schedule_template")
public class ScheduleTemplate implements Serializable {

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

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne
    @JsonIgnoreProperties("scheduleTemplates")
    private Vehicle vehicle;

    @ManyToOne
    @JsonIgnoreProperties("scheduleTemplates")
    private Driver driver;

    @ManyToOne
    @JsonIgnoreProperties("scheduleTemplates")
    private Route route;

    @ManyToOne
    @JsonIgnoreProperties("scheduleTemplates")
    private Bay bay;

    @ManyToMany
    @JoinTable(name = "schedule_template_weekday",
        joinColumns = @JoinColumn(name = "schedule_template_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "weekday_id", referencedColumnName = "id"))
    private Set<Weekday> weekdays = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "schedule_template_vehicle_facility",
        joinColumns = @JoinColumn(name = "schedule_template_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "vehicle_facility_id", referencedColumnName = "id"))
    private Set<VehicleFacility> vehicleFacilities = new HashSet<>();

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

    public ScheduleTemplate startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public ScheduleTemplate endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean isIsActive() {
        return isActive;
    }

    public ScheduleTemplate isActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ScheduleTemplate vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Driver getDriver() {
        return driver;
    }

    public ScheduleTemplate driver(Driver driver) {
        this.driver = driver;
        return this;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Route getRoute() {
        return route;
    }

    public ScheduleTemplate route(Route route) {
        this.route = route;
        return this;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Bay getBay() {
        return bay;
    }

    public ScheduleTemplate bay(Bay bay) {
        this.bay = bay;
        return this;
    }

    public void setBay(Bay bay) {
        this.bay = bay;
    }

    public Set<Weekday> getWeekdays() {
        return weekdays;
    }

    public ScheduleTemplate weekdays(Set<Weekday> weekdays) {
        this.weekdays = weekdays;
        return this;
    }

    public ScheduleTemplate addWeekday(Weekday weekday) {
        this.weekdays.add(weekday);
        weekday.getScheduleTemplates().add(this);
        return this;
    }

    public ScheduleTemplate removeWeekday(Weekday weekday) {
        this.weekdays.remove(weekday);
        weekday.getScheduleTemplates().remove(this);
        return this;
    }

    public void setWeekdays(Set<Weekday> weekdays) {
        this.weekdays = weekdays;
    }

    public Set<VehicleFacility> getVehicleFacilities() {
        return vehicleFacilities;
    }

    public ScheduleTemplate vehicleFacilities(Set<VehicleFacility> vehicleFacilities) {
        this.vehicleFacilities = vehicleFacilities;
        return this;
    }

    public ScheduleTemplate addVehicleFacility(VehicleFacility vehicleFacility) {
        this.vehicleFacilities.add(vehicleFacility);
        vehicleFacility.getScheduleTemplates().add(this);
        return this;
    }

    public ScheduleTemplate removeVehicleFacility(VehicleFacility vehicleFacility) {
        this.vehicleFacilities.remove(vehicleFacility);
        vehicleFacility.getScheduleTemplates().remove(this);
        return this;
    }

    public void setVehicleFacilities(Set<VehicleFacility> vehicleFacilities) {
        this.vehicleFacilities = vehicleFacilities;
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
        ScheduleTemplate scheduleTemplate = (ScheduleTemplate) o;
        if (scheduleTemplate.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), scheduleTemplate.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ScheduleTemplate{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", isActive='" + isIsActive() + "'" +
            "}";
    }
}
