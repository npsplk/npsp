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
    @Column(name = "number_plate", nullable = false)
    private String numberPlate;

    @NotNull
    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @ManyToOne
    @JsonIgnoreProperties("vehicles")
    private VehicleOwner owner;

    @ManyToOne
    @JsonIgnoreProperties("vehicles")
    private TransportType transportType;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "vehicle_vehicle_facility",
               joinColumns = @JoinColumn(name = "vehicle_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "vehicle_facility_id", referencedColumnName = "id"))
    private Set<VehicleFacility> vehicleFacilities = new HashSet<>();

    @OneToMany(mappedBy = "vehicle")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Trip> trips = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public Vehicle numberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
        return this;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
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

    public VehicleOwner getOwner() {
        return owner;
    }

    public Vehicle owner(VehicleOwner vehicleOwner) {
        this.owner = vehicleOwner;
        return this;
    }

    public void setOwner(VehicleOwner vehicleOwner) {
        this.owner = vehicleOwner;
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

    public Set<VehicleFacility> getVehicleFacilities() {
        return vehicleFacilities;
    }

    public Vehicle vehicleFacilities(Set<VehicleFacility> vehicleFacilities) {
        this.vehicleFacilities = vehicleFacilities;
        return this;
    }

    public Vehicle addVehicleFacility(VehicleFacility vehicleFacility) {
        this.vehicleFacilities.add(vehicleFacility);
        vehicleFacility.getVehicles().add(this);
        return this;
    }

    public Vehicle removeVehicleFacility(VehicleFacility vehicleFacility) {
        this.vehicleFacilities.remove(vehicleFacility);
        vehicleFacility.getVehicles().remove(this);
        return this;
    }

    public void setVehicleFacilities(Set<VehicleFacility> vehicleFacilities) {
        this.vehicleFacilities = vehicleFacilities;
    }

    public Set<Trip> getTrips() {
        return trips;
    }

    public Vehicle trips(Set<Trip> trips) {
        this.trips = trips;
        return this;
    }

    public Vehicle addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setVehicle(this);
        return this;
    }

    public Vehicle removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setVehicle(null);
        return this;
    }

    public void setTrips(Set<Trip> trips) {
        this.trips = trips;
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
            ", numberPlate='" + getNumberPlate() + "'" +
            ", numberOfSeats=" + getNumberOfSeats() +
            "}";
    }
}
