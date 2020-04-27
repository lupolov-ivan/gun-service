package gun.service.service.gun.systems.loading;

import gun.service.entity.UnitType;
import gun.service.service.gun.ammunition.Ammunition;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class AutomationLoadingSystem3000 extends AutomationLoadingSystem {

    public AutomationLoadingSystem3000(Integer loadTime, Integer disconnectTime, Ammunition ammunition) {
        super(loadTime, disconnectTime, ammunition);
    }

    @Override
    public boolean loadCassette(UnitType unitType) {

        if (currentCassette != null) {
            disconnectCassette();
        }

        if (ammunition.hasNext(unitType)) {
            try {
                TimeUnit.SECONDS.sleep(loadTime);
            } catch (InterruptedException ignored) {
            }

            currentCassette = ammunition.getCassette(unitType);
            currentEnemyTypeCassette = unitType;
            log.debug("Next cassette received");
            return true;
        } else {
            log.debug("Shells for target with type '{}' is over.", unitType);
            return false;
        }
    }

    @Override
    public void disconnectCassette() {
        if (currentCassette == null) {
            return;
        }

        if (currentCassette.getBalance() != 0) {
            ammunition.addCassette(currentCassette);
        }
        try {
            TimeUnit.SECONDS.sleep(disconnectTime);
        } catch (InterruptedException ignored) {
        }

        currentEnemyTypeCassette = null;
        currentCassette = null;
    }

    @Override
    public void extractShell() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ignored) { }
    }
}

