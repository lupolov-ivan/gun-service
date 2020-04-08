package gun.service.service;

import gun.service.dto.UnitDto;
import gun.service.entity.Subdivision;
import gun.service.entity.Unit;
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

    public void deleteSubdivision(Integer id) {
        List<Unit> units = unitService.getUnitsBySubdivisionId(id);
        units.forEach(unit -> unit.setSubdivisionId(null));
        unitService.saveAllUnit(units);
        subdivisionRepository.deleteById(id);
    }

    public void startPatrolling(Integer id, Integer battleId) {

        List<Unit> units = unitService.getUnitsBySubdivisionId(id);
        battleManagerService.setBattleId(battleId);

        units.forEach(unit -> {
            AutomaticFireComplex afc = new AutomaticFireComplex(
                    unit.getPosX(),
                    unit.getPosY(),
                    unit.getProtectionLevel(),
                    unit.getUnitType(),
                    unit.getIsAlive(),
                    Ammunition.createAmmunition(quantityBurstingCassette, capacityBurstingCassette, quantityArmorPiercingCassette, capacityArmorPiercingCassette),
                    battleManagerService
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

    public List<UnitDto> getSubdivisionUnitsById(Integer id) {
        List<Unit> units = unitService.getUnitsBySubdivisionId(id);
        List<UnitDto> unitDtoList = new ArrayList<>();

        units.forEach(unit -> {
            UnitDto unitDto = new UnitDto();

            unitDto.setPosX(unit.getPosX());
            unitDto.setPosY(unit.getPosY());
            unitDto.setProtectionLevel(unit.getProtectionLevel());
            unitDto.setUnitType(unit.getUnitType());
            unitDto.setIsAlive(unit.getIsAlive());

            unitDtoList.add(unitDto);
        });

        return unitDtoList;
    }
}
