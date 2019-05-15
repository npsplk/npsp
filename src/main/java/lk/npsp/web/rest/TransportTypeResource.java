package lk.npsp.web.rest;
import lk.npsp.domain.TransportType;
import lk.npsp.repository.TransportTypeRepository;
import lk.npsp.web.rest.errors.BadRequestAlertException;
import lk.npsp.web.rest.util.HeaderUtil;
import lk.npsp.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing TransportType.
 */
@RestController
@RequestMapping("/api")
public class TransportTypeResource {

    private final Logger log = LoggerFactory.getLogger(TransportTypeResource.class);

    private static final String ENTITY_NAME = "transportType";

    private final TransportTypeRepository transportTypeRepository;

    public TransportTypeResource(TransportTypeRepository transportTypeRepository) {
        this.transportTypeRepository = transportTypeRepository;
    }

    /**
     * POST  /transport-types : Create a new transportType.
     *
     * @param transportType the transportType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new transportType, or with status 400 (Bad Request) if the transportType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/transport-types")
    public ResponseEntity<TransportType> createTransportType(@Valid @RequestBody TransportType transportType) throws URISyntaxException {
        log.debug("REST request to save TransportType : {}", transportType);
        if (transportType.getId() != null) {
            throw new BadRequestAlertException("A new transportType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransportType result = transportTypeRepository.save(transportType);
        return ResponseEntity.created(new URI("/api/transport-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /transport-types : Updates an existing transportType.
     *
     * @param transportType the transportType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated transportType,
     * or with status 400 (Bad Request) if the transportType is not valid,
     * or with status 500 (Internal Server Error) if the transportType couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/transport-types")
    public ResponseEntity<TransportType> updateTransportType(@Valid @RequestBody TransportType transportType) throws URISyntaxException {
        log.debug("REST request to update TransportType : {}", transportType);
        if (transportType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TransportType result = transportTypeRepository.save(transportType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, transportType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /transport-types : get all the transportTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of transportTypes in body
     */
    @GetMapping("/transport-types")
    public ResponseEntity<List<TransportType>> getAllTransportTypes(Pageable pageable) {
        log.debug("REST request to get a page of TransportTypes");
        Page<TransportType> page = transportTypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/transport-types");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /all-transport-types : get all the transport-types.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of transport-types in body
     */
    @GetMapping("/all-transport-types")
    public ResponseEntity<List<TransportType>> getAllTransportTypes() {
        log.debug("REST request to get a list of TransportTypes");
        List<TransportType> list = transportTypeRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    /**
     * GET  /transport-types/:id : get the "id" transportType.
     *
     * @param id the id of the transportType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the transportType, or with status 404 (Not Found)
     */
    @GetMapping("/transport-types/{id}")
    public ResponseEntity<TransportType> getTransportType(@PathVariable Long id) {
        log.debug("REST request to get TransportType : {}", id);
        Optional<TransportType> transportType = transportTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(transportType);
    }

    /**
     * DELETE  /transport-types/:id : delete the "id" transportType.
     *
     * @param id the id of the transportType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/transport-types/{id}")
    public ResponseEntity<Void> deleteTransportType(@PathVariable Long id) {
        log.debug("REST request to delete TransportType : {}", id);
        transportTypeRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
