import java.util.List;
import java.util.Map;


public class CatalogueModeles {

    public static final Map<String, List<String>> MODELES_PAR_TYPE = Map.of(
            "CAMIONNETTE", List.of(
                    "Ford Transit T250",
                    "Ford Transit T350",
                    "BrightDrop Zevo 400",
                    "BrightDrop Zevo 600"
            ),
            "MINIVAN", List.of(
                    "Dodge Grand Caravan",
                    "Honda Odyssey",
                    "Toyota Sienna"
            ),
            "CUBE", List.of(
                    "Camion Cube 16 pieds",
                    "Camion Cube 20 pieds",
                    "Camion Cube 24 pieds"
            )
    );
}
