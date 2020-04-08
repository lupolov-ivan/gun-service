package gun.service.repository;

import gun.service.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Integer> {

    List<Unit> findUnitsBySubdivisionId(Integer id);

}
