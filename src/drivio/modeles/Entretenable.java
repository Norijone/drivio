package drivio.modeles;

import java.util.ArrayList;

public interface Entretenable {

    void signalerEntretien(String description);

    void planifierEntretien(String dateEntretien);

    ArrayList<String> getHistoriqueEntretien();

    boolean necessiteEntretien();
}
