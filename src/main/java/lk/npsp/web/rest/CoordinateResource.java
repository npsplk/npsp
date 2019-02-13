package lk.npsp.web.rest;
import lk.npsp.domain.Coordinate;
import lk.npsp.repository.CoordinateRepository;
import lk.npsp.repository.search.CoordinateSearchRepository;
import lk.npsp.web.rest.errors.BadRequestAlertException;
import lk.npsp.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Coordinate.
 */
@RestController
@RequestMapping("/api")
public class CoordinateResource {

    private final Logger log = LoggerFactory.getLogger(CoordinateResource.class);

    private static final String ENTITY_NAME = "coordinate";

    private final CoordinateRepository coordinateRepository;

    private final CoordinateSearchRepository coordinateSearchRepository;

    public CoordinateResource(CoordinateRepository coordinateRepository, CoordinateSearchRepository coordinateSearchRepository) {
        this.coordinateRepository = coordinateRepository;
        this.coordinateSearchRepository = coordinateSearchRepository;
    }

    /**
     * POST  /coordinates : Create a new coordinate.
     *
     * @param coordinate the coordinate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new coordinate, or with status 400 (Bad Request) if the coordinate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/coordinates")
    public ResponseEntity<Coordinate> createCoordinate(@RequestBody Coordinate coordinate) throws URISyntaxException {
        log.debug("REST request to save Coordinate : {}", coordinate);
        if (coordinate.getId() != null) {
            throw new BadRequestAlertException("A new coordinate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Coordinate result = coordinateRepository.save(coordinate);
        coordinateSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/coordinates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /coordinates : Updates an existing coordinate.
     *
     * @param coordinate the coordinate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated coordinate,
     * or with status 400 (Bad Request) if the coordinate is not valid,
     * or with status 500 (Internal Server Error) if the coordinate couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/coordinates")
    public ResponseEntity<Coordinate> updateCoordinate(@RequestBody Coordinate coordinate) throws URISyntaxException {
        log.debug("REST request to update Coordinate : {}", coordinate);
        if (coordinate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Coordinate result = coordinateRepository.save(coordinate);
        coordinateSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, coordinate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /coordinates : get all the coordinates.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of coordinates in body
     */
    @GetMapping("/coordinates")
    public List<Coordinate> getAllCoordinates() {
        log.debug("REST request to get all Coordinates");
        return coordinateRepository.findAll();
    }

    /**
     * GET  /coordinates/:id : get the "id" coordinate.
     *
     * @param id the id of the coordinate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the coordinate, or with status 404 (Not Found)
     */
    @GetMapping("/coordinates/{id}")
    public ResponseEntity<Coordinate> getCoordinate(@PathVariable Long id) {
        log.debug("REST request to get Coordinate : {}", id);
        Optional<Coordinate> coordinate = coordinateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(coordinate);
    }

    /**
     * DELETE  /coordinates/:id : delete the "id" coordinate.
     *
     * @param id the id of the coordinate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/coordinates/{id}")
    public ResponseEntity<Void> deleteCoordinate(@PathVariable Long id) {
        log.debug("REST request to delete Coordinate : {}", id);
        coordinateRepository.deleteById(id);
        coordinateSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/coordinates?query=:query : search for the coordinate corresponding
     * to the query.
     *
     * @param query the query of the coordinate search
     * @return the result of the search
     */
    @GetMapping("/_search/coordinates")
    public List<Coordinate> searchCoordinates(@RequestParam String query) {
        log.debug("REST request to search Coordinates for query {}", query);
        return StreamSupport
            .stream(coordinateSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
