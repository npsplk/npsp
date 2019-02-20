package lk.npsp.domain;

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
 * A Route.
 */
@Entity
@Table(name = "route")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "route")
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_name")
    private String routeName;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Location startLocation;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Location endLocation;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "route_location",
               joinColumns = @JoinColumn(name = "routes_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "locations_id", referencedColumnName = "id"))
    private Set<Location> locations = new HashSet<>();

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

    public Location getStartLocation() {
        return startLocation;
    }

    public Route startLocation(Location location) {
        this.startLocation = location;
        return this;
    }

    public void setStartLocation(Location location) {
        this.startLocation = location;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public Route endLocation(Location location) {
        this.endLocation = location;
        return this;
    }

    public void setEndLocation(Location location) {
        this.endLocation = location;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public Route locations(Set<Location> locations) {
        this.locations = locations;
        return this;
    }

    public Route addLocation(Location location) {
        this.locations.add(location);
        location.getRoutes().add(this);
        return this;
    }

    public Route removeLocation(Location location) {
        this.locations.remove(location);
        location.getRoutes().remove(this);
        return this;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
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
            "}";
    }
}
