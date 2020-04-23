package gun.service.service;

import gun.service.dto.UnitStateDto;
import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.dto.WinnerDto;
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

    public void updateUnitState(UnitStateDto dto) {
        battleManagerRepository.updateUnitState(dto, battleId);
    }

    public void setBattleId(Integer battleId) {
        this.battleId = battleId;
    }

    public void stopBattle(WinnerDto winner) {
        battleManagerRepository.stopBattle(battleId, winner);
    }
}
