package lk.npsp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
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
    @Column(name = "facility_meta", nullable = false)
    private String facilityMeta;

    @Column(name = "description")
    private String description;

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

    public String getFacilityMeta() {
        return facilityMeta;
    }

    public VehicleFacility facilityMeta(String facilityMeta) {
        this.facilityMeta = facilityMeta;
        return this;
    }

    public void setFacilityMeta(String facilityMeta) {
        this.facilityMeta = facilityMeta;
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
            ", facilityMeta='" + getFacilityMeta() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
