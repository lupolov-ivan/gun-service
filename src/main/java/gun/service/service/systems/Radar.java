package gun.service.service.systems;


import gun.service.dto.UnitDto;
import gun.service.entity.UnitType;
import gun.service.repository.BattleManagerRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Radar {

    private final BattleManagerRepository battleManagerRepository;

    private Set<UnitType> ignoreTypes;

    public Radar(BattleManagerRepository battleManagerRepository) {
        this.battleManagerRepository = battleManagerRepository;
        this.ignoreTypes = new HashSet<>();
    }

    public List<UnitDto> checkField() {

        List<UnitDto> enemiesPosition = new ArrayList<>();

        Battlefield b = battleManagerRepository.getBattlefield();

        int width = b.getWidth();
        int length = b.getLength();

        log.info("Radar are starting checking battlefield...");
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {

                UnitDto unit = battleManagerRepository.findUnitByCoordinate(x, y);

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
