package lk.npsp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Location.
 */
@Entity
@Table(name = "location")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "location")
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @ManyToOne
    @JsonIgnoreProperties("")
    private LocationType locationType;

    @OneToMany(mappedBy = "location")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ParkingArea> parkingAreas = new HashSet<>();

    @ManyToMany(mappedBy = "locations")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Route> routes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public Location locationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Location longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Location latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public Location locationType(LocationType locationType) {
        this.locationType = locationType;
        return this;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Set<ParkingArea> getParkingAreas() {
        return parkingAreas;
    }

    public Location parkingAreas(Set<ParkingArea> parkingAreas) {
        this.parkingAreas = parkingAreas;
        return this;
    }

    public Location addParkingArea(ParkingArea parkingArea) {
        this.parkingAreas.add(parkingArea);
        parkingArea.setLocation(this);
        return this;
    }

    public Location removeParkingArea(ParkingArea parkingArea) {
        this.parkingAreas.remove(parkingArea);
        parkingArea.setLocation(null);
        return this;
    }

    public void setParkingAreas(Set<ParkingArea> parkingAreas) {
        this.parkingAreas = parkingAreas;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public Location routes(Set<Route> routes) {
        this.routes = routes;
        return this;
    }

    public Location addRoute(Route route) {
        this.routes.add(route);
        route.getLocations().add(this);
        return this;
    }

    public Location removeRoute(Route route) {
        this.routes.remove(route);
        route.getLocations().remove(this);
        return this;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
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
        Location location = (Location) o;
        if (location.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), location.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Location{" +
            "id=" + getId() +
            ", locationName='" + getLocationName() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            "}";
    }
}
