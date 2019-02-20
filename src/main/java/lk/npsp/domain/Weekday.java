package lk.npsp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

import lk.npsp.domain.enumeration.Weekdays;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false)
    private Weekdays weekday;

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
