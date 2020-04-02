package gun.service.dto;

import gun.service.entity.UnitType;
import lombok.Data;

@Data
public class UnitDto {

     private Integer posX;
     private Integer posY;
     private Integer protectionLevel;
     private UnitType unitType;
}
