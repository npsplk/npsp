package lk.npsp.domain;



import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A TransportType.
 */
@Entity
@Table(name = "transport_type")
public class TransportType implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "type_name", nullable = false)
    private String typeName;

    @NotNull
    @Column(name = "meta_code", nullable = false)
    private String metaCode;

    @Column(name = "description")
    private String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public TransportType typeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMetaCode() {
        return metaCode;
    }

    public TransportType metaCode(String metaCode) {
        this.metaCode = metaCode;
        return this;
    }

    public void setMetaCode(String metaCode) {
        this.metaCode = metaCode;
    }

    public String getDescription() {
        return description;
    }

    public TransportType description(String description) {
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
        TransportType transportType = (TransportType) o;
        if (transportType.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), transportType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TransportType{" +
            "id=" + getId() +
            ", typeName='" + getTypeName() + "'" +
            ", metaCode='" + getMetaCode() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
