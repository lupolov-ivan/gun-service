package gun.service.service;

import gun.service.dto.CreateSubdivisionDto;
import gun.service.dto.Location;
import gun.service.dto.UnitDto;
import gun.service.entity.Subdivision;
import gun.service.entity.Unit;
import gun.service.entity.UnitState;
import gun.service.entity.UnitType;
import gun.service.exceptions.NotFoundException;
import gun.service.repository.SubdivisionRepository;
import gun.service.service.gun.AutomaticFireComplex;
import gun.service.service.gun.ammunition.Ammunition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static gun.service.entity.UnitState.ACTIVE;
import static gun.service.entity.UnitState.DEAD;
import static gun.service.entity.UnitType.AFC;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubdivisionService {

    private final BattleManagerService battleManagerService;
    private final UnitService unitService;
    private final SubdivisionRepository subdivisionRepository;

    public Subdivision createEmptySubdivision(Subdivision subdivision) {
        return subdivisionRepository.save(subdivision);
    }

    public Subdivision createFilledSubdivision(CreateSubdivisionDto dto) {
        Subdivision subdivision = new Subdivision();
        subdivision.setName(dto.getName());

        Subdivision subdivisionWithId = subdivisionRepository.save(subdivision);

        List<Location> unitsLocation = dto.getUnitLocation();

        unitsLocation.forEach(location -> {
            Unit unit = new Unit();

            unit.setPosX(location.getPosX());
            unit.setPosY(location.getPosY());
            unit.setProtectionLevel(dto.getProtectionLevel());
            unit.setUnitType(AFC);
            unit.setUnitState(ACTIVE);

            unit.setShotPeriod(dto.getShotPeriod());
            unit.setLoadCassetteTime(dto.getLoadCassetteTime());
            unit.setDisconnectCassetteTime(dto.getDisconnectCassetteTime());

            unit.setSubdivisionId(subdivisionWithId.getId());

            unit.setCapacityBurstingCassette(dto.getCapacityBurstingCassette());
            unit.setQuantityBurstingCassette(dto.getQuantityBurstingCassette());
            unit.setCapacityArmorPiercingCassette(dto.getCapacityArmorPiercingCassette());
            unit.setQuantityArmorPiercingCassette(dto.getQuantityArmorPiercingCassette());

            unitService.saveUnit(unit);
        });

        return subdivisionWithId;
    }

    public void deleteSubdivision(Integer subdivisionId) {
        subdivisionRepository.findById(subdivisionId).orElseThrow(NotFoundException::new);

        List<Unit> units = unitService.getUnitsBySubdivisionId(subdivisionId);
        units.forEach(unit -> unit.setSubdivisionId(null));
        unitService.saveAllUnit(units);
        subdivisionRepository.deleteById(subdivisionId);
    }

    public void startPatrolling(Integer subdivisionId, Integer battleId) {

        List<Unit> units = unitService.getUnitsBySubdivisionId(subdivisionId);
        battleManagerService.setBattleId(battleId);

        units.forEach(unit -> {
            AutomaticFireComplex afc = new AutomaticFireComplex(
                    unit.getId(),
                    unit.getPosX(),
                    unit.getPosY(),
                    unit.getProtectionLevel(),
                    unit.getUnitType(),
                    unit.getUnitState(),
                    unit.getShotPeriod(),
                    unit.getLoadCassetteTime(),
                    unit.getDisconnectCassetteTime(),
                    Ammunition.createAmmunition(unit.getQuantityBurstingCassette(),
                                                unit.getCapacityBurstingCassette(),
                                                unit.getQuantityArmorPiercingCassette(),
                                                unit.getCapacityBurstingCassette()),
                    battleManagerService,
                    unitService
            );
            new Thread(afc).start();
            log.debug("New AFC({}) start patrolling", afc);
        });
    }

    public void addUnitToSubdivisions(Integer unitId, Integer subdivisionId) {

        subdivisionRepository.findById(subdivisionId).orElseThrow(NotFoundException::new);

        Unit maybeUnit = unitService.findById(unitId).orElseThrow(NotFoundException::new);

        maybeUnit.setSubdivisionId(subdivisionId);
        unitService.saveUnit(maybeUnit);
    }

    public void removeUnitFromSubdivisions(Integer unitId) {

        Unit maybeUnit = unitService.findById(unitId).orElseThrow(NotFoundException::new);

        maybeUnit.setSubdivisionId(null);
        unitService.saveUnit(maybeUnit);
    }

    public void setStateUnitsDeadBySubdivisionId(Integer subdivisionId) {
        subdivisionRepository.findById(subdivisionId).orElseThrow(NotFoundException::new);

        List<Unit> units = unitService.getUnitsBySubdivisionId(subdivisionId);
        units.forEach(unit -> unit.setUnitState(DEAD));
        unitService.saveAllUnit(units);
        log.debug("All guns get state DEAD");
    }

    public List<UnitDto> getSubdivisionUnitsById(Integer id) {
        List<Unit> units = unitService.getUnitsBySubdivisionId(id);
        List<UnitDto> unitDtoList = new ArrayList<>();

        units.forEach(unit -> {
            UnitDto unitDto = new UnitDto();

            unitDto.setUnitId(unit.getId());
            unitDto.setPosX(unit.getPosX());
            unitDto.setPosY(unit.getPosY());
            unitDto.setProtectionLevel(unit.getProtectionLevel());
            unitDto.setUnitType(unit.getUnitType());
            unitDto.setUnitState(unit.getUnitState());

            unitDtoList.add(unitDto);
        });

        return unitDtoList;
    }

    public Subdivision getSubdivisionById(Integer subdivisionId) {
        return subdivisionRepository.findById(subdivisionId).orElseThrow(NotFoundException::new);
    }
}
