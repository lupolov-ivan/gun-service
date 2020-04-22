package gun.service.service.gun;

import gun.service.dto.SetUnitStateDto;
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

    public AutomaticFireComplex(Integer id, Integer posX, Integer posY, Integer protectionLevel, UnitType unitType, UnitState unitState, Ammunition ammunition, BattleManagerService battleManagerService, UnitService unitService) {
        super(id, posX, posY, protectionLevel, unitType, unitState);
        this.battleManagerService = battleManagerService;
        this.aimingSystem = new MechanicalInertialAimSystem(posX, posY);
        this.fireSystem = new FireSystem3000(new AutomationLoadingSystem3000(ammunition));
        this.radar = new Radar(battleManagerService);
        this.unitService = unitService;

    }

    public void patrol() {
        while (true) {

            Unit guns = unitService.findById(this.getId()).orElseThrow(NotFoundException::new);

            if (guns.getUnitState().equals(DEAD)) {
                log.info("Stop fire, unit is destroyed");
                break;
            }

            List<UnitDto> lastPosition = radar.checkField();

            if (lastPosition.size() == 0) {
                if(radar.getSizeIgnoreList() > 0) {
                    log.info("No shells of the required type to destroy remaining targets. Stopping fire...");
                    saveAndUpdateState(guns, NO_SHELLS);
                    break;
                }
                log.info("There is no enemies to destroy. Stopping fire...");
                saveAndUpdateState(guns, NO_ENEMIES);
                unitService.addUnitsGunsWithStateNoEnemies();
                if (canStopBattle()) {
                    battleManagerService.stopBattle(new WinnerDto(guns.getUnitType()));
                    log.info("GUN SERVICE CALL STOP BATTLE");
                }
                break;
            }

            UnitDto target = aimingSystem.catchTarget(lastPosition);

            if(!fireSystem.makeShot(target)) {
                radar.addTypeToIgnore(target.getUnitType());
                log.info("No shells for {}", target.getUnitType().name());
                continue;
            }

            log.info("AFC '{}' shot to target '{}'", this, target);

            setDamage(target);
        }
    }

    private void saveAndUpdateState(Unit unit, UnitState state) {

        unit.setUnitState(state);
        unitService.saveUnit(unit);

        SetUnitStateDto dto = new SetUnitStateDto();
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

    private boolean canStopBattle() {
        int size = unitService.getUnitsBySubdivisionId(this.getSubdivisionId()).size();
        int quantityGunsWithStateNoEnemies = unitService.getQuantityGunsWithStateNoEnemies();
        return size == quantityGunsWithStateNoEnemies;
    }

    @Override
    public void run() {
        patrol();
    }
}
