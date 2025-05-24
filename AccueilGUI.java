import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

public class AccueilGUI extends JFrame {
    
    // Couleurs
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color DARK_WOOD = new Color(60, 30, 10);
    private static final Color LIGHT_GOLD = new Color(255, 223, 128);
    private static final Color DARK_GOLD = new Color(190, 150, 20);
    
    // Image du plateau
    private BufferedImage boardImage;
    
    public AccueilGUI() {
        loadResources();
        initUI();
    }
    
    private void loadResources() {
        try {
            // URL de l'image externe (exemple avec une image de damier)
            String imageUrl = "https://i.pinimg.com/736x/7f/03/ad/7f03addff74640def94a6832b1095472.jpg";
            
            // Utiliser ImageIcon pour un meilleur chargement des images externes
            ImageIcon icon = new ImageIcon(new URL(imageUrl));
            // Attendre que l'image soit complètement chargée
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                boardImage = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB
                );
                Graphics g = boardImage.createGraphics();
                icon.paintIcon(null, g, 0, 0);
                g.dispose();
                System.out.println("Image chargée depuis URL externe: " + 
                    boardImage.getWidth() + "x" + boardImage.getHeight());
            } else {
                throw new IOException("Erreur de chargement de l'image: statut incomplet");
            }
            
        } catch (Exception e) {
            System.out.println("Erreur de chargement externe: " + e.getMessage());
            
            // Fallback: charger depuis les ressources locales
            try (InputStream is = getClass().getResourceAsStream("/resources/dames_image.png")) {
                if (is != null) {
                    boardImage = ImageIO.read(is);
                    System.out.println("Image chargée depuis ressources locales");
                } else {
                    // Fallback ultime
                    System.out.println("Création d'un damier programmatique");
                    boardImage = createCheckerboardImage(600, 600);
                }
            } catch (IOException ex) {
                System.out.println("Erreur de chargement local: " + ex.getMessage());
                boardImage = createCheckerboardImage(600, 600);
            }
        }
    }

    /**
     * Crée une image de damier programmatiquement
     */
    private BufferedImage createCheckerboardImage(int width, int height) {
        BufferedImage boardImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boardImg.createGraphics();
        
        // Activer l'antialiasing pour une meilleure qualité
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int squareSize = width / 8;
        
        // Dessiner le fond (bordure)
        g2d.setColor(DARK_GOLD);
        g2d.fillRect(0, 0, width, height);
        
        // Dessiner les cases
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int x = col * squareSize;
                int y = row * squareSize;
                
                if ((row + col) % 2 == 0) {
                    g2d.setColor(new Color(240, 217, 181)); // Cases claires
                } else {
                    g2d.setColor(new Color(181, 136, 99)); // Cases foncées
                }
                
                g2d.fillRect(x, y, squareSize, squareSize);
                
                // Dessiner les pièces
                if ((row + col) % 2 != 0) {
                    if (row < 3) {
                        drawPiece(g2d, x, y, squareSize, Color.BLACK);
                    } else if (row > 4) {
                        drawPiece(g2d, x, y, squareSize, LIGHT_GOLD);
                    }
                }
            }
        }
        
        // Dessiner une bordure dorée
        g2d.setColor(DARK_GOLD);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(0, 0, width - 1, height - 1);
        
        g2d.dispose();
        return boardImg;
    }
    
    /**
     * Dessine une pièce sur le damier
     */
    private void drawPiece(Graphics2D g2d, int x, int y, int size, Color color) {
        int padding = size / 8;
        int diameter = size - (padding * 2);
        
        // Effet d'ombre
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillOval(x + padding + 2, y + padding + 2, diameter, diameter);
        
        // Pièce
        g2d.setColor(color);
        g2d.fillOval(x + padding, y + padding, diameter, diameter);
        
        // Reflet
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillOval(x + padding + diameter/6, y + padding + diameter/6, diameter/3, diameter/3);
    }

    private void initUI() {
        // Configuration de la fenêtre
        setTitle("Master Checkers");
        setMinimumSize(new Dimension(1000, 800));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Création du panneau principal avec fond bois foncé
        String bgUrl = "https://i.pinimg.com/736x/61/38/22/6138225ae47e1549bcb77b075efe0f63.jpg";
        BackgroundPanel mainPanel = new BackgroundPanel(bgUrl, DARK_WOOD);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel pour le contenu central
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 20, 0);

        // Titre du jeu avec effet doré
        JLabel titleLabel = createGoldLabel("MASTER", 60);
        centerPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = createGoldLabel("CHECKERS", 60);
        centerPanel.add(subtitleLabel, gbc);

        // Ajouter l'image du damier avec effet 3D - IMPORTANT: boardPanel modifié
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setOpaque(false);
        boardContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Création du panneau pour l'image du damier
        final BufferedImage displayImage = boardImage; // Capture finale pour utilisation dans classe anonyme
        
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (displayImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    
                    // Calculer la taille pour préserver les proportions
                    int size = Math.min(getWidth(), getHeight());
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;
                    
                    // Dessiner l'ombre pour effet 3D
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillRect(x + 8, y + 8, size, size);
                    
                    // Dessiner l'image
                    g2d.drawImage(displayImage, x, y, size, size, this);
                    
                    // Bordure dorée
                    g2d.setColor(DARK_GOLD);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(x, y, size, size);
                    
                    // Reflet pour effet brillant
                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.drawLine(x + 2, y + 2, x + size - 2, y + 2);
                    g2d.drawLine(x + 2, y + 2, x + 2, y + size - 2);
                    
                    System.out.println("Image dessinée aux dimensions: " + size + "x" + size);
                } else {
                    // Cas où l'image est null
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(DARK_GOLD);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("Pas d'image disponible", 10, 20);
                }
            }
        };
        
        boardPanel.setPreferredSize(new Dimension(350, 350));
        boardPanel.setMinimumSize(new Dimension(0, 315));
        boardContainer.add(boardPanel, BorderLayout.CENTER);
        
        gbc.insets = new Insets(20, 0, 40, 0);
        centerPanel.add(boardContainer, gbc);

        // Bouton Play (style doré)
        JButton playButton = createGoldButton("PLAY");
        playButton.setPreferredSize(new Dimension(150, 80));
        playButton.addActionListener(this::startGame);
        gbc.insets = new Insets(20, 0, 20, 0);
        centerPanel.add(playButton, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Boutons dans les coins (style doré)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JButton infoButton = createSmallIconButton("https://i.pinimg.com/736x/81/11/30/811130512da8ae7224bb8bf417fb7454.jpg", "i");
        JButton soundButton = createSmallIconButton("/ressources/sound_icon.png", "🔊");
        
        topPanel.add(infoButton, BorderLayout.WEST);
        topPanel.add(soundButton, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        setContentPane(mainPanel);
    }
    
    // Classe pour gérer le fond avec image ou couleur de fond
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private Color backgroundColor;
        
        public BackgroundPanel(String imageUrl, Color fallbackColor) {
            try {
                // Utiliser ImageIcon pour un chargement asynchrone plus fiable
                ImageIcon icon = new ImageIcon(new URL(imageUrl));
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    backgroundImage = icon.getImage();
                    System.out.println("Arrière-plan chargé depuis URL");
                } else {
                    throw new IOException("Chargement d'image incomplet");
                }
            } catch (Exception e) {
                // Si ça échoue, essayer comme une ressource locale
                try {
                    InputStream is = getClass().getResourceAsStream(imageUrl);
                    if (is != null) {
                        backgroundImage = ImageIO.read(is);
                        System.out.println("Arrière-plan chargé depuis ressource locale");
                    }
                } catch (Exception ex) {
                    System.out.println("Erreur de chargement d'arrière-plan: " + ex.getMessage());
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
    
    // Classe pour créer une bordure avec ombre
    private class ShadowBorder implements Border {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (int i = 0; i < 5; i++) {
                g2d.setColor(new Color(0, 0, 0, 50 - i * 10));
                g2d.drawRect(x + i, y + i, width - 1 - i * 2, height - 1 - i * 2);
            }
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 5, 5, 5);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
    
    // Création d'un label avec effet doré
    private JLabel createGoldLabel(String text, int fontSize) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Background gradient for text
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
                
                // Draw text with gold gradient
                g2d.drawString(getText(), x, y);
                
                // Add highlight/glow effect
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
    
    // Création d'un petit bouton carré avec icône
    private JButton createSmallIconButton(String iconPath, String fallbackText) {
        JButton button = new JButton();
        
        try {
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image scaledImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            } else {
                button.setText(fallbackText);
            }
        } catch (Exception e) {
            button.setText(fallbackText);
        }
        
        button.setPreferredSize(new Dimension(50, 50));
        button.setBackground(DARK_GOLD);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
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
    
    // Création d'un bouton doré avec effet de relief et brillance
    private JButton createGoldButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded rectangle shape for button
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                // Gold gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, LIGHT_GOLD, 
                    0, getHeight(), DARK_GOLD
                );
                g2d.setPaint(gp);
                g2d.fill(roundedRectangle);
                
                // Border
                g2d.setColor(DARK_GOLD.darker());
                g2d.draw(roundedRectangle);
                
                // Text with shadow
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - textHeight) / 2) + fm.getAscent();
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.drawString(getText(), x + 1, y + 1);
                
                // Text
                g2d.setColor(Color.BLACK);
                g2d.drawString(getText(), x, y);
                
                // 3D effect / highlight
                if (!getModel().isPressed()) {
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.drawLine(5, 5, getWidth() - 5, 5);
                    g2d.drawLine(5, 5, 5, getHeight() - 5);
                }
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.BLACK);
        button.setBackground(GOLD_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void startGame(ActionEvent e) {
        // Vérifier si la classe DamesGUI existe
        try {
            // Essayer de charger la classe DamesGUI
            Class.forName("DamesGUI");
            
            // Si on arrive ici, la classe existe
            try {
                // Créer une instance et la rendre visible
                JFrame damesGUI = (JFrame) Class.forName("DamesGUI").getDeclaredConstructor().newInstance();
                damesGUI.setVisible(true);
                this.dispose(); // Fermer la fenêtre d'accueil
                System.out.println("Jeu démarré!");
            } catch (Exception ex) {
                System.out.println("Erreur lors du lancement du jeu: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du démarrage du jeu: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException ex) {
            // La classe DamesGUI n'existe pas
            System.out.println("La classe DamesGUI n'existe pas encore");
            JOptionPane.showMessageDialog(this, 
                "Le jeu va démarrer...\n(La classe DamesGUI n'est pas encore implémentée)", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Pour une meilleure qualité d'affichage
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Ajout de logs pour débogage
        System.out.println("Démarrage de l'application Master Checkers");
        
        EventQueue.invokeLater(() -> {
            AccueilGUI accueil = new AccueilGUI();
            accueil.setVisible(true);
            System.out.println("Interface d'accueil affichée");
        });
    }
}