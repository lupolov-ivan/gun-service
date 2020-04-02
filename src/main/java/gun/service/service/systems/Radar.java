package gun.service.service.systems;


import gun.service.repository.UnitDataRepository;
import gun.service.service.Battlefield;
import gun.service.dto.UnitDto;
import gun.service.entity.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Radar {

    Logger log = LoggerFactory.getLogger(Radar.class);

    private final UnitDataRepository unitDataRepository;

    private Battlefield battlefield;

    private Set<UnitType> ignoreTypes;

    public Radar(UnitDataRepository unitDataRepository) {
        this.unitDataRepository = unitDataRepository;
        this.ignoreTypes = new HashSet<>();
    }

    public List<UnitDto> checkField() {

        List<UnitDto> enemiesPosition = new ArrayList<>();

        int width = battlefield.getWidth();
        int length = battlefield.getLength();

        log.debug("Radar are starting checking battlefield...");
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {

                Optional<UnitDto> maybeUnit = unitDataRepository.getByCoordinate(x, y);

                maybeUnit.ifPresent(enemiesPosition::add);

                log.debug("Detected new enemy: {}", maybeUnit);
            }
        }
        log.debug("Radar finish checking battlefield. Enemy count: {}", enemiesPosition.size());

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
