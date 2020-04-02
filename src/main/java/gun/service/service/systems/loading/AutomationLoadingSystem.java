package gun.service.service.systems.loading;


import gun.service.entity.UnitType;
import gun.service.service.ammunition.Ammunition;
import gun.service.service.ammunition.Cassette;
import org.springframework.stereotype.Service;

@Service
public abstract class AutomationLoadingSystem {

    protected Cassette currentCassette;
    protected UnitType currentEnemyTypeCassette;
    protected Ammunition ammunition;

    protected AutomationLoadingSystem() {
        this.ammunition = Ammunition.createAmmunition();
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
