package drivio.utilitaires;

import java.util.ArrayList;

public class CatalogueModeles {

    public static ArrayList<String> getModeles(String type) {
        ArrayList<String> modeles = new ArrayList<>();
        if (type.equals("CAMIONNETTE")) {
            modeles.add("Ford Transit T250");
            modeles.add("Ford Transit T350");
            modeles.add("BrightDrop Zevo 400");
            modeles.add("BrightDrop Zevo 600");
        } else if (type.equals("MINIVAN")) {
            modeles.add("Dodge Grand Caravan");
            modeles.add("Honda Odyssey");
            modeles.add("Toyota Sienna");
        } else if (type.equals("CUBE")) {
            modeles.add("Camion Cube 16 pieds");
            modeles.add("Camion Cube 20 pieds");
            modeles.add("Camion Cube 24 pieds");
        }
        return modeles;
    }
}
