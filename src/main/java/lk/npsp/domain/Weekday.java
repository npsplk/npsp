package lk.npsp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import lk.npsp.domain.enumeration.Weekdays;

/**
 * A Weekday.
 */
@Entity
@Table(name = "weekday")
public class Weekday implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false)
    private Weekdays weekday;

    @ManyToMany(mappedBy = "weekdays")
    @JsonIgnore
    private Set<ScheduleTemplate> scheduleTemplates = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Weekdays getWeekday() {
        return weekday;
    }

    public Weekday weekday(Weekdays weekday) {
        this.weekday = weekday;
        return this;
    }

    public void setWeekday(Weekdays weekday) {
        this.weekday = weekday;
    }

    public Set<ScheduleTemplate> getScheduleTemplates() {
        return scheduleTemplates;
    }

    public Weekday scheduleTemplates(Set<ScheduleTemplate> scheduleTemplates) {
        this.scheduleTemplates = scheduleTemplates;
        return this;
    }

    public Weekday addScheduleTemplate(ScheduleTemplate scheduleTemplate) {
        this.scheduleTemplates.add(scheduleTemplate);
        scheduleTemplate.getWeekdays().add(this);
        return this;
    }

    public Weekday removeScheduleTemplate(ScheduleTemplate scheduleTemplate) {
        this.scheduleTemplates.remove(scheduleTemplate);
        scheduleTemplate.getWeekdays().remove(this);
        return this;
    }

    public void setScheduleTemplates(Set<ScheduleTemplate> scheduleTemplates) {
        this.scheduleTemplates = scheduleTemplates;
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
            ", weekday='" + getWeekday() + "'" +
            "}";
    }
}
