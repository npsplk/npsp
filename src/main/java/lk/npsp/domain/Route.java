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

    @NotNull
    @Column(name = "route_name", nullable = false)
    private String routeName;

    @OneToOne
    @JoinColumn(unique = true)
    private Location startLocation;

    @OneToOne
    @JoinColumn(unique = true)
    private Location endLocation;

    @OneToMany(mappedBy = "route")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Coordinate> coordinates = new HashSet<>();
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

    public Set<Coordinate> getCoordinates() {
        return coordinates;
    }

    public Route coordinates(Set<Coordinate> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public Route addCoordinates(Coordinate coordinate) {
        this.coordinates.add(coordinate);
        coordinate.setRoute(this);
        return this;
    }

    public Route removeCoordinates(Coordinate coordinate) {
        this.coordinates.remove(coordinate);
        coordinate.setRoute(null);
        return this;
    }

    public void setCoordinates(Set<Coordinate> coordinates) {
        this.coordinates = coordinates;
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
