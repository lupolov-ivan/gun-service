package gun.service.service.gun;

import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.entity.Unit;
import gun.service.entity.UnitType;
import gun.service.repository.BattleManagerRepository;
import gun.service.service.systems.Radar;
import gun.service.service.systems.aim.AimingSystem;
import gun.service.service.systems.aim.MechanicalInertialAimSystem;
import gun.service.service.systems.fire.FireSystem;
import gun.service.service.systems.fire.FireSystem3000;
import gun.service.service.systems.loading.AutomationLoadingSystem3000;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AutomaticFireComplex extends Unit implements Runnable {

    private final AimingSystem aimingSystem;
    private final FireSystem fireSystem;
    private final Radar radar;
    private final BattleManagerRepository battleManagerRepository;

    public AutomaticFireComplex(int posX, int posY, int protectionLevel, UnitType unitType, Boolean isAlive, BattleManagerRepository battleManagerRepository) {
        super(posX, posY, protectionLevel, unitType, isAlive);
        this.battleManagerRepository = battleManagerRepository;
        this.aimingSystem = new MechanicalInertialAimSystem(posX, posY);
        this.fireSystem = new FireSystem3000(new AutomationLoadingSystem3000());
        this.radar = new Radar(battleManagerRepository);
    }

    public void patrol() {
        while (true) {

            List<UnitDto> lastPosition = radar.checkField();

            if (lastPosition.size() == 0) {
                if(radar.getSizeIgnoreList() > 0) {
                    log.info("No shells of the required type to destroy remaining targets. Stopping fire...");
                    break;
                }
                log.info("There is no enemies to destroy. Stopping fire...");
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

            battleManagerRepository.setUnitDamage(damageDto);
        }
    }

    @Override
    public void run() {
        patrol();
    }
}
