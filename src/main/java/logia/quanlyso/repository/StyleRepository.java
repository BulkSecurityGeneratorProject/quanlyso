package logia.quanlyso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import logia.quanlyso.domain.Style;

/**
 * Spring Data JPA repository for the Style entity.
 *
 * @author Dai Mai
 */
@SuppressWarnings("unused")
public interface StyleRepository extends JpaRepository<Style, Long> {

}
