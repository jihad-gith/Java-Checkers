import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

public class DamesGUI extends JFrame {
    private Jeu jeu;
    private PlateauPanel plateauPanel;
    private JLabel statusLabel;
    private JTextArea historiqueTextArea;
    private JLabel scoreLabel;
    private Position positionSelectionnee;
    private JButton btnRefaire;
    private JButton btnRegles;
    private JButton btnRetourAccueil;

    // Couleurs Master Checkers (identiques à AccueilGUI)
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color DARK_WOOD = new Color(60, 30, 10);
    private static final Color LIGHT_GOLD = new Color(255, 223, 128);
    private static final Color DARK_GOLD = new Color(190, 150, 20);
    
    // Image de fond
    private BackgroundPanel mainPanel;
    
    public DamesGUI() {
        jeu = new Jeu();
        initUI();
        mettreAJourInterface();
    }
    
    private void initUI() {
        setTitle("Master Checkers - Jeu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);
        setResizable(true);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panneau principal avec fond bois (même style qu'AccueilGUI)
        String bgUrl = "https://i.pinimg.com/736x/61/38/22/6138225ae47e1549bcb77b075efe0f63.jpg";
        mainPanel = new BackgroundPanel(bgUrl, DARK_WOOD);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header avec titre et boutons
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panneau central avec le plateau
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panneau inférieur avec status et boutons
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }
    // Création d'un petit bouton avec icône depuis fichier local
private JButton  createImageIconButton(String iconPath, String tooltip) {
    JButton button = new JButton();
    button.setPreferredSize(new Dimension(50, 50));
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setToolTipText(tooltip);
    
    try {
        File imageFile = new File(iconPath);
        if (imageFile.exists()) {
            BufferedImage originalImage = ImageIO.read(imageFile);
            Image scaledImage = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            button.setIcon(icon);
            
            // Couleur de fond basée sur les tons de l'image
            // Utilisation de couleurs neutres qui s'harmonisent mieux
            button.setBackground(new Color(101, 67, 33));// Beige très clair
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            
            // Effets de survol harmonieux
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                  button.setBackground(new Color(160, 82, 45)); // Plus clair au survol
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(139, 69, 19));// Retour à la normale
                }
            });
            
        } else {
            // Fallback si l'image n'existe pas
            button.setText("🏠");
            button.setBackground(DARK_GOLD);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Fichier image non trouvé : " + iconPath);
        }
    } catch (IOException e) {
        button.setText("🏠");
        button.setBackground(DARK_GOLD);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
    }
    
    return button;
}
  
private JPanel createHeaderPanel() {
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    
    // Titre Master Checkers (plus petit que sur la page d'accueil)
    JLabel titleLabel = createGoldLabel("MASTER CHECKERS", 32);
    headerPanel.add(titleLabel, BorderLayout.CENTER);
    
    // Bouton retour dans le coin gauche avec image locale
    btnRetourAccueil =  createImageIconButton("Java-Checkers\\image.png", "Accueil");
    btnRetourAccueil.addActionListener(e -> retourAccueil());
    headerPanel.add(btnRetourAccueil, BorderLayout.WEST);
    
    // Score dans le coin droit
    scoreLabel = createGoldLabel("Blancs: 12 | Noirs: 12", 16);
    headerPanel.add(scoreLabel, BorderLayout.EAST);
    
    return headerPanel;
}
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Panneau avec le plateau centré
        JPanel plateauContainer = new JPanel(new GridBagLayout());
        plateauContainer.setOpaque(false);
        
        plateauPanel = new PlateauPanel();
        // Ajouter une bordure dorée avec effet 3D
        plateauPanel.setBorder(new GoldBorder());
        plateauContainer.add(plateauPanel);
        
        centerPanel.add(plateauContainer, BorderLayout.CENTER);
        
        // Panneau historique à droite
        JPanel historiquePanel = createHistoriquePanel();
        centerPanel.add(historiquePanel, BorderLayout.EAST);
        
        return centerPanel;
    }
    
    private JPanel createHistoriquePanel() {
    JPanel historiqueContainer = new JPanel(new BorderLayout());
    historiqueContainer.setOpaque(false);
    historiqueContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    historiqueContainer.setPreferredSize(new Dimension(250, 0));
    
    // Titre avec style doré
    JLabel historiqueTitle = createGoldLabel("HISTORIQUE", 18);
    historiqueTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    historiqueContainer.add(historiqueTitle, BorderLayout.NORTH);
    
    // Zone de texte avec fond simple et propre
    historiqueTextArea = new JTextArea(15, 20);
    historiqueTextArea.setEditable(false);
    historiqueTextArea.setLineWrap(true);
    historiqueTextArea.setWrapStyleWord(true);
    historiqueTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    historiqueTextArea.setBackground(new Color(245, 245, 220)); // Beige clair
    historiqueTextArea.setForeground(new Color(101, 67, 33)); // Marron foncé
    historiqueTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    JScrollPane scrollPane = new JScrollPane(historiqueTextArea);
    scrollPane.setBorder(new GoldBorder());
    scrollPane.setBackground(new Color(245, 245, 220));
    scrollPane.getViewport().setBackground(new Color(245, 245, 220));
    scrollPane.getViewport().setOpaque(true);
    
    historiqueContainer.add(scrollPane, BorderLayout.CENTER);
    
    return historiqueContainer;
}

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Status avec style doré
        statusLabel = createGoldLabel("Tour des Blancs", 24);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(statusLabel);
        
        bottomPanel.add(Box.createVerticalStrut(15));
        
        // Panneau pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        btnRegles = createGoldButton("RÈGLES");
        btnRegles.setPreferredSize(new Dimension(120, 50));
        btnRegles.addActionListener(e -> afficherRegles());
        
        btnRefaire = createGoldButton("NOUVELLE PARTIE");
        
        btnRefaire.setPreferredSize(new Dimension(180, 50));
        btnRefaire.setVisible(false);
        btnRefaire.addActionListener(e -> nouvellePartie());
        
        buttonPanel.add(btnRegles);
        buttonPanel.add(btnRefaire);
        
        bottomPanel.add(buttonPanel);
        
        return bottomPanel;
    }
    
    // Classe pour gérer le fond avec image (identique à AccueilGUI)
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private Color backgroundColor;
        
        public BackgroundPanel(String imageUrl, Color fallbackColor) {
            try {
                ImageIcon icon = new ImageIcon(new URL(imageUrl));
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    backgroundImage = icon.getImage();
                } else {
                    throw new IOException("Chargement d'image incomplet");
                }
            } catch (Exception e) {
                try {
                    InputStream is = getClass().getResourceAsStream(imageUrl);
                    if (is != null) {
                        backgroundImage = ImageIO.read(is);
                    }
                } catch (Exception ex) {
                    backgroundImage = null;
                }
            }
            this.backgroundColor = fallbackColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    
    // Bordure dorée avec effet 3D (identique à AccueilGUI)
    private class GoldBorder implements Border {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Ombre
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.setStroke(new BasicStroke(6));
            g2d.drawRect(x + 3, y + 3, width - 6, height - 6);
            
            // Bordure dorée
            g2d.setColor(DARK_GOLD);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(x, y, width - 1, height - 1);
            
            // Reflet
            g2d.setColor(LIGHT_GOLD);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x + 2, y + 2, width - 5, height - 5);
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 8, 8, 8);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
    
    // Création d'un label avec effet doré (identique à AccueilGUI)
    private JLabel createGoldLabel(String text, int fontSize) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, LIGHT_GOLD,
                    0, getHeight(), DARK_GOLD
                );
                g2d.setPaint(gp);
                
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - textHeight) / 2) + fm.getAscent();
                
                g2d.drawString(getText(), x, y);
                
                float alpha = 0.4f;
                for (int i = 1; i < 4; i++) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.drawString(getText(), x - i, y - i);
                    alpha -= 0.1f;
                }
                
                g2d.dispose();
            }
        };
        
        label.setForeground(GOLD_COLOR);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        return label;
    }
    
    // Création d'un bouton doré (identique à AccueilGUI)
    private JButton createGoldButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, LIGHT_GOLD,
                    0, getHeight(), DARK_GOLD
                );
                g2d.setPaint(gp);
                g2d.fill(roundedRectangle);
                
                g2d.setColor(DARK_GOLD.darker());
                g2d.draw(roundedRectangle);
                
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - textHeight) / 2) + fm.getAscent();
                
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.drawString(getText(), x + 1, y + 1);
                
                g2d.setColor(Color.BLACK);
                g2d.drawString(getText(), x, y);
                
                if (!getModel().isPressed()) {
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.drawLine(5, 5, getWidth() - 5, 5);
                    g2d.drawLine(5, 5, 5, getHeight() - 5);
                }
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setBackground(GOLD_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    // Création d'un petit bouton (identique à AccueilGUI)
    private JButton createSmallIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(50, 50));
        button.setBackground(DARK_GOLD);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(LIGHT_GOLD);
                button.setForeground(Color.BLACK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DARK_GOLD);
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private void retourAccueil() {
        try {
            Class.forName("AccueilGUI");
            JFrame accueilGUI = (JFrame) Class.forName("AccueilGUI").getDeclaredConstructor().newInstance();
            accueilGUI.setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Impossible de retourner à l'accueil: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void afficherRegles() {
        JDialog reglesDialog = new JDialog(this, "Règles du Master Checkers", true);
        reglesDialog.setLayout(new BorderLayout());
        
        // Fond avec le même style
        BackgroundPanel dialogPanel = new BackgroundPanel(
            "https://i.pinimg.com/736x/61/38/22/6138225ae47e1549bcb77b075efe0f63.jpg", 
            DARK_WOOD
        );
        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBackground(new Color(0, 0, 0, 180));
        textArea.setForeground(LIGHT_GOLD);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        textArea.setText(
            "RÈGLES DU MASTER CHECKERS\n\n" +
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
        scrollPane.setBorder(new GoldBorder());
        scrollPane.getViewport().setOpaque(false);
        
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton okButton = createGoldButton("Fermer");
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.addActionListener(e -> reglesDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        reglesDialog.setContentPane(dialogPanel);
        reglesDialog.setSize(500, 500);
        reglesDialog.setLocationRelativeTo(this);
        reglesDialog.setVisible(true);
    }

    private void nouvellePartie() {
        jeu = new Jeu();
        positionSelectionnee = null;
        historiqueTextArea.setText("");
        btnRefaire.setVisible(false);
        mettreAJourInterface();
    }

    private void mettreAJourInterface() {
        int[] compteur = jeu.getPlateau().compterPieces();
        scoreLabel.setText("Blancs: " + compteur[0] + " | Noirs: " + compteur[1]);

        if (jeu.estPartieTerminee()) {
            if (compteur[0] == 0) {
                statusLabel.setText("LES NOIRS ONT GAGNÉ !");
            } else if (compteur[1] == 0) {
                statusLabel.setText("LES BLANCS ONT GAGNÉ !");
            } else if (jeu.getPlateau().getMouvementsPossibles(true).isEmpty()) {
                statusLabel.setText("LES NOIRS ONT GAGNÉ ! (Blancs bloqués)");
            } else {
                statusLabel.setText("LES BLANCS ONT GAGNÉ ! (Noirs bloqués)");
            }
            btnRefaire.setVisible(true);
        } else if (jeu.estPrisesEnChaineEnCours()) {
            statusLabel.setText((jeu.estTourBlanc() ? "BLANCS" : "NOIRS") + " : Continuez la prise en chaîne");
            btnRefaire.setVisible(false);
        } else {
            statusLabel.setText("Tour des " + (jeu.estTourBlanc() ? "BLANCS" : "NOIRS"));
            btnRefaire.setVisible(false);
        }

        plateauPanel.repaint();
    }

    private class PlateauPanel extends JPanel {
        private static final int TAILLE_CASE = 60; // Légèrement plus grand
        
        // Couleurs dorées pour le plateau
        private static final Color CASE_CLAIRE = new Color(255, 248, 220);    // Cornsilk
        private static final Color CASE_FONCEE = new Color(210, 180, 140);    // Tan
        private static final Color PIECE_BLANCHE = LIGHT_GOLD;                // Or clair
        private static final Color PIECE_NOIRE = new Color(101, 67, 33);      // Marron foncé
        private static final Color SELECTION = new Color(255, 215, 0, 200);   // Or avec transparence
        private static final Color COUP_POSSIBLE = new Color(50, 205, 50, 150); // Vert semi-transparent
        
        // Images pour le plateau et les pièces (identique au code original)
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
            setOpaque(false);

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
                    imageComplete = ImageIO.read(fichierImage);
                    
                    if (imageComplete.getWidth() < 100 || imageComplete.getHeight() < 150) {
                        throw new IOException("Dimensions de l'image inadéquates");
                    }
                    
                    decouperImages();
                    useImages = true;
                } else {
                    useImages = false;
                }
            } catch (Exception e) {
                useImages = false;
            }
        }
        
        private void decouperImages() {
            try {
                int largeurSprite = imageComplete.getWidth() / 2;
                int hauteurSprite = imageComplete.getHeight() / 3;
                
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
            historiqueTextArea.setCaretPosition(historiqueTextArea.getDocument().getLength());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fond du plateau avec gradient doré
            GradientPaint plateauGradient = new GradientPaint(
                0, 0, LIGHT_GOLD,
                getWidth(), getHeight(), DARK_GOLD
            );
            g2.setPaint(plateauGradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    int x = j * TAILLE_CASE;
                    int y = i * TAILLE_CASE;
                    
                    if (useImages) {
                        g2.drawImage(
                            (i + j) % 2 == 0 ? caseBlanc : caseNoir,
                            x, y, TAILLE_CASE, TAILLE_CASE, null
                        );
                    } else {
                        // Cases avec style doré
                        g2.setColor((i + j) % 2 == 0 ? CASE_CLAIRE : CASE_FONCEE);
                        g2.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);
                        
                        // Bordure subtile pour chaque case
                        g2.setColor(DARK_GOLD);
                        g2.setStroke(new BasicStroke(1));
                        g2.drawRect(x, y, TAILLE_CASE, TAILLE_CASE);
                    }

                    // Dessiner les pièces avec style doré
                    Piece piece = jeu.getPlateau().getPiece(new Position(i, j));
                    if (piece != null && !piece.estVide()) {
                        if (useImages) {
                            BufferedImage pieceImage;
                            if (piece.estDame()) {
                                pieceImage = piece.estBlanc() ? dameBlanc : dameNoir;
                            } else {
                                pieceImage = piece.estBlanc() ? pionBlanc : pionNoir;
                            }
                            g2.drawImage(pieceImage, x, y, TAILLE_CASE, TAILLE_CASE, null);
                        } else {
                            // Pièces avec style doré et effet 3D
                            int d = (int)(TAILLE_CASE * 0.75);
                            int px = x + (TAILLE_CASE - d) / 2;
                            int py = y + (TAILLE_CASE - d) / 2;
                            
                            // Ombre
                                                       // Ombre
                            g2.setColor(new Color(0, 0, 0, 100));
                            g2.fillOval(px + 2, py + 2, d, d);
                            
                            // Pièce principale
                            g2.setColor(piece.estBlanc() ? PIECE_BLANCHE : PIECE_NOIRE);
                            g2.fillOval(px, py, d, d);
                            
                            // Bordure
                            g2.setColor(piece.estBlanc() ? DARK_GOLD : Color.BLACK);
                            g2.setStroke(new BasicStroke(2));
                            g2.drawOval(px, py, d, d);
                            
                            // Reflet
                            g2.setColor(new Color(255, 255, 255, 80));
                            g2.fillOval(px + d/4, py + d/4, d/3, d/3);

                            if (piece.estDame()) {
                                // Couronne pour les dames
                                g2.setColor(piece.estBlanc() ? DARK_GOLD : LIGHT_GOLD);
                                g2.setFont(new Font("Arial", Font.BOLD, 16));
                                g2.drawString("★", px + d/2 - 8, py + d/2 + 6);
                            }
                        }
                    }
                }
            }

            // Affichage des sélections et mouvements possibles
            if (positionSelectionnee != null) {
                int x = positionSelectionnee.getColonne() * TAILLE_CASE;
                int y = positionSelectionnee.getLigne() * TAILLE_CASE;

                // Effet de sélection doré
                g2.setColor(SELECTION);
                g2.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);

                List<Mouvement> mouvements;
                if (jeu.estPrisesEnChaineEnCours()) {
                    mouvements = jeu.getPlateau().getPrisesEnChaine(positionSelectionnee, jeu.estTourBlanc());
                } else {
                    mouvements = jeu.getPlateau().getMouvementsPossibles(jeu.estTourBlanc());
                    mouvements.removeIf(m -> !m.getDebut().equals(positionSelectionnee));
                }

                // Cases de destination possibles
                for (Mouvement m : mouvements) {
                    Position fin = m.getFin();
                    int xf = fin.getColonne() * TAILLE_CASE;
                    int yf = fin.getLigne() * TAILLE_CASE;
                    g2.setColor(COUP_POSSIBLE);
                    g2.fillRect(xf, yf, TAILLE_CASE, TAILLE_CASE);
                }
            }
            
            // Ajout des coordonnées du plateau avec style doré
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            for (int i = 0; i < 8; i++) {
                // Lettres en bas (A-H)
                g2.setColor(i % 2 == 0 ? CASE_FONCEE : CASE_CLAIRE);
                g2.drawString(String.valueOf((char)('A' + i)), 
                             i * TAILLE_CASE + TAILLE_CASE/2 - 5, 
                             8 * TAILLE_CASE - 5);
                
                // Chiffres à gauche (1-8)
                g2.drawString(String.valueOf(8 - i), 
                             5, 
                             i * TAILLE_CASE + TAILLE_CASE/2 + 5);
            }
        }
    }

    public static void main(String[] args) {
        // Pour une meilleure qualité d'affichage
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        EventQueue.invokeLater(() -> {
            DamesGUI damesGUI = new DamesGUI();
            damesGUI.setVisible(true);
        });
    }
}