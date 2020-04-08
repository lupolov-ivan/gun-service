package gun.service.service.gun.ammunition;

import gun.service.entity.UnitType;

import java.util.ArrayList;
import java.util.List;

public class Ammunition {

    private final static int DEFAULT_BURSTING_CASSETTE_QUANTITY = 10;
    private final static int DEFAULT_ARMOR_PIERCING_CASSETTE_QUANTITY = 15;

    private List<Cassette> burstingCassettes;
    private List<Cassette> armorPiercingCassettes;

    private int quantityBurstingCassette;
    private int balanceBurstingCassette;
    private int quantityArmorPiercingCassette;
    private int balanceArmorPiercingCassette;

    private Cassette lastReceivedCassette;

    public Ammunition(int quantityBurstingCassette, int quantityArmorPiercingCassette) {
        this.burstingCassettes = new ArrayList<>(quantityBurstingCassette);
        this.armorPiercingCassettes = new ArrayList<>(quantityArmorPiercingCassette);
        this.quantityBurstingCassette = quantityBurstingCassette;
        this.quantityArmorPiercingCassette = quantityArmorPiercingCassette;
    }

    public static Ammunition createAmmunition(int quantityBurstingCassette, int capacityBurstingCassette, int quantityArmorPiercingCassette, int capacityArmorPiercingCassette) {
        Ammunition ammunition = new Ammunition(quantityBurstingCassette, quantityArmorPiercingCassette);

        for (int i = 0; i < quantityBurstingCassette; i++) {
            ammunition.addCassette(Cassette.createCassette(TypeShell.BURSTING, capacityBurstingCassette));
        }
        for (int i = 0; i < quantityArmorPiercingCassette; i++) {
            ammunition.addCassette(Cassette.createCassette(TypeShell.ARMOR_PIERCING, capacityArmorPiercingCassette));
        }

        return ammunition;
    }

    public static Ammunition createAmmunition() {
        return createAmmunition(DEFAULT_BURSTING_CASSETTE_QUANTITY, 10, DEFAULT_ARMOR_PIERCING_CASSETTE_QUANTITY, 10);
    }

    public boolean addCassette (Cassette cassette) {
        if (cassette.getTypeShell().equals(TypeShell.BURSTING)) {
            if (burstingCassettes.size() < quantityBurstingCassette) {
                burstingCassettes.add(cassette);
                balanceBurstingCassette = burstingCassettes.size();
                return true;
            }
        }

        if (cassette.getTypeShell().equals(TypeShell.ARMOR_PIERCING)) {
            if (armorPiercingCassettes.size() < quantityArmorPiercingCassette) {
                armorPiercingCassettes.add(cassette);
                balanceArmorPiercingCassette = armorPiercingCassettes.size();
                return true;
            }
        }
        return false;
    }

    public boolean hasNext(UnitType unitType) {
        if (unitType.equals(unitType.TANK)) {
            return balanceArmorPiercingCassette > 0;
        }
        if (unitType.equals(unitType.INFANTRY)){
            return balanceBurstingCassette > 0;
        }
        return false;
    }

    public Cassette getCassette(UnitType enemyData) {
        if (enemyData.equals(UnitType.TANK)) {
            balanceArmorPiercingCassette--;
            lastReceivedCassette = armorPiercingCassettes.remove(0);
        }
        if (enemyData.equals(UnitType.INFANTRY)) {
            balanceBurstingCassette--;
            lastReceivedCassette = burstingCassettes.remove(0);
        }
        return lastReceivedCassette;
    }

    public List<Cassette> getArmorPiercingCassettes() {
        return armorPiercingCassettes;
    }

    public List<Cassette> getBurstingCassettes() {
        return burstingCassettes;
    }

    public int getQuantityArmorPiercingCassette() {
        return quantityArmorPiercingCassette;
    }

    public int getQuantityBurstingCassette() {
        return quantityBurstingCassette;
    }

    @Override
    public String toString() {
        return "Ammunition{" +
                "burstingCassettes=" + burstingCassettes +
                ", armorPiercingCassettes=" + armorPiercingCassettes +
                ", quantityBurstingCassette=" + quantityBurstingCassette +
                ", balanceBurstingCassette=" + balanceBurstingCassette +
                ", quantityArmorPiercingCassette=" + quantityArmorPiercingCassette +
                ", balanceArmorPiercingCassette=" + balanceArmorPiercingCassette +
                ", lastReceivedCassette=" + lastReceivedCassette +
                '}';
    }
}
