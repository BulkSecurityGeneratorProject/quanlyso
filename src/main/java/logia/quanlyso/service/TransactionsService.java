package logia.quanlyso.service;

import logia.quanlyso.service.dto.TransactionsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Transactions.
 */
public interface TransactionsService {

    /**
     * Save a transactions.
     *
     * @param transactionsDTO the entity to save
     * @return the persisted entity
     */
    TransactionsDTO save(TransactionsDTO transactionsDTO);

    /**
     *  Get all the transactions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<TransactionsDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" transactions.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    TransactionsDTO findOne(Long id);

    /**
     *  Delete the "id" transactions.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
}
