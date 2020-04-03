package gun.service.service.systems.aim;

import gun.service.dto.UnitDto;
import gun.service.entity.UnitType;

import java.util.List;

public abstract class AimingSystem {

    protected UnitDto lastTarget;
    protected int countShotSameTarget = 1;

    public abstract UnitDto catchTarget(List<UnitDto> enemies);

    public abstract double computeAccuracyFactor(UnitType unitType);
}
