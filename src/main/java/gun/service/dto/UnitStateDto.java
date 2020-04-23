package gun.service.dto;

import gun.service.entity.UnitState;
import gun.service.entity.UnitType;
import lombok.Data;

@Data
public class UnitStateDto {

    private Integer unitId;
    private UnitType unitType;
    private UnitState unitState;
}
