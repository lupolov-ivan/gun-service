package gun.service.service.gun;

import gun.service.dto.SetUnitStateDto;
import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.entity.Unit;
import gun.service.entity.UnitState;
import gun.service.entity.UnitType;
import gun.service.exceptions.NotFoundException;
import gun.service.service.BattleManagerService;
import gun.service.service.UnitService;
import gun.service.service.gun.ammunition.Ammunition;
import gun.service.service.gun.systems.Radar;
import gun.service.service.gun.systems.aim.AimingSystem;
import gun.service.service.gun.systems.aim.MechanicalInertialAimSystem;
import gun.service.service.gun.systems.fire.FireSystem;
import gun.service.service.gun.systems.fire.FireSystem3000;
import gun.service.service.gun.systems.loading.AutomationLoadingSystem3000;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static gun.service.entity.UnitState.*;

@Slf4j
public class AutomaticFireComplex extends Unit implements Runnable {

    private final AimingSystem aimingSystem;
    private final FireSystem fireSystem;
    private final Radar radar;
    private final BattleManagerService battleManagerService;

    @Autowired
    private UnitService unitService;

    public AutomaticFireComplex(Integer id, Integer posX, Integer posY, Integer protectionLevel, UnitType unitType, UnitState unitState, Ammunition ammunition, BattleManagerService battleManagerService) {
        super(id, posX, posY, protectionLevel, unitType, unitState);
        this.battleManagerService = battleManagerService;
        this.aimingSystem = new MechanicalInertialAimSystem(posX, posY);
        this.fireSystem = new FireSystem3000(new AutomationLoadingSystem3000(ammunition));
        this.radar = new Radar(battleManagerService);
    }

    public void patrol() {
        while (true) {

            List<UnitDto> lastPosition = radar.checkField();

            if (lastPosition.size() == 0) {
                if(radar.getSizeIgnoreList() > 0) {
                    log.info("No shells of the required type to destroy remaining targets. Stopping fire...");
                    setAndUpdateState(NO_SHELLS);
                    break;
                }
                log.info("There is no enemies to destroy. Stopping fire...");
                setAndUpdateState(NO_ENEMIES);
                break;
            }

            UnitDto target = aimingSystem.catchTarget(lastPosition);

            if(!fireSystem.makeShot(target)) {
                radar.addTypeToIgnore(target.getUnitType());
                log.info("No shells for {}", target.getUnitType().name());
                continue;
            }

            log.info("AFC '{}' shot to target '{}'", this, target);

            UnitDamageDto damageDto = new UnitDamageDto();

            damageDto.setPosX(target.getPosX());
            damageDto.setPosY(target.getPosY());
            damageDto.setDamage(aimingSystem.computeAccuracyFactor(target.getUnitType()));

            battleManagerService.setUnitDamage(damageDto);
        }
    }

    private void setAndUpdateState(UnitState state) {

        Unit maybeUnit = unitService.findById(this.getId()).orElseThrow(NotFoundException::new);

        maybeUnit.setUnitState(state);
        unitService.saveUnit(maybeUnit);

        SetUnitStateDto dto = new SetUnitStateDto();
        dto.setUnitId(maybeUnit.getId());
        dto.setUnitType(maybeUnit.getUnitType());
        dto.setUnitState(state);
        battleManagerService.updateUnitState(dto);
    }

    @Override
    public void run() {
        patrol();
    }
}
