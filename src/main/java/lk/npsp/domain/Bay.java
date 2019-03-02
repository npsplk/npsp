package lk.npsp.domain;



import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Bay.
 */
@Entity
@Table(name = "bay")
public class Bay implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bay_name")
    private String bayName;

    @Column(name = "binding_address")
    private String bindingAddress;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBayName() {
        return bayName;
    }

    public Bay bayName(String bayName) {
        this.bayName = bayName;
        return this;
    }

    public void setBayName(String bayName) {
        this.bayName = bayName;
    }

    public String getBindingAddress() {
        return bindingAddress;
    }

    public Bay bindingAddress(String bindingAddress) {
        this.bindingAddress = bindingAddress;
        return this;
    }

    public void setBindingAddress(String bindingAddress) {
        this.bindingAddress = bindingAddress;
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
        Bay bay = (Bay) o;
        if (bay.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bay.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Bay{" +
            "id=" + getId() +
            ", bayName='" + getBayName() + "'" +
            ", bindingAddress='" + getBindingAddress() + "'" +
            "}";
    }
}
