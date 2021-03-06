package logia.quanlyso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import logia.quanlyso.domain.Transactions;

/**
 * Spring Data JPA repository for the Transactions entity.
 *
 * @author Dai Mai
 */
@SuppressWarnings("unused")
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

}
