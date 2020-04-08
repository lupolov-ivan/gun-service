package gun.service.repository;

import gun.service.entity.Subdivision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubdivisionRepository extends JpaRepository<Subdivision, Integer> {

}
