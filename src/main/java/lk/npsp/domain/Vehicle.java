package lk.npsp.domain;

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
 * A Vehicle.
 */
@Entity
@Table(name = "vehicle")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "vehicle")
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;

    @NotNull
    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @ManyToOne
    @JsonIgnoreProperties("")
    private TransportType transportType;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Driver driver;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "vehicle_vehicle_facility",
               joinColumns = @JoinColumn(name = "vehicles_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "vehicle_facilities_id", referencedColumnName = "id"))
    private Set<VehicleFacility> vehicleFacilities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public Vehicle registrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
        return this;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public Vehicle numberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
        return this;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public Vehicle transportType(TransportType transportType) {
        this.transportType = transportType;
        return this;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public Driver getDriver() {
        return driver;
    }

    public Vehicle driver(Driver driver) {
        this.driver = driver;
        return this;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Set<VehicleFacility> getVehicleFacilities() {
        return vehicleFacilities;
    }

    public Vehicle vehicleFacilities(Set<VehicleFacility> vehicleFacilities) {
        this.vehicleFacilities = vehicleFacilities;
        return this;
    }

    public Vehicle addVehicleFacility(VehicleFacility vehicleFacility) {
        this.vehicleFacilities.add(vehicleFacility);
        return this;
    }

    public Vehicle removeVehicleFacility(VehicleFacility vehicleFacility) {
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
        Vehicle vehicle = (Vehicle) o;
        if (vehicle.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vehicle.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Vehicle{" +
            "id=" + getId() +
            ", registrationNumber='" + getRegistrationNumber() + "'" +
            ", numberOfSeats=" + getNumberOfSeats() +
            "}";
    }
}
