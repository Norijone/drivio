import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface graphique principale de Drivio (Swing).
 * Permet d'ajouter, modifier, retirer, louer et retourner des véhicules,
 * avec affichage des erreurs de validation via des boîtes de dialogue.
 */
public class DrivioGUI extends JFrame {

    private static final String CHEMIN_CSV = "data/vehicules.csv";
    private static final String[] COLONNES = {
            "ID", "Modèle", "Type", "Énergie", "Capacité (kg)", "Kilométrage",
            "Statut", "Zone", "Chauffeur", "Tarif base ($)"
    };

    private final LecteurCSV lecteurCSV = new LecteurCSV();
    private final GestionnaireFlotte gestionnaire;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public DrivioGUI(List<Vehicule> vehiculesInitiaux) {
        super("Drivio — Gestion de flotte de véhicules de livraison");
        this.gestionnaire = new GestionnaireFlotte(vehiculesInitiaux);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(COLONNES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // édition uniquement via les formulaires (validation centralisée)
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construireBarreOutils(), BorderLayout.NORTH);
        add(construireBarreStatut(), BorderLayout.SOUTH);

        rafraichirTable();
    }

    private JPanel construireBarreOutils() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        JButton btnAjouter = new JButton("Ajouter un véhicule");
        JButton btnModifier = new JButton("Modifier");
        JButton btnRetirer = new JButton("Retirer");
        JButton btnLouer = new JButton("Louer");
        JButton btnRetourner = new JButton("Retourner");
        JButton btnEntretien = new JButton("Signaler entretien");
        JButton btnStats = new JButton("Statistiques");
        JButton btnAlertes = new JButton("Alertes");
        JButton btnRapport = new JButton("Générer rapport");

        btnAjouter.addActionListener(e -> ajouterVehicule());
        btnModifier.addActionListener(e -> modifierVehicule());
        btnRetirer.addActionListener(e -> retirerVehicule());
        btnLouer.addActionListener(e -> louerVehicule());
        btnRetourner.addActionListener(e -> retournerVehicule());
        btnEntretien.addActionListener(e -> signalerEntretien());
        btnStats.addActionListener(e -> afficherStatistiques());
        btnAlertes.addActionListener(e -> afficherAlertes());
        btnRapport.addActionListener(e -> genererRapport());

        for (JButton b : new JButton[]{btnAjouter, btnModifier, btnRetirer, btnLouer,
                btnRetourner, btnEntretien, btnStats, btnAlertes, btnRapport}) {
            panel.add(b);
        }
        return panel;
    }

    private JLabel labelStatut;

    private JPanel construireBarreStatut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelStatut = new JLabel(" ");
        panel.add(labelStatut);
        return panel;
    }

    private void setStatut(String message) {
        labelStatut.setText(message);
    }

    // ---------------------------------------------------------------
    // Rafraîchissement / persistance
    // ---------------------------------------------------------------

    private void rafraichirTable() {
        tableModel.setRowCount(0);
        for (Vehicule v : gestionnaire.getFlotte()) {
            tableModel.addRow(new Object[]{
                    v.getId(), v.getModele(), v.getTypeVehicule(), v.getTypeEnergie(),
                    v.getCapaciteKg(), v.getKilometrage(), v.getStatut(), v.getZone(),
                    v.getChauffeurAssigne() == null || v.getChauffeurAssigne().isBlank() ? "-" : v.getChauffeurAssigne(),
                    String.format("%.2f", v.getTarifBase())
            });
        }
    }

    private void sauvegarderCSV() {
        try {
            lecteurCSV.ecrireVehicules(CHEMIN_CSV, gestionnaire.getFlotte());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde du CSV : " + e.getMessage(),
                    "Erreur d'écriture", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vehicule getVehiculeSelectionne() {
        int ligne = table.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionne d'abord un véhicule dans la liste.",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String id = (String) tableModel.getValueAt(ligne, 0);
        return gestionnaire.trouverParId(id);
    }

    // ---------------------------------------------------------------
    // Ajouter
    // ---------------------------------------------------------------

    private void ajouterVehicule() {
        JTextField idField = new JTextField();
        JTextField modeleField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"CAMIONNETTE", "MINIVAN", "CUBE"});
        JComboBox<String> energieCombo = new JComboBox<>(new String[]{"ESSENCE", "DIESEL", "ELECTRIQUE"});
        JTextField capaciteField = new JTextField();
        JTextField kmField = new JTextField("0");
        JComboBox<String> zoneCombo = new JComboBox<>(new String[]{"MONTREAL", "RIVE_NORD", "RIVE_SUD"});
        JTextField chauffeurField = new JTextField();
        JTextField tarifField = new JTextField();

        JPanel panel = construirePanelFormulaire(
                "ID :", idField, "Modèle :", modeleField, "Type :", typeCombo,
                "Énergie :", energieCombo, "Capacité (kg) :", capaciteField,
                "Kilométrage :", kmField, "Zone :", zoneCombo,
                "Chauffeur assigné (optionnel) :", chauffeurField, "Tarif de base ($) :", tarifField);

        int resultat = JOptionPane.showConfirmDialog(this, panel, "Ajouter un véhicule",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultat != JOptionPane.OK_OPTION) return;

        try {
            if (gestionnaire.trouverParId(idField.getText().trim()) != null) {
                throw new DonneeInvalideException("Un véhicule avec l'id \"" + idField.getText().trim() + "\" existe déjà.");
            }
            Vehicule nouveau = VehiculeFactory.creerVehicule(
                    idField.getText(), modeleField.getText(), (String) typeCombo.getSelectedItem(),
                    (String) energieCombo.getSelectedItem(), capaciteField.getText(), kmField.getText(),
                    "DISPONIBLE", (String) zoneCombo.getSelectedItem(), chauffeurField.getText(), tarifField.getText());

            gestionnaire.ajouterVehicule(nouveau);
            rafraichirTable();
            sauvegarderCSV();
            setStatut("Véhicule " + nouveau.getId() + " ajouté avec succès.");
        } catch (DonneeInvalideException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------------
    // Modifier
    // ---------------------------------------------------------------

    private void modifierVehicule() {
        Vehicule v = getVehiculeSelectionne();
        if (v == null) return;

        JTextField modeleField = new JTextField(v.getModele());
        JComboBox<String> energieCombo = new JComboBox<>(new String[]{"ESSENCE", "DIESEL", "ELECTRIQUE"});
        energieCombo.setSelectedItem(v.getTypeEnergie().toString());
        JTextField capaciteField = new JTextField(String.valueOf(v.getCapaciteKg()));
        JTextField kmField = new JTextField(String.valueOf(v.getKilometrage()));
        JComboBox<String> zoneCombo = new JComboBox<>(new String[]{"MONTREAL", "RIVE_NORD", "RIVE_SUD"});
        zoneCombo.setSelectedItem(v.getZone().toString());
        JTextField chauffeurField = new JTextField(v.getChauffeurAssigne() == null ? "" : v.getChauffeurAssigne());
        JTextField tarifField = new JTextField(String.valueOf(v.getTarifBase()));

        JPanel panel = construirePanelFormulaire(
                "ID (non modifiable) :", new JLabel(v.getId()), "Modèle :", modeleField,
                "Énergie :", energieCombo, "Capacité (kg) :", capaciteField,
                "Kilométrage :", kmField, "Zone :", zoneCombo,
                "Chauffeur assigné :", chauffeurField, "Tarif de base ($) :", tarifField);

        int resultat = JOptionPane.showConfirmDialog(this, panel, "Modifier " + v.getId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultat != JOptionPane.OK_OPTION) return;

        try {
            // Réutilise la fabrique juste pour valider les nouvelles valeurs (même type/id/statut conservés).
            Vehicule valide = VehiculeFactory.creerVehicule(
                    v.getId(), modeleField.getText(), v.getCodeCsv(), (String) energieCombo.getSelectedItem(),
                    capaciteField.getText(), kmField.getText(), v.getStatut().toString(),
                    (String) zoneCombo.getSelectedItem(), chauffeurField.getText(), tarifField.getText());

            v.setModele(valide.getModele());
            v.setTypeEnergie(valide.getTypeEnergie());
            v.setCapaciteKg(valide.getCapaciteKg());
            v.setKilometrage(valide.getKilometrage());
            v.setZone(valide.getZone());
            v.setChauffeurAssigne(valide.getChauffeurAssigne());
            v.setTarifBase(valide.getTarifBase());

            rafraichirTable();
            sauvegarderCSV();
            setStatut("Véhicule " + v.getId() + " modifié avec succès.");
        } catch (DonneeInvalideException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------------
    // Retirer
    // ---------------------------------------------------------------

    private void retirerVehicule() {
        Vehicule v = getVehiculeSelectionne();
        if (v == null) return;

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Retirer définitivement le véhicule " + v.getId() + " (" + v.getModele() + ") ?",
                "Confirmer le retrait", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;

        gestionnaire.retirerVehicule(v.getId());
        rafraichirTable();
        sauvegarderCSV();
        setStatut("Véhicule " + v.getId() + " retiré de la flotte.");
    }

    // ---------------------------------------------------------------
    // Louer
    // ---------------------------------------------------------------

    private void louerVehicule() {
        Vehicule v = getVehiculeSelectionne();
        if (v == null) return;

        JTextField chauffeurField = new JTextField();
        JTextField joursField = new JTextField("1");
        JPanel panel = construirePanelFormulaire("Chauffeur :", chauffeurField, "Nombre de jours :", joursField);

        int resultat = JOptionPane.showConfirmDialog(this, panel, "Louer " + v.getId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultat != JOptionPane.OK_OPTION) return;

        try {
            int jours = Integer.parseInt(joursField.getText().trim());
            if (chauffeurField.getText().isBlank()) {
                throw new VehiculeIndisponibleException("Le nom du chauffeur est obligatoire.");
            }
            gestionnaire.louer(v, chauffeurField.getText().trim(), jours);
            rafraichirTable();
            sauvegarderCSV();
            double tarif = v.calculerTarif(jours);
            setStatut(String.format("Véhicule %s loué à %s pour %d jour(s) — tarif : %.2f$",
                    v.getId(), chauffeurField.getText().trim(), jours, tarif));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le nombre de jours doit être un nombre entier.",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        } catch (VehiculeIndisponibleException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Location impossible", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------------
    // Retourner
    // ---------------------------------------------------------------

    private void retournerVehicule() {
        Vehicule v = getVehiculeSelectionne();
        if (v == null) return;

        JTextField kmField = new JTextField("0");
        JPanel panel = construirePanelFormulaire("Kilomètres parcourus durant la location :", kmField);

        int resultat = JOptionPane.showConfirmDialog(this, panel, "Retourner " + v.getId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultat != JOptionPane.OK_OPTION) return;

        try {
            int km = Integer.parseInt(kmField.getText().trim());
            gestionnaire.retourner(v, km);
            rafraichirTable();
            sauvegarderCSV();
            setStatut("Véhicule " + v.getId() + " retourné — statut remis à DISPONIBLE, kilométrage mis à jour.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le kilométrage doit être un nombre entier.",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        } catch (KilometrageInvalideException | VehiculeIndisponibleException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Retour impossible", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------------
    // Entretien
    // ---------------------------------------------------------------

    private void signalerEntretien() {
        Vehicule v = getVehiculeSelectionne();
        if (v == null) return;

        String description = JOptionPane.showInputDialog(this,
                "Description du problème / de l'entretien pour " + v.getId() + " :");
        if (description == null || description.isBlank()) return;

        v.signalerEntretien(description.trim());
        v.setStatut(StatutVehicule.EN_ENTRETIEN);
        rafraichirTable();
        sauvegarderCSV();
        setStatut("Entretien signalé pour " + v.getId() + ".");
    }

    // ---------------------------------------------------------------
    // Statistiques / alertes / rapport
    // ---------------------------------------------------------------

    private void afficherStatistiques() {
        StatistiquesFlotte stats = new StatistiquesFlotte(gestionnaire.getFlotte());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Revenu total généré : %.2f$%n", stats.revenuTotal()));
        sb.append(String.format("Kilométrage moyen : %.1f km%n%n", stats.kilometrageMoyen()));
        sb.append("Taux d'utilisation par type (jours loués / véhicule) :\n");
        for (Map.Entry<String, Double> entry : stats.tauxUtilisationParType().entrySet()) {
            sb.append(String.format("  %s : %.1f jours%n", entry.getKey(), entry.getValue()));
        }
        sb.append("\nRépartition par zone :\n");
        for (Map.Entry<String, Long> entry : stats.repartitionParZone().entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" : ").append(entry.getValue()).append(" véhicule(s)\n");
        }
        sb.append("\nVéhicules nécessitant un entretien :\n");
        List<Vehicule> aEntretenir = stats.vehiculesNecessitantEntretien();
        if (aEntretenir.isEmpty()) {
            sb.append("  Aucun.\n");
        } else {
            aEntretenir.forEach(v -> sb.append("  ").append(v.getId()).append(" (").append(v.getModele()).append(")\n"));
        }

        JTextArea zoneTexte = new JTextArea(sb.toString());
        zoneTexte.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(zoneTexte), "Statistiques de la flotte",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void afficherAlertes() {
        GestionnaireAlertes alertes = new GestionnaireAlertes(gestionnaire.getFlotte());
        StringBuilder sb = new StringBuilder();
        alertes.genererAlertes().forEach(a -> sb.append("• ").append(a).append("\n"));

        JTextArea zoneTexte = new JTextArea(sb.toString());
        zoneTexte.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(zoneTexte), "Alertes actives",
                JOptionPane.WARNING_MESSAGE);
    }

    private void genererRapport() {
        StatistiquesFlotte stats = new StatistiquesFlotte(gestionnaire.getFlotte());
        GestionnaireAlertes alertes = new GestionnaireAlertes(gestionnaire.getFlotte());
        String contenu = RapportGenerateur.genererContenu(gestionnaire.getFlotte(), stats, alertes);
        try {
            lecteurCSV.ecrireRapport("data/rapport_flotte.txt", contenu);
            setStatut("Rapport généré : data/rapport_flotte.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la génération du rapport : " + e.getMessage(),
                    "Erreur d'écriture", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------------
    // Utilitaire de construction de formulaire
    // ---------------------------------------------------------------

    private JPanel construirePanelFormulaire(Object... labelsEtChamps) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        for (int i = 0; i < labelsEtChamps.length; i += 2) {
            panel.add(new JLabel((String) labelsEtChamps[i]));
            panel.add((Component) labelsEtChamps[i + 1]);
        }
        return panel;
    }

    // ---------------------------------------------------------------
    // Point d'entrée
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        LecteurCSV lecteurCSV = new LecteurCSV();
        List<String> erreurs = new ArrayList<>();
        List<Vehicule> vehicules;
        try {
            vehicules = lecteurCSV.chargerVehicules("data/vehicules.csv", erreurs);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors du chargement du CSV : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            DrivioGUI gui = new DrivioGUI(vehicules);
            gui.setVisible(true);
            if (!erreurs.isEmpty()) {
                StringBuilder sb = new StringBuilder("Certaines lignes du CSV ont été ignorées :\n\n");
                erreurs.forEach(err -> sb.append("• ").append(err).append("\n"));
                JOptionPane.showMessageDialog(gui, sb.toString(), "Avertissement au chargement",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
