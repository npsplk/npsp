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

    @OneToMany(mappedBy = "route")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Location> locations = new HashSet<>();
    @ManyToOne
    @JsonIgnoreProperties("routes")
    private Location startLocation;

    @ManyToOne
    @JsonIgnoreProperties("routes")
    private Location endLocation;

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

    public Set<Location> getLocations() {
        return locations;
    }

    public Route locations(Set<Location> locations) {
        this.locations = locations;
        return this;
    }

    public Route addLocations(Location location) {
        this.locations.add(location);
        location.setRoute(this);
        return this;
    }

    public Route removeLocations(Location location) {
        this.locations.remove(location);
        location.setRoute(null);
        return this;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
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
