import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public class AccueilGUI extends JFrame {

    public AccueilGUI() {
        initUI();
    }

    private void initUI() {
        // Configuration de la fenêtre
        setTitle("Jeu de Dames - Accueil");
        setMinimumSize(new Dimension(600, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Création du panneau principal avec fond noir
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel pour le contenu central
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 20, 0);

        // Titre du jeu
        JLabel titleLabel = new JLabel("JEU DE DAMES", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        centerPanel.add(titleLabel, gbc);

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Version Classique", SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        centerPanel.add(subtitleLabel, gbc);

        // Image de référence (damier)
        try {
            // Utilisation d'une image interne (remplacer par votre propre image)
            URL imageUrl = getClass().getResource("/ressources/dames_image.png");
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image scaledImage = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                gbc.insets = new Insets(20, 0, 20, 0);
                centerPanel.add(imageLabel, gbc);
            } else {
                // Fallback si l'image n'est pas trouvée
                JLabel noImageLabel = new JLabel("Jeu de Dames", SwingConstants.CENTER);
                noImageLabel.setForeground(Color.WHITE);
                noImageLabel.setFont(new Font("Arial", Font.PLAIN, 24));
                centerPanel.add(noImageLabel, gbc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 0, 50));

        // Bouton Commencer
        JButton startButton = createStyledButton("COMMENCER", new Color(220, 220, 220));
        startButton.addActionListener(this::startGame);

        // Bouton Quitter
        JButton quitButton = createStyledButton("QUITTER", new Color(180, 180, 180));
        quitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(startButton);
        buttonPanel.add(quitButton);

        centerPanel.add(buttonPanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("© 2023 Jeu de Dames - Tous droits réservés", SwingConstants.CENTER);
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.BLACK);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void startGame(ActionEvent e) {
        new DamesGUI().setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            AccueilGUI accueil = new AccueilGUI();
            accueil.setVisible(true);
        });
    }
} 