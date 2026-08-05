package drivio.gestion;

import java.util.ArrayList;

import drivio.modeles.Vehicule;

public class GestionnaireAlertes {

    private ArrayList<Vehicule> flotte;

    public GestionnaireAlertes(ArrayList<Vehicule> flotte) {
        this.flotte = flotte;
    }

    public ArrayList<String> genererAlertes() {
        ArrayList<String> alertes = new ArrayList<>();
        for (int i = 0; i < flotte.size(); i++) {
            Vehicule v = flotte.get(i);
            if (v.necessiteEntretien()) {
                alertes.add("Entretien a venir : " + v.getId() + " (" + v.getModele() + ") - "
                        + v.getKilometrage() + " km");
            }
            if (v.getStatut().equals("EN_ENTRETIEN")) {
                alertes.add("Panne signalee : " + v.getId() + " (" + v.getModele() + ") est en entretien");
            }
        }
        if (alertes.isEmpty()) {
            alertes.add("Aucune alerte active.");
        }
        return alertes;
    }
}
