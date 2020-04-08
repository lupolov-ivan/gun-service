package gun.service.service;

import gun.service.dto.UnitDamageDto;
import gun.service.dto.UnitDto;
import gun.service.repository.BattleManagerRepository;
import gun.service.service.gun.systems.Battlefield;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleManagerService {

    private final BattleManagerRepository battleManagerRepository;

    private Integer battleId;

    public UnitDto findUnitByCoordinateAndBattleId(Integer posX, Integer posY) {
        return battleManagerRepository.findUnitByCoordinateAndBattleId(posX, posY, battleId);
    }

    public void setUnitDamage(UnitDamageDto damageDto) {
        battleManagerRepository.setUnitDamage(damageDto, battleId);
    }

    public Battlefield getBattlefield() {
        return battleManagerRepository.getBattlefield();
    }

    public void setBattleId(Integer battleId) {
        this.battleId = battleId;
    }
}
