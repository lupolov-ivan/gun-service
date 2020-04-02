package gun.service.service.ammunition;

import java.util.ArrayList;
import java.util.List;

public class Cassette {

    private final static int DEFAULT_CAPACITY = 10;

    private List<Shell> shells;
    private TypeShell typeShell;
    private int capacity;
    private int balance;


    private Cassette(TypeShell typeShell, int capacity) {
        this.typeShell = typeShell;
        this.capacity = capacity;
        shells = new ArrayList<>();
    }

    public static Cassette createCassette(TypeShell typeShell, int capacity) {
        Cassette cassette = new Cassette(typeShell, capacity);
        for (int i = 0; i < capacity; i++) {
            cassette.add(new Shell(typeShell, 1));
        }
        return cassette;
    }

    public static Cassette createCassette(TypeShell typeShell) {
        return createCassette(typeShell, DEFAULT_CAPACITY);
    }

    public boolean add(Shell shell) {
        if (shell.getType().equals(typeShell) && shells.size() < capacity) {
                shells.add(shell);
                balance = shells.size();
            return true;
        } else {
            return false;
        }
    }

    public boolean hasNext() {
        return balance > 0;
    }

    public Shell getShell() {
        balance--;
        return shells.remove(0);
    }

    public int getBalance() {
        return balance;
    }

    public TypeShell getTypeShell() {
        return typeShell;
    }

    @Override
    public String toString() {
        return "Cassette{" +
                "typeShell=" + typeShell +
                ", capacity=" + capacity +
                ", balance=" + balance +
                '}';
    }
}
