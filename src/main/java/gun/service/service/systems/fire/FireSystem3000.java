package gun.service.service.systems.fire;

import gun.service.dto.UnitDto;
import gun.service.service.systems.loading.AutomationLoadingSystem;
import gun.service.exceptions.ShellJammedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FireSystem3000 extends FireSystem {

    Logger log = LoggerFactory.getLogger(FireSystem3000.class);

    private AutomationLoadingSystem loadingSystem;

    public FireSystem3000(AutomationLoadingSystem loadingSystem) {
        shotPeriod = 1;
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
        //unitDto.setDamage(currentShell.getDamageEnergy() * unitDto.getAccuracyFactor());

        try {
            TimeUnit.SECONDS.sleep(shotPeriod);
        } catch (InterruptedException ignored) {
        }
        return true;
    }

    @Override
    public void noMoreEnemies(){
        loadingSystem.disconnectCassette();
    }
}
