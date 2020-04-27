package gun.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    private Integer posX;
    private Integer posY;
    private Integer protectionLevel;
    @Enumerated(STRING)
    private UnitType unitType;
    @Enumerated(STRING)
    private UnitState unitState;
    private Integer subdivisionId;

    private Integer loadCassetteTime;
    private Integer disconnectCassetteTime;
    private Integer shotPeriod;

    private Integer quantityBurstingCassette;
    private Integer capacityBurstingCassette;
    private Integer quantityArmorPiercingCassette;
    private Integer capacityArmorPiercingCassette;

    public Unit(Integer id, Integer posX, Integer posY, Integer protectionLevel, UnitType unitType, UnitState unitState) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.protectionLevel = protectionLevel;
        this.unitType = unitType;
        this.unitState = unitState;
    }
}
