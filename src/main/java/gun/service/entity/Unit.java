package gun.service.entity;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class Unit {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    private Integer posX;
    private Integer posY;
    private Integer protectionLevel;

    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    private Boolean isAlive;
    private Integer battlefieldId;

    public Unit() {
    }

    public Unit(Integer posX, Integer posY, Integer protectionLevel, UnitType unitType, Boolean isAlive) {
        this.posX = posX;
        this.posY = posY;
        this.protectionLevel = protectionLevel;
        this.unitType = unitType;
        this.isAlive = isAlive;
    }
}
