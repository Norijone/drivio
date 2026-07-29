import java.util.ArrayList;
import java.util.List;

/**
 * Gère les opérations de location, retour et entretien sur la flotte.
 * Respecte le SRP : cette classe ne s'occupe QUE des opérations métier,
 * pas du chargement CSV ni des statistiques (voir LecteurCSV, StatistiquesFlotte).
 */
public class GestionnaireFlotte implements GestionLocation {

    private final List<Vehicule> flotte;

    public GestionnaireFlotte(List<Vehicule> flotte) {
        this.flotte = flotte;
    }

    public List<Vehicule> getFlotte() {
        return flotte;
    }

    public void ajouterVehicule(Vehicule vehicule) {
        flotte.add(vehicule);
    }

    public void retirerVehicule(String id) {
        flotte.removeIf(v -> v.getId().equals(id));
    }

    public Vehicule trouverParId(String id) {
        return flotte.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Vehicule> getVehiculesDisponibles() {
        List<Vehicule> disponibles = new ArrayList<>();
        for (Vehicule v : flotte) {
            if (v.getStatut() == StatutVehicule.DISPONIBLE) {
                disponibles.add(v);
            }
        }
        return disponibles;
    }

    @Override
    public void louer(Vehicule vehicule, String chauffeur, int joursLocation) throws VehiculeIndisponibleException {
        if (vehicule.getStatut() != StatutVehicule.DISPONIBLE) {
            throw new VehiculeIndisponibleException(
                    "Le véhicule " + vehicule.getId() + " n'est pas disponible (statut : " + vehicule.getStatut() + ")");
        }
        if (joursLocation <= 0) {
            throw new VehiculeIndisponibleException("Le nombre de jours de location doit être positif.");
        }
        vehicule.enregistrerLocation(chauffeur, joursLocation);
    }

    @Override
    public void retourner(Vehicule vehicule, int kilometrageParcouru)
            throws KilometrageInvalideException, VehiculeIndisponibleException {
        if (vehicule.getStatut() != StatutVehicule.LOUE) {
            throw new VehiculeIndisponibleException(
                    "Le véhicule " + vehicule.getId() + " n'est pas actuellement loué.");
        }
        if (kilometrageParcouru < 0) {
            throw new KilometrageInvalideException(
                    "Le kilométrage parcouru ne peut pas être négatif (reçu : " + kilometrageParcouru + ").");
        }
        vehicule.enregistrerRetour(kilometrageParcouru);
    }

    @Override
    public void renouveler(Vehicule vehicule, int joursSupplementaires) throws VehiculeIndisponibleException {
        if (vehicule.getStatut() != StatutVehicule.LOUE) {
            throw new VehiculeIndisponibleException(
                    "Impossible de renouveler : le véhicule " + vehicule.getId() + " n'est pas actuellement loué.");
        }
        if (joursSupplementaires <= 0) {
            throw new VehiculeIndisponibleException("Le nombre de jours supplémentaires doit être positif.");
        }
        vehicule.joursLouesCumules += joursSupplementaires;
        vehicule.getHistoriqueLocation().add("Location renouvelée pour " + joursSupplementaires + " jour(s) de plus");
    }
}
