package gun.service.service.systems.aim;

import gun.service.dto.UnitDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class AimingSystem {

    protected UnitDto lastTarget;
    protected int countShotSameTarget = 1;

    public abstract UnitDto catchTarget(List<UnitDto> enemies);
}
