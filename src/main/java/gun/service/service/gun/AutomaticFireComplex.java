package gun.service.service.gun;

import gun.service.dto.UnitStateDto;
import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.dto.WinnerDto;
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

import java.util.List;

import static gun.service.entity.UnitState.*;

@Slf4j
public class AutomaticFireComplex extends Unit implements Runnable {

    private final AimingSystem aimingSystem;
    private final FireSystem fireSystem;
    private final Radar radar;
    private final BattleManagerService battleManagerService;
    private final UnitService unitService;

    public AutomaticFireComplex(Integer id, Integer posX, Integer posY, Integer protectionLevel, UnitType unitType, UnitState unitState, Integer shotPeriod, Integer loadCassetteTime, Integer disconnectCassetteTime, Ammunition ammunition, BattleManagerService battleManagerService, UnitService unitService) {
        super(id, posX, posY, protectionLevel, unitType, unitState);
        this.battleManagerService = battleManagerService;
        this.aimingSystem = new MechanicalInertialAimSystem(posX, posY);
        this.fireSystem = new FireSystem3000(new AutomationLoadingSystem3000(loadCassetteTime, disconnectCassetteTime, ammunition), shotPeriod);
        this.radar = new Radar(battleManagerService);
        this.unitService = unitService;
    }

    public void patrol() {
        while (true) {

            Unit guns = unitService.findById(this.getId()).orElseThrow(NotFoundException::new);

            if (guns.getUnitState().equals(DEAD)) {
                log.debug("Stop fire, unit is destroyed");
                break;
            }

            if(guns.getUnitState().equals(NO_ENEMIES)) {
                log.debug("There is no enemies to destroy. Stopping fire...");
                break;
            }

            List<UnitDto> lastPosition = radar.checkField();

            if (lastPosition.size() == 0) {
                if(radar.getSizeIgnoreList() > 0) {
                    log.debug("No shells of the required type to destroy remaining targets. Stopping fire...");
                    saveAndUpdateState(guns, NO_SHELLS);
                    break;
                }
                log.debug("There is no enemies to destroy. Stopping fire...");
                saveAndUpdateState(guns, NO_ENEMIES);
                unitService.setGunsStatusNoEnemies(guns.getSubdivisionId());
                battleManagerService.stopBattle(new WinnerDto(guns.getUnitType()));
                log.debug("GUN SERVICE CALL STOP BATTLE");
                break;
            }

            UnitDto target = aimingSystem.catchTarget(lastPosition);

            if(!fireSystem.makeShot(target)) {
                radar.addTypeToIgnore(target.getUnitType());
                log.debug("No shells for {}", target.getUnitType().name());
                continue;
            }

            log.debug("AFC '{}' shot to target '{}'", this, target);

            setDamage(target);
        }
    }

    private void saveAndUpdateState(Unit unit, UnitState state) {

        this.setUnitState(state);
        unit.setUnitState(state);
        unitService.saveUnit(unit);

        UnitStateDto dto = new UnitStateDto();
        dto.setUnitId(unit.getId());
        dto.setUnitType(unit.getUnitType());
        dto.setUnitState(state);
        battleManagerService.updateUnitState(dto);
    }

    private void setDamage(UnitDto target) {
        UnitDamageDto damageDto = new UnitDamageDto();

        damageDto.setPosX(target.getPosX());
        damageDto.setPosY(target.getPosY());
        damageDto.setDamage(aimingSystem.computeAccuracyFactor(target.getUnitType()));

        battleManagerService.setUnitDamage(damageDto);
    }

    @Override
    public void run() {
        patrol();
    }
}
