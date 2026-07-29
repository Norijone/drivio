import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcule les statistiques de la flotte. Séparée de GestionnaireFlotte (SRP) :
 * cette classe ne fait QUE de l'analyse, jamais de modification d'état.
 */
public class StatistiquesFlotte {

    private final List<Vehicule> flotte;

    public StatistiquesFlotte(List<Vehicule> flotte) {
        this.flotte = flotte;
    }

    public double revenuTotal() {
        double total = 0;
        for (Vehicule v : flotte) {
            total += v.calculerTarif(v.getJoursLouesCumules() == 0 ? 0 : v.getJoursLouesCumules());
        }
        return total;
    }

    public double kilometrageMoyen() {
        if (flotte.isEmpty()) return 0;
        return flotte.stream().mapToInt(Vehicule::getKilometrage).average().orElse(0);
    }

    /** Taux d'utilisation par type de véhicule = jours loués cumulés / nombre de véhicules du type. */
    public Map<String, Double> tauxUtilisationParType() {
        Map<String, Integer> joursParType = new HashMap<>();
        Map<String, Integer> nbParType = new HashMap<>();

        for (Vehicule v : flotte) {
            String type = v.getTypeVehicule();
            joursParType.merge(type, v.getJoursLouesCumules(), Integer::sum);
            nbParType.merge(type, 1, Integer::sum);
        }

        Map<String, Double> resultat = new HashMap<>();
        for (String type : joursParType.keySet()) {
            resultat.put(type, (double) joursParType.get(type) / nbParType.get(type));
        }
        return resultat;
    }

    public List<Vehicule> vehiculesLesPlusUtilises(int top) {
        return flotte.stream()
                .sorted(Comparator.comparingInt(Vehicule::getNombreLocations).reversed())
                .limit(top)
                .toList();
    }

    public List<Vehicule> vehiculesNecessitantEntretien() {
        return flotte.stream()
                .filter(Vehicule::necessiteEntretien)
                .toList();
    }

    public Map<String, Long> repartitionParZone() {
        Map<String, Long> repartition = new HashMap<>();
        for (Vehicule v : flotte) {
            repartition.merge(v.getZone().toString(), 1L, Long::sum);
        }
        return repartition;
    }
}
