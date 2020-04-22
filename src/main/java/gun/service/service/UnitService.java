package gun.service.service;

import gun.service.entity.Unit;
import gun.service.entity.UnitState;
import gun.service.exceptions.NotFoundException;
import gun.service.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static gun.service.entity.UnitState.NO_ENEMIES;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    public Unit saveUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    public List<Unit> saveAllUnit(List<Unit> units) {
        return unitRepository.saveAll(units);
    }

    public List<Unit> getUnits() {
        return unitRepository.findAll();
    }

    public List<Unit> getUnitsBySubdivisionId(Integer id) {
        return  unitRepository.findUnitsBySubdivisionId(id);
    }

    public void setGunsStatusNoEnemies(Integer subdivisionId) {
        List<Unit> units = getUnitsBySubdivisionId(subdivisionId);
        units.forEach(unit -> unit.setUnitState(NO_ENEMIES));
        saveAllUnit(units);
    }

    public void removeUnitById(Integer id) {
        unitRepository.findById(id).orElseThrow(NotFoundException::new);
        unitRepository.deleteById(id);
    }

    public Optional<Unit> findById(Integer id) {
        return unitRepository.findById(id);
    }
}
