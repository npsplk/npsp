package lk.npsp.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import lk.npsp.domain.enumeration.ScheduleState;

/**
 * A ScheduleInstance.
 */
@Entity
@Table(name = "schedule_instance")
public class ScheduleInstance implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jhi_date")
    private LocalDate date;

    @Column(name = "scheduled_time")
    private Instant scheduledTime;

    @Column(name = "actual_scheduled_time")
    private Instant actualScheduledTime;

    @Column(name = "actual_departure_time")
    private Instant actualDepartureTime;

    @Lob
    @Column(name = "special_notes")
    private String specialNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_state")
    private ScheduleState scheduleState;

    @ManyToOne
    @JsonIgnoreProperties("scheduleInstances")
    private Vehicle vehicle;

    @ManyToOne
    @JsonIgnoreProperties("scheduleInstances")
    private ScheduleTemplate scheduleTemplate;

    @ManyToOne
    @JsonIgnoreProperties("scheduleInstances")
    private Driver driver;

    @ManyToOne
    @JsonIgnoreProperties("scheduleInstances")
    private Route route;

    @ManyToOne
    @JsonIgnoreProperties("scheduleInstances")
    private Bay bay;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public ScheduleInstance date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Instant getScheduledTime() {
        return scheduledTime;
    }

    public ScheduleInstance scheduledTime(Instant scheduledTime) {
        this.scheduledTime = scheduledTime;
        return this;
    }

    public void setScheduledTime(Instant scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Instant getActualScheduledTime() {
        return actualScheduledTime;
    }

    public ScheduleInstance actualScheduledTime(Instant actualScheduledTime) {
        this.actualScheduledTime = actualScheduledTime;
        return this;
    }

    public void setActualScheduledTime(Instant actualScheduledTime) {
        this.actualScheduledTime = actualScheduledTime;
    }

    public Instant getActualDepartureTime() {
        return actualDepartureTime;
    }

    public ScheduleInstance actualDepartureTime(Instant actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
        return this;
    }

    public void setActualDepartureTime(Instant actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public ScheduleInstance specialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
        return this;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    public ScheduleState getScheduleState() {
        return scheduleState;
    }

    public ScheduleInstance scheduleState(ScheduleState scheduleState) {
        this.scheduleState = scheduleState;
        return this;
    }

    public void setScheduleState(ScheduleState scheduleState) {
        this.scheduleState = scheduleState;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ScheduleInstance vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ScheduleTemplate getScheduleTemplate() {
        return scheduleTemplate;
    }

    public ScheduleInstance scheduleTemplate(ScheduleTemplate scheduleTemplate) {
        this.scheduleTemplate = scheduleTemplate;
        return this;
    }

    public void setScheduleTemplate(ScheduleTemplate scheduleTemplate) {
        this.scheduleTemplate = scheduleTemplate;
    }

    public Driver getDriver() {
        return driver;
    }

    public ScheduleInstance driver(Driver driver) {
        this.driver = driver;
        return this;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Route getRoute() {
        return route;
    }

    public ScheduleInstance route(Route route) {
        this.route = route;
        return this;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Bay getBay() {
        return bay;
    }

    public ScheduleInstance bay(Bay bay) {
        this.bay = bay;
        return this;
    }

    public void setBay(Bay bay) {
        this.bay = bay;
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
        ScheduleInstance scheduleInstance = (ScheduleInstance) o;
        if (scheduleInstance.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), scheduleInstance.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ScheduleInstance{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", scheduledTime='" + getScheduledTime() + "'" +
            ", actualScheduledTime='" + getActualScheduledTime() + "'" +
            ", actualDepartureTime='" + getActualDepartureTime() + "'" +
            ", specialNotes='" + getSpecialNotes() + "'" +
            ", scheduleState='" + getScheduleState() + "'" +
            "}";
    }
}
