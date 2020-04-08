package gun.service.service.gun.systems.loading;


import gun.service.entity.UnitType;
import gun.service.service.gun.ammunition.Ammunition;
import gun.service.service.gun.ammunition.Cassette;

public abstract class AutomationLoadingSystem {

    protected Cassette currentCassette;
    protected UnitType currentEnemyTypeCassette;
    protected Ammunition ammunition;

    protected AutomationLoadingSystem(Ammunition ammunition) {
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
