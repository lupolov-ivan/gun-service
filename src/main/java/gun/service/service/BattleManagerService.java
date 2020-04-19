package gun.service.service;

import gun.service.dto.SetUnitStateDto;
import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.repository.BattleManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BattleManagerService {

    private final BattleManagerRepository battleManagerRepository;

    private Integer battleId;

    public List<UnitDto> getAllUnitsOnTheBattlefield() {
        return battleManagerRepository.getAllUnitsOnTheBattlefield(battleId);
    }

    public void setUnitDamage(UnitDamageDto damageDto) {
        battleManagerRepository.setUnitDamage(damageDto, battleId);
    }

    public void updateUnitState(SetUnitStateDto dto) {
        battleManagerRepository.updateUnitState(dto, battleId);
    }

    public void setBattleId(Integer battleId) {
        this.battleId = battleId;
    }
}
