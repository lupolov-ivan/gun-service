package gun.service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    private Integer posX;
    private Integer posY;
    private Integer protectionLevel;
    private Boolean isAlive;
    @Enumerated(EnumType.STRING)
    private UnitType unitType;
    private Integer subdivisionId;

    public Unit(Integer posX, Integer posY, Integer protectionLevel, UnitType unitType, Boolean isAlive) {
        this.posX = posX;
        this.posY = posY;
        this.protectionLevel = protectionLevel;
        this.unitType = unitType;
        this.isAlive = isAlive;
    }
}
