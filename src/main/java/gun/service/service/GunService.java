package gun.service.service;

import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.repository.UnitDataRepository;
import gun.service.service.ammunition.Ammunition;
import gun.service.service.systems.Radar;
import gun.service.service.systems.aim.AimingSystem;
import gun.service.service.systems.fire.FireSystem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GunService implements Runnable {

    Logger log = LoggerFactory.getLogger(GunService.class);

    private final AimingSystem aimingSystem;
    private final FireSystem fireSystem;
    private final Radar radar;
    private final UnitDataRepository unitDataRepository;

    public void patrol() {
        while (true) {

            List<UnitDto> lastPosition = radar.checkField();

            if (lastPosition.size() == 0) {
                if(radar.getSizeIgnoreList() > 0) {
                    log.debug("No shells of the required type to destroy remaining targets. Stopping fire...");
                    break;
                }
                log.debug("There is no enemies to destroy. Stopping fire...");
                break;
            }

            UnitDto target = aimingSystem.catchTarget(lastPosition);

            if(!fireSystem.makeShot(target)) {
                radar.addTypeToIgnore(target.getUnitType());
                log.info("No shells for {}", target.getUnitType().name());
                continue;
            }

            log.debug("AFC '{}' shot to target '{}'", this, target);

            UnitDamageDto damageDto = new UnitDamageDto();
            damageDto.setPosX(target.getPosX());
            damageDto.setPosY(target.getPosY());
            damageDto.setDamage(3.0); // TODO: replace to accuracy factor

            unitDataRepository.damage(damageDto);
        }
    }

    @Override
    public void run() {
        patrol();
    }
}
