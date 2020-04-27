package gun.service.service.gun.systems.fire;

import gun.service.dto.UnitDto;
import gun.service.exceptions.ShellJammedException;
import gun.service.service.gun.systems.loading.AutomationLoadingSystem;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class FireSystem3000 extends FireSystem {

    private AutomationLoadingSystem loadingSystem;

    public FireSystem3000(AutomationLoadingSystem loadingSystem, Integer shotPeriod) {
        this.shotPeriod = shotPeriod;
        this.loadingSystem = loadingSystem;
    }

    @Override
    public boolean makeShot(UnitDto unitDto) {
        if (unitDto.getUnitType() != loadingSystem.getCurrentEnemyTypeCassette()
                || !loadingSystem.getCurrentCassette().hasNext()) {
            if (!loadingSystem.loadCassette(unitDto.getUnitType())) {
                return false;
            }
        }

        try {
            isJammed();
        } catch (ShellJammedException e) {
            log.debug("Shell is Jammed. Extracting shell...");
            loadingSystem.extractShell();
        }

        currentShell = loadingSystem.getCurrentCassette().getShell();

        try {
            TimeUnit.SECONDS.sleep(shotPeriod);
        } catch (InterruptedException ignored) {
        }
        return true;
    }
}
