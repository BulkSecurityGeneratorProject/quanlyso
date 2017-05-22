package logia.quanlyso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import logia.quanlyso.domain.ProfitFactor;

/**
 * Spring Data JPA repository for the ProfitFactor entity.
 */
@SuppressWarnings("unused")
public interface ProfitFactorRepository extends JpaRepository<ProfitFactor,Long> {

}