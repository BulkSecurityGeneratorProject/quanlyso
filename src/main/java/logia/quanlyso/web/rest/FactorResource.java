package logia.quanlyso.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;
import logia.quanlyso.service.FactorService;
import logia.quanlyso.service.dto.FactorDTO;
import logia.quanlyso.web.rest.util.HeaderUtil;

/**
 * REST controller for managing Factor.
 *
 * @author Dai Mai
 */
@RestController
@RequestMapping("/api")
public class FactorResource {

	/** The log. */
	private final Logger		log			= LoggerFactory.getLogger(FactorResource.class);

	/** The Constant ENTITY_NAME. */
	private static final String	ENTITY_NAME	= "factor";

	/** The factor service. */
	private final FactorService	factorService;

	/**
	 * Instantiates a new factor resource.
	 *
	 * @param factorService the factor service
	 */
	public FactorResource(FactorService factorService) {
		this.factorService = factorService;
	}

	/**
	 * POST /factors : Create a new factor.
	 *
	 * @param factorDTO the factorDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new factorDTO, or with
	 *         status 400 (Bad Request) if the factor has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping("/factors")
	@Timed
	public ResponseEntity<FactorDTO> createFactor(@RequestBody FactorDTO factorDTO)
			throws URISyntaxException {
		this.log.debug("REST request to save Factor : {}", factorDTO);
		if (factorDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(FactorResource.ENTITY_NAME,
					"idexists", "A new factor cannot already have an ID")).body(null);
		}
		FactorDTO result = this.factorService.save(factorDTO);
		return ResponseEntity
				.created(new URI("/api/factors/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(FactorResource.ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * PUT /factors : Updates an existing factor.
	 *
	 * @param factorDTO the factorDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated factorDTO,
	 *         or with status 400 (Bad Request) if the factorDTO is not valid,
	 *         or with status 500 (Internal Server Error) if the factorDTO couldnt be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PutMapping("/factors")
	@Timed
	public ResponseEntity<FactorDTO> updateFactor(@RequestBody FactorDTO factorDTO)
			throws URISyntaxException {
		this.log.debug("REST request to update Factor : {}", factorDTO);
		if (factorDTO.getId() == null) {
			return this.createFactor(factorDTO);
		}
		FactorDTO result = this.factorService.save(factorDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(FactorResource.ENTITY_NAME, factorDTO.getId().toString()))
				.body(result);
	}

	/**
	 * GET /factors : get all the factors.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of factors in body
	 */
	@GetMapping("/factors")
	@Timed
	public List<FactorDTO> getAllFactors() {
		this.log.debug("REST request to get all Factors");
		return this.factorService.findAll();
	}

	/**
	 * GET /factors/:id : get the "id" factor.
	 *
	 * @param id the id of the factorDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the factorDTO, or with status
	 *         404 (Not Found)
	 */
	@GetMapping("/factors/{id}")
	@Timed
	public ResponseEntity<FactorDTO> getFactor(@PathVariable Long id) {
		this.log.debug("REST request to get Factor : {}", id);
		FactorDTO factorDTO = this.factorService.findOne(id);
		return ResponseUtil.wrapOrNotFound(Optional.ofNullable(factorDTO));
	}

	/**
	 * DELETE /factors/:id : delete the "id" factor.
	 *
	 * @param id the id of the factorDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/factors/{id}")
	@Timed
	public ResponseEntity<Void> deleteFactor(@PathVariable Long id) {
		this.log.debug("REST request to delete Factor : {}", id);
		this.factorService.delete(id);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert(FactorResource.ENTITY_NAME, id.toString())).build();
	}

}
