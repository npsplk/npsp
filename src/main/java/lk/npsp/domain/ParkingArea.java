package lk.npsp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * A ParkingArea.
 */
@Entity
@Table(name = "parking_area")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "parkingarea")
public class ParkingArea implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "area_name", nullable = false)
    private String areaName;

    @ManyToOne
    @JsonIgnoreProperties("parkingAreas")
    private Location location;

    @OneToMany(mappedBy = "parkingArea")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ParkingSlot> parkingSlots = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAreaName() {
        return areaName;
    }

    public ParkingArea areaName(String areaName) {
        this.areaName = areaName;
        return this;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Location getLocation() {
        return location;
    }

    public ParkingArea location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<ParkingSlot> getParkingSlots() {
        return parkingSlots;
    }

    public ParkingArea parkingSlots(Set<ParkingSlot> parkingSlots) {
        this.parkingSlots = parkingSlots;
        return this;
    }

    public ParkingArea addParkingSlot(ParkingSlot parkingSlot) {
        this.parkingSlots.add(parkingSlot);
        parkingSlot.setParkingArea(this);
        return this;
    }

    public ParkingArea removeParkingSlot(ParkingSlot parkingSlot) {
        this.parkingSlots.remove(parkingSlot);
        parkingSlot.setParkingArea(null);
        return this;
    }

    public void setParkingSlots(Set<ParkingSlot> parkingSlots) {
        this.parkingSlots = parkingSlots;
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
        ParkingArea parkingArea = (ParkingArea) o;
        if (parkingArea.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), parkingArea.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ParkingArea{" +
            "id=" + getId() +
            ", areaName='" + getAreaName() + "'" +
            "}";
    }
}
