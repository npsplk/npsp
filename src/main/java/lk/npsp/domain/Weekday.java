package lk.npsp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Weekday.
 */
@Entity
@Table(name = "weekday")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "weekday")
public class Weekday implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "weekday_name", nullable = false)
    private String weekdayName;

    @NotNull
    @Column(name = "weekday_meta", nullable = false)
    private String weekdayMeta;

    @ManyToMany(mappedBy = "weekdays")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Schedule> schedules = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeekdayName() {
        return weekdayName;
    }

    public Weekday weekdayName(String weekdayName) {
        this.weekdayName = weekdayName;
        return this;
    }

    public void setWeekdayName(String weekdayName) {
        this.weekdayName = weekdayName;
    }

    public String getWeekdayMeta() {
        return weekdayMeta;
    }

    public Weekday weekdayMeta(String weekdayMeta) {
        this.weekdayMeta = weekdayMeta;
        return this;
    }

    public void setWeekdayMeta(String weekdayMeta) {
        this.weekdayMeta = weekdayMeta;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public Weekday schedules(Set<Schedule> schedules) {
        this.schedules = schedules;
        return this;
    }

    public Weekday addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.getWeekdays().add(this);
        return this;
    }

    public Weekday removeSchedule(Schedule schedule) {
        this.schedules.remove(schedule);
        schedule.getWeekdays().remove(this);
        return this;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
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
        Weekday weekday = (Weekday) o;
        if (weekday.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), weekday.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Weekday{" +
            "id=" + getId() +
            ", weekdayName='" + getWeekdayName() + "'" +
            ", weekdayMeta='" + getWeekdayMeta() + "'" +
            "}";
    }
}
