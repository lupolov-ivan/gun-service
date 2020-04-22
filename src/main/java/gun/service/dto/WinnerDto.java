package gun.service.dto;

import gun.service.entity.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WinnerDto {
    private UnitType winner;
}
