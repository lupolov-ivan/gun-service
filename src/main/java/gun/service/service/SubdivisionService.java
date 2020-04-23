package gun.service.service;

import gun.service.dto.UnitDto;
import gun.service.entity.Subdivision;
import gun.service.entity.Unit;
import gun.service.entity.UnitState;
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

import static gun.service.entity.UnitState.DEAD;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubdivisionService {

    @Value("${gun-specifications.quantity-bursting-cassette}")
    private Integer quantityBurstingCassette;
    @Value("${gun-specifications.capacity-bursting-cassette}")
    private Integer capacityBurstingCassette;
    @Value("${gun-specifications.quantity-armor-piercing-cassette}")
    private Integer quantityArmorPiercingCassette;
    @Value("${gun-specifications.capacity-armor-piercing-cassette}")
    private Integer capacityArmorPiercingCassette;

    private final BattleManagerService battleManagerService;
    private final UnitService unitService;
    private final SubdivisionRepository subdivisionRepository;

    public Subdivision createSubdivision(Subdivision subdivision) {
        return subdivisionRepository.save(subdivision);
    }

    public void deleteSubdivision(Integer subdivisionId) {
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
                    Ammunition.createAmmunition(quantityBurstingCassette, capacityBurstingCassette, quantityArmorPiercingCassette, capacityArmorPiercingCassette),
                    battleManagerService,
                    unitService
            );
            new Thread(afc).start();
            log.info("New AFC({}) start patrolling", afc);
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
        log.info("All guns get state DEAD");
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
