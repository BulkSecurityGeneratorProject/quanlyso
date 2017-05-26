package logia.quanlyso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import logia.quanlyso.domain.CostFactor;

/**
 * Spring Data JPA repository for the CostFactor entity.
 *
 * @author Dai Mai
 */
@SuppressWarnings("unused")
public interface CostFactorRepository extends JpaRepository<CostFactor,Long> {

}
