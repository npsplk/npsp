package lk.npsp.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * A RouteLocation.
 */
@Entity
@Table(name = "route_location")
public class RouteLocation implements Serializable, Comparable<RouteLocation> {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sequence_number")
    private Long sequenceNumber;

    @ManyToOne
    @JsonIgnoreProperties("routeLocations")
    private Location location;

    @ManyToOne
    @JsonIgnoreProperties("routeLocations")
    private Route route;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public RouteLocation sequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Location getLocation() {
        return location;
    }

    public RouteLocation location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Route getRoute() {
        return route;
    }

    public RouteLocation route(Route route) {
        this.route = route;
        return this;
    }

    public void setRoute(Route route) {
        this.route = route;
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
        RouteLocation routeLocation = (RouteLocation) o;
        if (routeLocation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), routeLocation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RouteLocation{" +
            "id=" + getId() +
            ", sequenceNumber=" + getSequenceNumber() +
            "}";
    }

    @Override
    @NotNull
    public int compareTo(RouteLocation routeLocation){
        return routeLocation.getSequenceNumber().intValue();
    }

}
