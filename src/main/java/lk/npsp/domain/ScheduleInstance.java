package lk.npsp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A ScheduleInstance.
 */
@Entity
@Table(name = "schedule_instance")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "scheduleinstance")
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

    @ManyToOne
    @JsonIgnoreProperties("")
    private Vehicle vehicle;

    @ManyToOne
    @JsonIgnoreProperties("")
    private ScheduleTemplate scheduleTemplate;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Driver driver;

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
            "}";
    }
}
