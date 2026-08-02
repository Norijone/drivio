import java.util.List;


public interface Entretenable {
    void signalerEntretien(String description);
    void planifierEntretien(String dateEntretien);
    List<String> getHistoriqueEntretien();
    boolean necessiteEntretien();
}
