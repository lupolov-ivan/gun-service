package gun.service.service.gun.systems;


import gun.service.dto.UnitDto;
import gun.service.entity.UnitType;
import gun.service.service.BattleManagerService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class Radar {

    private final BattleManagerService battleManagerService;

    private Set<UnitType> ignoreTypes;

    public Radar(BattleManagerService battleManagerService) {
        this.battleManagerService = battleManagerService;
        this.ignoreTypes = new HashSet<>();
    }

    public List<UnitDto> checkField() {

        List<UnitDto> enemiesPosition = new ArrayList<>();

        Battlefield b = battleManagerService.getBattlefield();

        int width = b.getWidth();
        int length = b.getLength();

        log.info("Radar are starting checking battlefield...");
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {

                UnitDto unit = battleManagerService.findUnitByCoordinate(x, y);

                if (unit != null && unit.getIsAlive() && (unit.getUnitType().equals(UnitType.TANK) || unit.getUnitType().equals(UnitType.INFANTRY))) {
                    enemiesPosition.add(unit);
                    log.info("Detected new enemy: {}", unit);
                }
            }
        }
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
