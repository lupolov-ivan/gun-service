package gun.service.service.gun.systems.fire;


import gun.service.dto.UnitDto;
import gun.service.exceptions.ShellJammedException;
import gun.service.service.gun.ammunition.Shell;

import java.util.Random;

public abstract class FireSystem {

    protected int shotPeriod;
    protected Shell currentShell;

    abstract public boolean makeShot(UnitDto data);

    protected void isJammed() throws ShellJammedException {
        Random random = new Random();

        if (random.nextInt(100) < 5) {
            throw new ShellJammedException("Shell is Jammed");
        }
    }
}
