package gun.service.service.gun.systems.loading;

import gun.service.entity.UnitType;
import gun.service.service.gun.ammunition.Ammunition;
import gun.service.service.gun.ammunition.Cassette;

public abstract class AutomationLoadingSystem {

    protected Integer loadTime;
    protected Integer disconnectTime;
    protected Cassette currentCassette;
    protected UnitType currentEnemyTypeCassette;
    protected Ammunition ammunition;

    public AutomationLoadingSystem(Integer loadTime, Integer disconnectTime, Ammunition ammunition) {
        this.loadTime = loadTime;
        this.disconnectTime = disconnectTime;
        this.ammunition = ammunition;
    }

    abstract public boolean loadCassette(UnitType unitType);

    abstract public void disconnectCassette();

    abstract public void extractShell();

    public Cassette getCurrentCassette() {
        return currentCassette;
    }

    public UnitType getCurrentEnemyTypeCassette() {
        return currentEnemyTypeCassette;
    }
}
