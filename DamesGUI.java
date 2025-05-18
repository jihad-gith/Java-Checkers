import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DamesGUI extends JFrame {
    private Jeu jeu;
    private PlateauPanel plateauPanel;
    private JLabel statusLabel;
    private JTextArea historiqueTextArea;
    private JLabel scoreLabel;
    private Position positionSelectionnee;
    private JButton btnRefaire;
    // private JButton btnAnnuler;
    private JButton btnRegles;

    // Nouvelles couleurs pour un meilleur contraste
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color PANEL_COLOR = new Color(250, 250, 250);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);  // Bleu acier
    private static final Color TEXT_COLOR = new Color(50, 50, 50);      // Gris foncé
    
    public DamesGUI() {
        jeu = new Jeu();

        setTitle("Jeu de Dames");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Panneau de score (à gauche) - Modifié pour un meilleur positionnement
        JPanel scorePanel = createScorePanel();
        add(scorePanel, BorderLayout.WEST);

        // Plateau au centre
        plateauPanel = new PlateauPanel();
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(plateauPanel);

        // Historique à droite
        JPanel historiquePanel = createHistoriquePanel();
        add(historiquePanel, BorderLayout.EAST);

        // Panneau d'informations (en bas)
        JPanel infoPanel = createInfoPanel();
        add(centerPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        setSize(900, 650);
        setLocationRelativeTo(null);
        setVisible(true);

        mettreAJourInterface();
    }
    
    private JPanel createScorePanel() {
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBackground(PANEL_COLOR);
        scorePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(20, 15, 10, 15)
        ));

        // Titre du panneau de score
        JLabel scoreTitleLabel = new JLabel("SCORE", SwingConstants.CENTER);
        scoreTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreTitleLabel.setForeground(ACCENT_COLOR);
        scorePanel.add(scoreTitleLabel, BorderLayout.NORTH);
        
        // Score au centre
        scoreLabel = new JLabel("Blancs: 12 | Noirs: 12", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setForeground(TEXT_COLOR);
        JPanel scoreWrapper = new JPanel();
        scoreWrapper.setBackground(PANEL_COLOR);
        scoreWrapper.add(scoreLabel);
        scorePanel.add(scoreWrapper, BorderLayout.CENTER);
        
        // Bouton règles en bas du panneau score
        btnRegles = new JButton("Règles du jeu");
        btnRegles.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRegles.setBackground(PANEL_COLOR);
        btnRegles.setForeground(ACCENT_COLOR);
        btnRegles.setFocusPainted(false);
        btnRegles.setBorderPainted(false);
        btnRegles.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegles.addActionListener(e -> afficherRegles());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(btnRegles);
        scorePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return scorePanel;
    }
    
    private JPanel createHistoriquePanel() {
        historiqueTextArea = new JTextArea(20, 20);
        historiqueTextArea.setEditable(false);
        historiqueTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        historiqueTextArea.setBackground(PANEL_COLOR);
        historiqueTextArea.setForeground(TEXT_COLOR);
        JScrollPane historiquePane = new JScrollPane(historiqueTextArea);
        historiquePane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel historiquePanel = new JPanel(new BorderLayout());
        JLabel historiqueTitle = new JLabel("HISTORIQUE DES COUPS", SwingConstants.CENTER);
        historiqueTitle.setFont(new Font("Arial", Font.BOLD, 14));
        historiqueTitle.setForeground(ACCENT_COLOR);
        historiqueTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        historiquePanel.add(historiqueTitle, BorderLayout.NORTH);
        historiquePanel.add(historiquePane, BorderLayout.CENTER);
        
        return historiquePanel;
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(PANEL_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        statusLabel = new JLabel("Tour des Blancs", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(ACCENT_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(statusLabel);

        // Panneau pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(PANEL_COLOR);
        
        btnRefaire = createStyledButton("Nouvelle partie");
        btnRefaire.setVisible(false);
        btnRefaire.addActionListener(e -> nouvellePartie());
        
        // btnAnnuler = createStyledButton("Annuler coup");
        // btnAnnuler.setEnabled(false);  // Désactivé par défaut
        // btnAnnuler.addActionListener(e -> annulerDernierCoup());
        
        // buttonPanel.add(btnAnnuler);
        buttonPanel.add(btnRefaire);
        
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createVerticalStrut(5));
        
        return infoPanel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void afficherRegles() {
        JDialog reglesDialog = new JDialog(this, "Règles du Jeu de Dames", true);
        reglesDialog.setLayout(new BorderLayout());
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setText(
            "RÈGLES DU JEU DE DAMES\n\n" +
            "Le jeu de dames se joue sur un plateau de 8x8 cases.\n\n" +
            "DÉPLACEMENT:\n" +
            "- Les pions se déplacent en diagonale d'une case vers l'avant.\n" +
            "- Les dames peuvent se déplacer en diagonale d'une case dans n'importe quelle direction.\n\n" +
            "PRISE:\n" +
            "- Pour prendre une pièce adverse, il faut sauter par-dessus en diagonale et atterrir sur une case vide.\n" +
            "- Si après une prise, une autre prise est possible avec la même pièce, le joueur doit continuer (prise en chaîne).\n\n" +
            "PROMOTION:\n" +
            "- Un pion qui atteint la dernière rangée adverse est promu en dame.\n\n" +
            "FIN DE PARTIE:\n" +
            "- Le joueur qui capture toutes les pièces adverses ou qui bloque l'adversaire gagne la partie."
        );
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        reglesDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton okButton = new JButton("Fermer");
        okButton.addActionListener(e -> reglesDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        reglesDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        reglesDialog.setSize(400, 400);
        reglesDialog.setLocationRelativeTo(this);
        reglesDialog.setVisible(true);
    }

    private void nouvellePartie() {
        jeu = new Jeu();
        positionSelectionnee = null;
        historiqueTextArea.setText("");
        btnRefaire.setVisible(false);
        // btnAnnuler.setEnabled(false);
        mettreAJourInterface();
    }
    
    // private void annulerDernierCoup() {
    //     // Placeholder pour la fonctionnalité d'annulation
    //     // À implémenter selon la structure de votre classe Jeu
    //     JOptionPane.showMessageDialog(this, 
    //         "Fonctionnalité à implémenter", 
    //         "Annuler coup", 
    //         JOptionPane.INFORMATION_MESSAGE);
    // }

    private void mettreAJourInterface() {
        int[] compteur = jeu.getPlateau().compterPieces();
        scoreLabel.setText("Blancs: " + compteur[0] + " | Noirs: " + compteur[1]);

        if (jeu.estPartieTerminee()) {
            if (compteur[0] == 0) {
                statusLabel.setText("Les Noirs ont gagné !");
            } else if (compteur[1] == 0) {
                statusLabel.setText("Les Blancs ont gagné !");
            } else if (jeu.getPlateau().getMouvementsPossibles(true).isEmpty()) {
                statusLabel.setText("Les Noirs ont gagné ! (Blancs bloqués)");
            } else {
                statusLabel.setText("Les Blancs ont gagné ! (Noirs bloqués)");
            }
            btnRefaire.setVisible(true);
        } else if (jeu.estPrisesEnChaineEnCours()) {
            statusLabel.setText((jeu.estTourBlanc() ? "Blancs" : "Noirs") + " : continuez la prise en chaîne");
            btnRefaire.setVisible(false);
        } else {
            statusLabel.setText("Tour des " + (jeu.estTourBlanc() ? "Blancs" : "Noirs"));
            btnRefaire.setVisible(false);
        }

        plateauPanel.repaint();
    }

    private class PlateauPanel extends JPanel {
        private static final int TAILLE_CASE = 50;
        
        // Nouvelles couleurs pour le plateau
        private static final Color CASE_CLAIRE = new Color(240, 217, 181);  // Beige clair
        private static final Color CASE_FONCEE = new Color(181, 136, 99);   // Marron foncé
        private static final Color PIECE_BLANCHE = new Color(255, 255, 240); // Blanc cassé
        private static final Color PIECE_NOIRE = new Color(40, 40, 40);     // Noir profond
        private static final Color SELECTION = new Color(255, 255, 0, 150); // Jaune semi-transparent
        private static final Color COUP_POSSIBLE = new Color(50, 205, 50, 150); // Vert semi-transparent
        
        // Images pour le plateau et les pièces
        private BufferedImage imageComplete;
        private BufferedImage caseBlanc;
        private BufferedImage caseNoir;
        private BufferedImage pionBlanc;
        private BufferedImage pionNoir;
        private BufferedImage dameBlanc;
        private BufferedImage dameNoir;
        private boolean useImages = false;

        public PlateauPanel() {
            setPreferredSize(new Dimension(8 * TAILLE_CASE, 8 * TAILLE_CASE));
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

            // Charger l'image
            chargerImages();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    gererClicSouris(e.getX(), e.getY());
                }
            });
        }

        private void chargerImages() {
            try {
                File fichierImage = new File("Dames_image.png");
                if (fichierImage.exists()) {
                    // Charger l'image complète
                    imageComplete = ImageIO.read(fichierImage);
                    
                    // Vérifier que l'image est assez grande pour contenir tous les sprites
                    if (imageComplete.getWidth() < 100 || imageComplete.getHeight() < 150) {
                        throw new IOException("Dimensions de l'image inadéquates");
                    }
                    
                    // Extraire les sprites individuels
                    decouperImages();
                    useImages = true;
                } else {
                    // Mode silencieux - pas de message d'erreur
                    useImages = false;
                }
            } catch (Exception e) {
                // Mode silencieux - pas de stack trace
                useImages = false;
            }
        }
        
        private void decouperImages() {
            try {
                // Pour rendre la découpe plus robuste, prendre la moitié de la largeur et le tiers de la hauteur
                int largeurSprite = imageComplete.getWidth() / 2;
                int hauteurSprite = imageComplete.getHeight() / 3;
                
                // Extraire chaque sprite
                caseBlanc = imageComplete.getSubimage(0, 0, largeurSprite, hauteurSprite);
                caseNoir = imageComplete.getSubimage(largeurSprite, 0, largeurSprite, hauteurSprite);
                pionBlanc = imageComplete.getSubimage(0, hauteurSprite, largeurSprite, hauteurSprite);
                pionNoir = imageComplete.getSubimage(largeurSprite, hauteurSprite, largeurSprite, hauteurSprite);
                dameBlanc = imageComplete.getSubimage(0, 2 * hauteurSprite, largeurSprite, hauteurSprite);
                dameNoir = imageComplete.getSubimage(largeurSprite, 2 * hauteurSprite, largeurSprite, hauteurSprite);
            } catch (Exception e) {
                useImages = false;
            }
        }

        private void gererClicSouris(int x, int y) {
            int colonne = x / TAILLE_CASE;
            int ligne = y / TAILLE_CASE;

            if (ligne >= 0 && ligne < 8 && colonne >= 0 && colonne < 8) {
                Position pos = new Position(ligne, colonne);
                Piece piece = jeu.getPlateau().getPiece(pos);

                if (jeu.estPartieTerminee()) return;

                if (positionSelectionnee == null) {
                    if (piece != null && !piece.estVide() &&
                            ((jeu.estTourBlanc() && piece.estBlanc()) || (!jeu.estTourBlanc() && piece.estNoir()))) {
                        if (jeu.estPrisesEnChaineEnCours()) {
                            if (pos.equals(jeu.getPositionDernierePrise())) {
                                positionSelectionnee = pos;
                                repaint();
                            }
                        } else {
                            positionSelectionnee = pos;
                            repaint();
                        }
                    }
                } else {
                    if (!positionSelectionnee.equals(pos)) {
                        Mouvement m = new Mouvement(positionSelectionnee, pos);
                        if (jeu.estMouvementAutorise(m)) {
                            jeu.jouerCoup(m);
                            ajouterHistorique(m);
                            positionSelectionnee = null;
                            mettreAJourInterface();
                            // Activer le bouton d'annulation après un coup
                            // btnAnnuler.setEnabled(true);
                        }
                    } else {
                        positionSelectionnee = null;
                        repaint();
                    }
                }
            }
        }

        private void ajouterHistorique(Mouvement m) {
            String coup = (jeu.estTourBlanc() ? "Noirs" : "Blancs") + " : " +
                    m.getDebut().toString() + " -> " + m.getFin().toString();
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            historiqueTextArea.append("[" + time + "] " + coup + "\n");
            // Auto-scroll vers le bas
            historiqueTextArea.setCaretPosition(historiqueTextArea.getDocument().getLength());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    int x = j * TAILLE_CASE;
                    int y = i * TAILLE_CASE;
                    
                    if (useImages) {
                        // Utiliser l'image appropriée pour chaque case
                        g2.drawImage(
                            (i + j) % 2 == 0 ? caseBlanc : caseNoir,
                            x, y, TAILLE_CASE, TAILLE_CASE, null
                        );
                    } else {
                        // Utiliser les nouvelles couleurs pour un meilleur contraste
                        g2.setColor((i + j) % 2 == 0 ? CASE_CLAIRE : CASE_FONCEE);
                        g2.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);
                    }

                    // Dessiner les pièces
                    Piece piece = jeu.getPlateau().getPiece(new Position(i, j));
                    if (piece != null && !piece.estVide()) {
                        if (useImages) {
                            // Sélectionner la bonne image selon le type de pièce
                            BufferedImage pieceImage;
                            if (piece.estDame()) {
                                pieceImage = piece.estBlanc() ? dameBlanc : dameNoir;
                            } else {
                                pieceImage = piece.estBlanc() ? pionBlanc : pionNoir;
                            }
                            g2.drawImage(pieceImage, x, y, TAILLE_CASE, TAILLE_CASE, null);
                        } else {
                            // Utiliser les nouvelles couleurs pour un meilleur contraste
                            g2.setColor(piece.estBlanc() ? PIECE_BLANCHE : PIECE_NOIRE);
                            int d = (int)(TAILLE_CASE * 0.8);
                            int px = x + (TAILLE_CASE - d) / 2;
                            int py = y + (TAILLE_CASE - d) / 2;
                            g2.fillOval(px, py, d, d);
                            
                            // Ajouter un contour pour améliorer la visibilité
                            g2.setColor(piece.estBlanc() ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                            g2.setStroke(new BasicStroke(2));
                            g2.drawOval(px, py, d, d);

                            if (piece.estDame()) {
                                g2.setColor(piece.estBlanc() ? Color.BLACK : Color.WHITE);
                                g2.setFont(new Font("Arial", Font.BOLD, 20));
                                g2.drawString("D", px + d / 2 - 6, py + d / 2 + 6);
                            }
                        }
                    }
                }
            }

            // Affichage des sélections et mouvements possibles avec de nouvelles couleurs
            if (positionSelectionnee != null) {
                int x = positionSelectionnee.getColonne() * TAILLE_CASE;
                int y = positionSelectionnee.getLigne() * TAILLE_CASE;

                g2.setColor(SELECTION);
                g2.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);

                List<Mouvement> mouvements;
                if (jeu.estPrisesEnChaineEnCours()) {
                    mouvements = jeu.getPlateau().getPrisesEnChaine(positionSelectionnee, jeu.estTourBlanc());
                } else {
                    mouvements = jeu.getPlateau().getMouvementsPossibles(jeu.estTourBlanc());
                    mouvements.removeIf(m -> !m.getDebut().equals(positionSelectionnee));
                }

                for (Mouvement m : mouvements) {
                    Position fin = m.getFin();
                    int xf = fin.getColonne() * TAILLE_CASE;
                    int yf = fin.getLigne() * TAILLE_CASE;
                    g2.setColor(COUP_POSSIBLE);
                    g2.fillRect(xf, yf, TAILLE_CASE, TAILLE_CASE);
                }
            }
            
            // Ajout des coordonnées du plateau
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            for (int i = 0; i < 8; i++) {
                g2.setColor(i % 2 == 0 ? CASE_FONCEE : CASE_CLAIRE);
                g2.drawString(String.valueOf((char)('A' + i)), i * TAILLE_CASE + 5, 8 * TAILLE_CASE - 5);
                g2.drawString(String.valueOf(8 - i), 5, i * TAILLE_CASE + 15);
            }
        }
    }

    public static void main(String[] args) {
        // Définir un look and feel moderne
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorer les erreurs de Look & Feel
        }
        
        SwingUtilities.invokeLater(DamesGUI::new);
    }
}