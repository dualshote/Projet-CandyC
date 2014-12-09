/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;
import Model.Case;
import Model.Chrono;
import Model.Grille;
import Model.Score;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dualshote
 */
public class MainView extends JFrame {
    private ScoreLab scoreLab = new ScoreLab();
    private ChronoLab chronoLab = new ChronoLab();
    private final int minutes, secondes;
    private final String POLICE = "Arial";
    private JPanel jpGrille;
    
    /* Objet pour la serialisation */
    private Score scoreSeria;
    private Chrono chronoSeria;
    private Grille grilleSeria;
    
    private int width;
    private int height;
    
    public MainView(final int width, final int height, final int minutes, final int secondes){
        
        this.minutes = minutes;
        this.secondes = secondes;
        this.width = width;
        this.height = height;
        
        /*  --------- Paramètres --------- */
        this.setTitle("CandyCrush Party");
        this.setSize(665,560);
        this.setLocationRelativeTo(null);
        //this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        /*  --------- Menu --------- */
        JMenuBar menu = new JMenuBar();
        JMenu partie = new JMenu("Partie");
        JMenuItem nouvellePartie = new JMenuItem("Nouvelle partie");
        JMenuItem sauegarderPartie = new JMenuItem("Sauvegarder une partie");
        JMenuItem chargerPartie = new JMenuItem("Charger une partie");
        partie.add(nouvellePartie);
        partie.add(sauegarderPartie);
        partie.add(chargerPartie);
        menu.add(partie);
        
        /*  --------- Fenetre de droite --------- */
        JPanel jpDroite = new JPanel();
        //jpDroite.setBackground(Color.yellow);
        jpDroite.setPreferredSize(new Dimension(130,498));
        
        /*  --------- Label sur la fenêtre de droite --------- */
        scoreLab.setFont(new Font(POLICE, 0, 18));
        jpDroite.add(scoreLab);
        chronoLab.setFont(new Font(POLICE, 0, 18));
        jpDroite.add(chronoLab);
        
        /*  --------- Grille, fenêtre de gauche --------- */
        jpGrille = new JPanel(new GridLayout(height,width));
        jpGrille.setPreferredSize(new Dimension(500,500));
        grilleSeria = new Grille(height, width);
        initialisation(width, height, grilleSeria, jpGrille, new GestionDeLaGrille(grilleSeria,this.minutes, this.secondes), 0);
        
        
        /*  --------- Gestion affichage --------- */
        GroupLayout groupeLayout = new GroupLayout(this.getContentPane());
        this.setLayout(groupeLayout);
        groupeLayout.setHorizontalGroup(
                groupeLayout.createSequentialGroup()
                            .addComponent(jpGrille, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jpDroite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        groupeLayout.setVerticalGroup(
                groupeLayout.createSequentialGroup()
                        .addGroup(groupeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jpGrille, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jpDroite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        
        
        /*  --------- Action sur le menu --------- */
        nouvellePartie.addActionListener(new ActionListener() {
            boolean reset = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                initialisation(width, height, grilleSeria, jpGrille, new GestionDeLaGrille(grilleSeria, minutes, secondes), 1);
                System.out.println("Partie renitialisée");
            }
        });
        
        sauegarderPartie.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sauvegargerSeria();
                } catch (IOException ex) {
                    Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        chargerPartie.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try { 
                    chargementSeria();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
        /*  Paramètre pour rendre visible la fenêtre    */
        this.setJMenuBar(menu);
        this.setVisible(true);
    }
    
    
    /*  --------- Fonction pour initialiser la grille de jeu --------- */
    public void initialisation(int width, int height,Grille grille, JPanel jpGrille, MouseListener monMouseListener, int reset){
        
        boolean init = true;
        
        if(reset == 0){ //Si la partie n'est pas renitialisée
            for(int j = 0; j < height; j++){
                for(int i =0; i < width; i++){
                    Case maCase = new Case(i,j,grille);
                    grille.setCase(maCase);
                    CaseGrille maCaseGrille = new CaseGrille();
                    maCaseGrille.initialisation(maCase.getX(), maCase.getY(), maCase.getForme());
                    maCase.addObserver(maCaseGrille);
                    maCaseGrille.setBorder(BorderFactory.createLineBorder(Color.black, 2));
                    maCaseGrille.addMouseListener(monMouseListener);
                    jpGrille.add(maCaseGrille);
                }
            }
            
            for(int j = 0; j < height; j++){
                for(int i =0; i < width; i++){
                    grille.getCase(i, j).aggregation(init);
                }
            }
            Score score = new Score();
            score.addObserver(this.scoreLab);
            score.setPoints(0);
            GestionAgregation.setScore(score);
            
            Chrono chrono = new Chrono(minutes, secondes);
            chrono.addObserver(this.chronoLab);
            chrono.setChrono(minutes, secondes);
            GestionChrono.setChrono(chrono);
            
            
            /*  Definition objet pour la serialisation  */
            this.grilleSeria = grille;
            this.scoreSeria = score;
            this.chronoSeria = chrono;
            
        }
        else if(reset == 1){   //Si la partie est renitialisée
            for(int j = 0; j < height; j++){
                for(int i =0; i < width; i++){
                    grille.getCase(i, j).regenerer();
                    grille.getCase(i, j).aggregation(init);
                }
            }
            Score score = new Score();
            score.addObserver(this.scoreLab);
            score.setPoints(0);
            GestionAgregation.setScore(score);
            
            Chrono chrono = new Chrono(minutes, secondes);
            chrono.addObserver(this.chronoLab);
            chrono.setChrono(minutes, secondes);
            GestionChrono.setChrono(chrono);
            GestionChrono.setDebutChrono(1); //On permet au chronomètre de se lancer à nouveau quand on  redémarre une nouvelle partie
        
            /*  Definition objet pour la serialisation  */
            this.grilleSeria = grille;
            this.scoreSeria = score;
            this.chronoSeria = chrono;
        }
        else if(reset == -1){
            System.out.println("aeeahe");
            for(int j = 0; j < height; j++){
                for(int i =0; i < width; i++){
                    grille.getCase(i, j).regenerer();
                }
            }
            
            System.out.println("grille apres chargement : " + grille);
        }
    }
    
    public void chargementSeria() throws FileNotFoundException{
        ObjectInputStream oisGrille = null;
        Grille grille = null;
        
        try{
            final FileInputStream fichierGrille = new FileInputStream("grille.serial");
            oisGrille = new ObjectInputStream(fichierGrille);
            grille = (Grille) oisGrille.readObject();
                     
            this.grilleSeria = grille;            
            System.out.println("Grille chargée  : " + this.grilleSeria);
        }
        catch(final java.io.IOException e){
             e.printStackTrace();
        }
        catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
              if (oisGrille != null) {
                oisGrille.close();
              }
            }
            catch (final IOException ex) {
              ex.printStackTrace();
            }
        }
        
        System.out.println("Partie chargée ! ");
        if(grille != null){
            initialisation(this.width, this.height, this.grilleSeria, this.jpGrille, new GestionDeLaGrille(this.grilleSeria, minutes, secondes), -1);
        }
    }
    
    public void sauvegargerSeria() throws FileNotFoundException, IOException{
        Grille grille1 = this.grilleSeria;
        ObjectOutputStream oosGrille = null;
        System.out.println("Grille avant seria  : " + grille1);
        try {
            final FileOutputStream fosGrille = new FileOutputStream("grille.serial");
            oosGrille = new ObjectOutputStream(fosGrille);
            // Ecriture dans le flux de sortie
            oosGrille.writeObject(grille1);

            // Vide le tampon
            oosGrille.flush();

        } 
        catch (final java.io.IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(oosGrille != null){
                    oosGrille.close();
                    oosGrille.close();
                }
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Partie sauvegardée ! ");
    }
}
