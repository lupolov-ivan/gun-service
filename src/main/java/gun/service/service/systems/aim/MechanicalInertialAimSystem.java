package gun.service.service.systems.aim;

import gun.service.dto.UnitDto;
import gun.service.entity.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class MechanicalInertialAimSystem extends AimingSystem {

    Logger log = LoggerFactory.getLogger(MechanicalInertialAimSystem.class);

    private int posX;
    private int posY;

    public MechanicalInertialAimSystem(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public UnitDto catchTarget(List<UnitDto> enemies) {

        UnitDto closestTarget = enemies.get(0);
        int currentMinDistance = abs(posX - closestTarget.getPosX()) + abs(posY - closestTarget.getPosY());

        if(enemies.size() == 1) {
            log.debug("Catch last target: {}", closestTarget);

            if(closestTarget.equals(lastTarget)) {
                log.debug("Target caught is the same");
                countShotSameTarget++;
            } else {
                log.debug("Target caught is new");
                countShotSameTarget = 1;
            }
            lastTarget = closestTarget;
            //lastTarget.setAccuracyFactor(computeAccuracyFactor(countShotSameTarget, lastTarget.getUnitType())); TODO: replace to DamageDTO
            return lastTarget;
        }

        for (int i = 1; i < enemies.size(); i++) {

            UnitDto currentTarget = enemies.get(i);

            int currentTargetPosX = currentTarget.getPosX();
            int currentTargetPosY = currentTarget.getPosY();

            int distance = abs(posX - currentTargetPosX) + abs(posY - currentTargetPosY);
            if (distance < currentMinDistance) {
                currentMinDistance = distance;
                closestTarget = currentTarget;
            } else if(distance == currentMinDistance) {
                if (currentTargetPosY <= closestTarget.getPosY()) {
                    closestTarget = currentTarget;
                }
            }
        }

        log.debug("Catch new target: {}", closestTarget);

        if(closestTarget.equals(lastTarget)) {
            log.debug("Target caught is the same");
            lastTarget = closestTarget;
            countShotSameTarget++;
        } else {
            log.debug("Target caught is new");
            lastTarget = closestTarget;
            countShotSameTarget = 1;
        }
        //lastTarget.setAccuracyFactor(computeAccuracyFactor(countShotSameTarget, lastTarget.getUnitType())); TODO: replace to DamageDTO
        return lastTarget;
    }

    private double computeAccuracyFactor(int shotSameTarget, UnitType unitType) {
        if (unitType.equals(unitType.TANK)) {
            if(shotSameTarget == 1) {
                double min = 0.2;
                double max = 0.6;
                return getCoefficient(min, max);
            } else if (shotSameTarget == 2) {
                double min = 0.5;
                double max = 0.8;
                return getCoefficient(min, max);
            } else if (shotSameTarget >= 3) {
                double min = 0.8;
                double max = 1.0;
                return getCoefficient(min, max);
            }
        }
        if (unitType.equals(unitType.INFANTRY)) {
            if(shotSameTarget == 1) {
                double min = 0.2;
                double max = 1.0;
                return getCoefficient(min, max);
            } else if (shotSameTarget >= 2) {
                double min = 0.5;
                double max = 1.0;
                return getCoefficient(min, max);
            }
        }
        return 0.0;
    }

    private double getCoefficient(double min, double max) {
        return random() * (max - min) + min;
    }
}
