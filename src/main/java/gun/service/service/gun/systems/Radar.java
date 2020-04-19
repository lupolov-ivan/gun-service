package gun.service.service.gun.systems;


import gun.service.dto.UnitDto;
import gun.service.entity.UnitType;
import gun.service.service.BattleManagerService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gun.service.entity.UnitState.ACTIVE;
import static gun.service.entity.UnitType.INFANTRY;
import static gun.service.entity.UnitType.TANK;

@Slf4j
public class Radar {

    private final BattleManagerService battleManagerService;

    private final Set<UnitType> ignoreTypes;

    public Radar(BattleManagerService battleManagerService) {
        this.battleManagerService = battleManagerService;
        this.ignoreTypes = new HashSet<>();
    }

    public List<UnitDto> checkField() {

        List<UnitDto> enemiesPosition = new ArrayList<>();

        log.info("Radar are starting checking battlefield...");

        battleManagerService.getAllUnitsOnTheBattlefield().forEach(unit -> {
            if (unit.getUnitState().equals(ACTIVE) && (unit.getUnitType().equals(TANK) || unit.getUnitType().equals(INFANTRY))) {
                enemiesPosition.add(unit);
                log.info("Detected new enemy: {}", unit);
            }
        });

        log.info("Radar finish checking battlefield. Enemy count: {}", enemiesPosition.size());

        return enemiesPosition;
    }

    public boolean addTypeToIgnore(UnitType type) {
        return ignoreTypes.add(type);
    }

    public boolean removeTypeFromIgnore(UnitType type){
        return ignoreTypes.remove(type);
    }

    public int getSizeIgnoreList() {
        return ignoreTypes.size();
    }
}
