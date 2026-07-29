import java.util.List;

/**
 * Contrat pour tout véhicule pouvant faire l'objet d'un suivi d'entretien.
 * Sépare la responsabilité "entretien" de la responsabilité "location" (SRP).
 */
public interface Entretenable {
    void signalerEntretien(String description);
    void planifierEntretien(String dateEntretien);
    List<String> getHistoriqueEntretien();
    boolean necessiteEntretien();
}
