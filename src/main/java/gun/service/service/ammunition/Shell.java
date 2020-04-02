package gun.service.service.ammunition;

public class Shell {

    private TypeShell type;
    private int damageEnergy;

    public Shell(TypeShell type, int damageEnergy) {
        this.type = type;
        this.damageEnergy = damageEnergy;
    }

    public int getDamageEnergy() {
        return damageEnergy;
    }

    public TypeShell getType() {
        return type;
    }
}
