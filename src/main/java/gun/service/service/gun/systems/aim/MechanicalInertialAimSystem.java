package gun.service.service.gun.systems.aim;

import gun.service.dto.UnitDto;
import gun.service.entity.UnitType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.random;

@Slf4j
public class MechanicalInertialAimSystem extends AimingSystem {

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
            log.info("Catch last target: {}", closestTarget);

            if(closestTarget.equals(lastTarget)) {
                log.info("Target caught is the same");
                countShotSameTarget++;
            } else {
                log.info("Target caught is new");
                countShotSameTarget = 1;
            }
            lastTarget = closestTarget;
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

        log.info("Catch new target: {}", closestTarget);

        if(closestTarget.equals(lastTarget)) {
            log.info("Target caught is the same");
            lastTarget = closestTarget;
            countShotSameTarget++;
        } else {
            log.info("Target caught is new");
            lastTarget = closestTarget;
            countShotSameTarget = 1;
        }
        return lastTarget;
    }

    public double computeAccuracyFactor(UnitType unitType) {
        if (unitType.equals(UnitType.TANK)) {
            if(countShotSameTarget == 1) {
                double min = 0.2;
                double max = 0.6;
                return getCoefficient(min, max);
            } else if (countShotSameTarget == 2) {
                double min = 0.5;
                double max = 0.8;
                return getCoefficient(min, max);
            } else if (countShotSameTarget >= 3) {
                double min = 0.8;
                double max = 1.0;
                return getCoefficient(min, max);
            }
        }
        if (unitType.equals(UnitType.INFANTRY)) {
            if(countShotSameTarget == 1) {
                double min = 0.2;
                double max = 1.0;
                return getCoefficient(min, max);
            } else if (countShotSameTarget >= 2) {
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
