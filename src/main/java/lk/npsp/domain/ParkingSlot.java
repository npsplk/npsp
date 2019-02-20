package lk.npsp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ParkingSlot.
 */
@Entity
@Table(name = "parking_slot")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "parkingslot")
public class ParkingSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number")
    private String slotNumber;

    @ManyToOne
    @JsonIgnoreProperties("parkingSlots")
    private ParkingArea parkingArea;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public ParkingSlot slotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
        return this;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public ParkingArea getParkingArea() {
        return parkingArea;
    }

    public ParkingSlot parkingArea(ParkingArea parkingArea) {
        this.parkingArea = parkingArea;
        return this;
    }

    public void setParkingArea(ParkingArea parkingArea) {
        this.parkingArea = parkingArea;
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
        ParkingSlot parkingSlot = (ParkingSlot) o;
        if (parkingSlot.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), parkingSlot.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
            "id=" + getId() +
            ", slotNumber='" + getSlotNumber() + "'" +
            "}";
    }
}
