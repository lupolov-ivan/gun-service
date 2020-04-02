package gun.service.service.systems.loading;

import gun.service.entity.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class AutomationLoadingSystem3000 extends AutomationLoadingSystem {

    Logger log = LoggerFactory.getLogger(AutomationLoadingSystem3000.class);

    @Override
    public boolean loadCassette(UnitType unitType) {

        if (currentCassette != null) {
            disconnectCassette();
        }

        if (ammunition.hasNext(unitType)) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ignored) {
            }

            currentCassette = ammunition.getCassette(unitType);
            currentEnemyTypeCassette = unitType;
            log.info("Next cassette received");
            return true;
        } else {
            log.info("Shells for target with type '{}' is over.", unitType);
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
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ignored) {
        }

        currentEnemyTypeCassette = null;
        currentCassette = null;
    }

    @Override
    public void extractShell() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ignored) {
        }
    }
}

