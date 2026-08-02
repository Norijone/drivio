import java.util.ArrayList;
import java.util.List;


public class GestionnaireAlertes {

    private final List<Vehicule> flotte;

    public GestionnaireAlertes(List<Vehicule> flotte) {
        this.flotte = flotte;
    }

    public List<String> genererAlertes() {
        List<String> alertes = new ArrayList<>();
        for (Vehicule v : flotte) {
            if (v.necessiteEntretien()) {
                alertes.add("Entretien à venir : " + v.getId() + " (" + v.getModele() + ") - "
                        + v.getKilometrage() + " km");
            }
            if (v.getStatut() == StatutVehicule.EN_ENTRETIEN) {
                alertes.add("Panne signalée : " + v.getId() + " (" + v.getModele() + ") est en entretien");
            }
        }
        if (alertes.isEmpty()) {
            alertes.add("Aucune alerte active.");
        }
        return alertes;
    }
}
