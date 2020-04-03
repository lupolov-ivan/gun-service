package gun.service.service;

import gun.service.dto.UnitDto;
import gun.service.entity.Unit;
import gun.service.repository.BattleManagerRepository;
import gun.service.repository.UnitRepository;
import gun.service.service.gun.AutomaticFireComplex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitService {

    private final UnitRepository unitRepository;
    private final BattleManagerRepository battleManagerRepository;

    public Unit createGun(Unit unit) {
        return unitRepository.save(unit);
    }

    public void registerUnitsOnBattlefield() {

        List<Unit> units = unitRepository.findAll();

        units.forEach(unit -> {
            UnitDto unitDto = new UnitDto();

            unitDto.setPosX(unit.getPosX());
            unitDto.setPosY(unit.getPosY());
            unitDto.setProtectionLevel(unit.getProtectionLevel());
            unitDto.setUnitType(unit.getUnitType());
            unitDto.setIsAlive(unit.getIsAlive());

            battleManagerRepository.registerUnitOnBattlefield(unitDto);
        });
    }

    public void startPatrolling() {

        List<Unit> units = unitRepository.findAll();

        units.forEach(unit -> {
            AutomaticFireComplex afc = new AutomaticFireComplex(
                    unit.getPosX(),
                    unit.getPosY(),
                    unit.getProtectionLevel(),
                    unit.getUnitType(),
                    unit.getIsAlive(),
                    battleManagerRepository
            );
            new Thread(afc).start();
            log.info("New AFC({}) start patrolling", afc);
        });
    }
}
