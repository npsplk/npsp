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
 * A VehicleFacility.
 */
@Entity
@Table(name = "vehicle_facility")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "vehiclefacility")
public class VehicleFacility implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "facility_name", nullable = false)
    private String facilityName;

    @NotNull
    @Column(name = "facilitymeta", nullable = false)
    private String facilitymeta;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "facilities")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Vehicle> vehicles = new HashSet<>();

    @ManyToMany(mappedBy = "vehicleFacilities")
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

    public String getFacilityName() {
        return facilityName;
    }

    public VehicleFacility facilityName(String facilityName) {
        this.facilityName = facilityName;
        return this;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilitymeta() {
        return facilitymeta;
    }

    public VehicleFacility facilitymeta(String facilitymeta) {
        this.facilitymeta = facilitymeta;
        return this;
    }

    public void setFacilitymeta(String facilitymeta) {
        this.facilitymeta = facilitymeta;
    }

    public String getDescription() {
        return description;
    }

    public VehicleFacility description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public VehicleFacility vehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
        return this;
    }

    public VehicleFacility addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
        vehicle.getFacilities().add(this);
        return this;
    }

    public VehicleFacility removeVehicle(Vehicle vehicle) {
        this.vehicles.remove(vehicle);
        vehicle.getFacilities().remove(this);
        return this;
    }

    public void setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public VehicleFacility schedules(Set<Schedule> schedules) {
        this.schedules = schedules;
        return this;
    }

    public VehicleFacility addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.getVehicleFacilities().add(this);
        return this;
    }

    public VehicleFacility removeSchedule(Schedule schedule) {
        this.schedules.remove(schedule);
        schedule.getVehicleFacilities().remove(this);
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
        VehicleFacility vehicleFacility = (VehicleFacility) o;
        if (vehicleFacility.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vehicleFacility.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "VehicleFacility{" +
            "id=" + getId() +
            ", facilityName='" + getFacilityName() + "'" +
            ", facilitymeta='" + getFacilitymeta() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
