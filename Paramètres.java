package Reseau;
import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class Paramètres extends JFrame {
	// Jlabel  = ajouter du texte
	JLabel titre_jeu = new JLabel("Bataille Navale");
	JLabel saisie_jeu = new JLabel("Saisir les coordonnées : ");
	JLabel saisie_reseau = new JLabel("Saisir la connexion : ");
	JButton bouton_valider = new JButton("Valider");
	JButton bouton_valider_coordonnées = new JButton("Générer coordonnées");
	JTextField fl = new JTextField();
	JTextField fl_y = new JTextField();
	BorderLayout bord = new BorderLayout();
	JRadioButton client = new JRadioButton("Client");
	JRadioButton serveur = new JRadioButton("Serveur");
	ButtonGroup group = new ButtonGroup();
	// Ajouter un paneau
	
	JPanel panneau = new JPanel();
	JPanel panneau_centre = new JPanel();
	
	GridLayout grille_du_sud = new GridLayout(2,5);
	ArrayList<ArrayList<Integer>> liste_saisie = new ArrayList<ArrayList<Integer>>();
	
	public Paramètres() {
		this.setTitle("Paramètres Jeu");
		this.setSize(new Dimension(700,150));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		this.setContentPane(panneau);
		this.group.add(client);
		this.group.add(serveur);
		this.panneau.setBackground(Color.GRAY);
		panneau.setLayout(bord);
		panneau.add(titre_jeu, BorderLayout.NORTH);
		panneau.add(panneau_centre);
		panneau_centre.setLayout(grille_du_sud);
		panneau_centre.add(saisie_jeu);
		panneau_centre.add(fl);
		panneau_centre.add(fl_y);
		panneau_centre.add(bouton_valider_coordonnées);
		panneau.add(bouton_valider, BorderLayout.SOUTH);
		panneau_centre.add(saisie_reseau);
		panneau_centre.add(client);
		panneau_centre.add(serveur);
		titre_jeu.setHorizontalAlignment(SwingConstants.CENTER);
		this.bouton_valider_coordonnées.addActionListener(e-> genererListe());
		this.bouton_valider.addActionListener(e-> {
            try {
                creerlaGrille();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
		
		
	}
	
	public void creerlaGrille() throws IOException {
		if(!this.liste_saisie.isEmpty()) {
		this.dispose();
		Jeu awt = new Jeu(liste_saisie, getReseau());
		}else {
			JOptionPane.showMessageDialog(null, "Aucune coordonnées de saisie ! ", "Vide", JOptionPane.INFORMATION_MESSAGE);
		} 
	}
	
	
	public ArrayList<ArrayList<Integer>> genererListe() {
	    try {
	        int x = Integer.parseInt(fl.getText());
	        int y = Integer.parseInt(fl_y.getText());

	        // Vérification des limites des coordonnées
	        if (x < 0 || x > 9 || y < 0 || y > 9) {
	            JOptionPane.showMessageDialog(null, "Les coordonnées doivent être comprises entre 0 et 9. Veuillez resaisir.", "Erreur", JOptionPane.ERROR_MESSAGE);
	            return this.liste_saisie;
	        }

	        // Vérification de la limite de 5 coordonnées maximum
	        if (this.liste_saisie.size() >= 5) {
	            JOptionPane.showMessageDialog(null, "Vous ne pouvez pas ajouter plus de 5 coordonnées.", "Limite atteinte", JOptionPane.WARNING_MESSAGE);
	            return this.liste_saisie;
	        }

	        // Création de la sous-liste pour la vérification
	        ArrayList<Integer> sous_liste = new ArrayList<>();
	        sous_liste.add(x);
	        sous_liste.add(y);

	        // Vérification des doublons
	        if (this.liste_saisie.contains(sous_liste)) {
	            JOptionPane.showMessageDialog(null, "Ces coordonnées ont déjà été saisies.", "Doublon", JOptionPane.WARNING_MESSAGE);
	        } else {
	            this.liste_saisie.add(sous_liste);
	            System.out.println("Coordonnées ajoutées : " + sous_liste);
	        }
	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(null, "Veuillez saisir des nombres valides pour les coordonnées.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
	    }

	    System.out.println("Liste des coordonnées actuelles : " + this.liste_saisie);
	    return this.liste_saisie;
	}


	public Reseau getReseau(){
		if(client.isSelected()) {
			return Reseau.Client;
		}else{
			return Reseau.Serveur;
		}
	}
	

}
