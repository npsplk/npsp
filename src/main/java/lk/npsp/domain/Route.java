package lk.npsp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;

/**
 * A Route.
 */
@Entity
@Table(name = "route")
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "route_number")
    private String routeNumber;

    @OneToMany(mappedBy = "route", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<RouteLocation> routeLocations = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public Route routeName(String routeName) {
        this.routeName = routeName;
        return this;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public Route routeNumber(String routeNumber) {
        this.routeNumber = routeNumber;
        return this;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public SortedSet<RouteLocation> getRouteLocations() {
        SortedSet<RouteLocation> sortedRouteLocations= new TreeSet<>(
            Comparator.comparing(RouteLocation::getSequenceNumber));
        sortedRouteLocations.addAll(this.routeLocations);
        return sortedRouteLocations;
    }

    public Route routeLocations(Set<RouteLocation> routeLocations) {
        this.routeLocations = routeLocations;
        return this;
    }

    public Route addRouteLocation(RouteLocation routeLocation) {
        this.routeLocations.add(routeLocation);
        routeLocation.setRoute(this);
        return this;
    }

    public Route removeRouteLocation(RouteLocation routeLocation) {
        this.routeLocations.remove(routeLocation);
        routeLocation.setRoute(null);
        return this;
    }

    public void setRouteLocations(Set<RouteLocation> routeLocations) {
        this.routeLocations = routeLocations;
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
        Route route = (Route) o;
        if (route.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), route.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Route{" +
            "id=" + getId() +
            ", routeName='" + getRouteName() + "'" +
            ", routeNumber='" + getRouteNumber() + "'" +
            "}";
    }
}
