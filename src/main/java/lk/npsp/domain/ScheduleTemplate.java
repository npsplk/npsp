package lk.npsp.domain;

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
 * A ScheduleTemplate.
 */
@Entity
@Table(name = "schedule_template")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "scheduletemplate")
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

    @ManyToOne
    @JsonIgnoreProperties("")
    private Route route;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "schedule_template_weekdays",
               joinColumns = @JoinColumn(name = "schedule_templates_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "weekdays_id", referencedColumnName = "id"))
    private Set<Weekday> weekdays = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "schedule_template_vehicle_facility",
               joinColumns = @JoinColumn(name = "schedule_templates_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "vehicle_facilities_id", referencedColumnName = "id"))
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

    public Set<Weekday> getWeekdays() {
        return weekdays;
    }

    public ScheduleTemplate weekdays(Set<Weekday> weekdays) {
        this.weekdays = weekdays;
        return this;
    }

    public ScheduleTemplate addWeekdays(Weekday weekday) {
        this.weekdays.add(weekday);
        return this;
    }

    public ScheduleTemplate removeWeekdays(Weekday weekday) {
        this.weekdays.remove(weekday);
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
        return this;
    }

    public ScheduleTemplate removeVehicleFacility(VehicleFacility vehicleFacility) {
        this.vehicleFacilities.remove(vehicleFacility);
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
            "}";
    }
}
