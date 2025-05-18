import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;



/**
 * Enum pour représenter les types de pièces sur le plateau
 */
enum TypePiece {
    PION_BLANC, PION_NOIR, DAME_BLANCHE, DAME_NOIRE, VIDE
}

/**
 * Classe représentant une pièce du jeu
 */
class Piece {
    private TypePiece type;
    
    /**
     * Constructeur de pièce
     * @param type Le type de la pièce
     */
    public Piece(TypePiece type) {
        this.type = type;
    }
    
    /**
     * Getter pour le type de pièce
     * @return Le type de la pièce
     */
    public TypePiece getType() {
        return type;
    }
    
    /**
     * Modifie le type de la pièce
     * @param type Le nouveau type de la pièce
     */
    public void setType(TypePiece type) {
        this.type = type;
    }
    
    /**
     * Vérifie si la pièce est blanche
     * @return true si la pièce est blanche, false sinon
     */
    public boolean estBlanc() {
        return type == TypePiece.PION_BLANC || type == TypePiece.DAME_BLANCHE;
    }
    
    /**
     * Vérifie si la pièce est noire
     * @return true si la pièce est noire, false sinon
     */
    public boolean estNoir() {
        return type == TypePiece.PION_NOIR || type == TypePiece.DAME_NOIRE;
    }
    
    /**
     * Vérifie si la pièce est une dame
     * @return true si la pièce est une dame, false sinon
     */
    public boolean estDame() {
        return type == TypePiece.DAME_BLANCHE || type == TypePiece.DAME_NOIRE;
    }
    
    /**
     * Vérifie si la case est vide
     * @return true si la case est vide, false sinon
     */
    public boolean estVide() {
        return type == TypePiece.VIDE;
    }
    
    /**
     * Promeut un pion en dame
     */
    public void promouvoir() {
        if (type == TypePiece.PION_BLANC) {
            type = TypePiece.DAME_BLANCHE;
        } else if (type == TypePiece.PION_NOIR) {
            type = TypePiece.DAME_NOIRE;
        }
    }
    
    @Override
public String toString() {
    switch (type) {
      case PION_BLANC: return "\u001B[37mW\u001B[0m";  // Blanc
        case PION_NOIR: return "\u001B[30mB\u001B[0m";   // Noir
        case DAME_BLANCHE: return "\u001B[37mWD\u001B[0m"; // Dame blanche
        case DAME_NOIRE: return "\u001B[30mBD\u001B[0m";  // Dame noire
        case VIDE: return ".";
        default: return "?";
    }
}
}

/**
 * Classe Position pour représenter les coordonnées sur le plateau
 */
class Position {
    private int ligne;
    private int colonne;
    
    /**
     * Constructeur de position
     * @param ligne La ligne (0-7)
     * @param colonne La colonne (0-7)
     */
    public Position(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }
    
    /**
     * @return Le numéro de ligne
     */
    public int getLigne() {
        return ligne;
    }
    
    /**
     * @return Le numéro de colonne
     */
    public int getColonne() {
        return colonne;
    }
    
    /**
     * Vérifie si la position est sur le plateau
     * @return true si la position est valide, false sinon
     */
    public boolean estValide() {
        return ligne >= 0 && ligne < 8 && colonne >= 0 && colonne < 8;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return ligne == position.ligne && colonne == position.colonne;
    }
    
    @Override
    public int hashCode() {
        return 31 * ligne + colonne;
    }
    
    @Override
    public String toString() {
        return "(" + ligne + "," + colonne + ")";
    }
    
    /**
     * Méthode utilitaire pour vérifier si une position est entre deux autres
     * @param pos1 Première position
     * @param pos2 Deuxième position
     * @return true si cette position est entre pos1 et pos2 sur une diagonale
     */
  public boolean estEntre(Position pos1, Position pos2) {
    // Vérifier que les trois points sont alignés diagonalement
    int diffLigne1 = pos2.ligne - pos1.ligne;
    int diffCol1 = pos2.colonne - pos1.colonne;
    
    // Vérifier qu'on est sur une diagonale
    if (Math.abs(diffLigne1) != Math.abs(diffCol1)) {
        return false;
    }
    
    // Directions de la diagonale
    int dirLigne = Integer.compare(diffLigne1, 0);
    int dirCol = Integer.compare(diffCol1, 0);
    
    // Vérifier si cette position est sur la diagonale entre pos1 et pos2
    int distance = Math.abs(diffLigne1);
    
    for (int i = 1; i < distance; i++) {
        int checkLigne = pos1.ligne + i * dirLigne;
        int checkCol = pos1.colonne + i * dirCol;
        
        if (checkLigne == this.ligne && checkCol == this.colonne) {
            return true;
        }
    }
    
    return false;
}
public String toChessNotation() {
    char colonne = (char) ('A' + this.colonne);
    int ligne = this.ligne + 1;
    return String.valueOf(colonne) + ligne;
}

}

/**
 * Classe Mouvement pour représenter un déplacement
 */
class Mouvement {
    private Position debut;
    private Position fin;
    private List<Position> prises;
    
    /**
     * Constructeur de mouvement
     * @param debut Position de départ
     * @param fin Position d'arrivée
     */
    public Mouvement(Position debut, Position fin) {
        this.debut = debut;
        this.fin = fin;
        this.prises = new ArrayList<>();
    }
    
    /**
     * @return La position de départ
     */
    public Position getDebut() {
        return debut;
    }
    
    /**
     * @return La position d'arrivée
     */
    public Position getFin() {
        return fin;
    }
    
    /**
     * @return La liste des positions des pièces prises
     */
    public List<Position> getPrises() {
        return prises;
    }
    
    /**
     * Ajoute une position de pièce prise
     * @param position La position de la pièce prise
     */
    public void ajouterPrise(Position position) {
        prises.add(position);
    }
    
    /**
     * Vérifie si le mouvement contient des prises
     * @return true si au moins une prise est effectuée, false sinon
     */
    public boolean contientPrise() {
        return !prises.isEmpty();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(debut).append(" -> ").append(fin);
        
        if (!prises.isEmpty()) {
            sb.append(" (prises: ");
            for (int i = 0; i < prises.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(prises.get(i));
            }
            sb.append(")");
        }
        
        return sb.toString();
    }
}

/**
 * Classe Plateau pour représenter le plateau de jeu
 */
class Plateau {
    private Piece[][] cases;
    private static final int TAILLE = 8;
    
    /**
     * Constructeur du plateau
     * Initialise le plateau avec la configuration de départ
     */
    public Plateau() {
        cases = new Piece[TAILLE][TAILLE];
        initialiser();
    }
    public static int getTaille() {
        return TAILLE;
    }
    /**
 * Vérifie si une pièce à une position donnée peut effectuer une prise
 * @param position La position de la pièce à vérifier
 * @param joueurBlanc true si c'est le joueur blanc, false si c'est le joueur noir
 * @return true si la pièce peut effectuer une prise, false sinon
 */
public boolean piecePeutPrendre(Position position, boolean joueurBlanc) {
    Piece piece = getPiece(position);
    if (piece == null || piece.estVide() || (joueurBlanc && !piece.estBlanc()) || (!joueurBlanc && !piece.estNoir())) {
        return false;
    }
    
    List<Mouvement> prisesPossibles = getPrisesEnChaine(position, joueurBlanc);
    return !prisesPossibles.isEmpty();
}

   public List<Mouvement> getTousMouvementsPossibles(boolean joueurBlanc) {
    List<Mouvement> mouvements = new ArrayList<>();
    
    // Parcourir toutes les pièces du joueur
    for (int i = 0; i < TAILLE; i++) {
        for (int j = 0; j < TAILLE; j++) {
            Position debut = new Position(i, j);
            Piece piece = getPiece(debut);
            
            // Vérifier si c'est une pièce du joueur actuel
            if (piece != null && ((joueurBlanc && piece.estBlanc()) || (!joueurBlanc && piece.estNoir()))) {
                // Récupérer les prises potentielles pour cette pièce
                List<Mouvement> prisesEnChaine = getPrisesEnChaine(debut, joueurBlanc);
                if (!prisesEnChaine.isEmpty()) {
                    mouvements.addAll(prisesEnChaine);
                }
                
                // Ajouter aussi les déplacements simples
                if (!piece.estDame()) {
                    // Pour les pions simples
                    int direction = joueurBlanc ? -1 : 1;
                    for (int dj : new int[]{-1, 1}) {
                        int newRow = i + direction;
                        int newCol = j + dj;
                        Position fin = new Position(newRow, newCol);
                        if (fin.estValide() && getPiece(fin) != null && getPiece(fin).estVide()) {
                            Mouvement newMove = new Mouvement(debut, fin);
                            mouvements.add(newMove);
                        }
                    }
                } else {
                    // Pour les dames - mouvements diagonaux dans toutes les directions
                    for (int dirLigne : new int[]{-1, 1}) {
                        for (int dirColonne : new int[]{-1, 1}) {
                            // On parcourt la diagonale jusqu'à trouver un obstacle
                            for (int distance = 1; distance < TAILLE; distance++) {
                                Position fin = new Position(i + dirLigne * distance, j + dirColonne * distance);
                                if (!fin.estValide()) break;
                                
                                Piece pieceFin = getPiece(fin);
                                if (pieceFin == null || !pieceFin.estVide()) break;
                                
                                // La diagonale est libre jusqu'ici, on peut ajouter ce mouvement
                                mouvements.add(new Mouvement(debut, fin));
                            }
                        }
                    }
                }
            }
        }
    }
    
    return mouvements;
} 
    /**
     * Initialise le plateau avec la position de départ
     */
    private void initialiser() {
        // Remplir toutes les cases avec des pièces vides
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                cases[i][j] = new Piece(TypePiece.VIDE);
            }
        }
        
        // Placer les pions noirs (en haut)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < TAILLE; j++) {
                if ((i + j) % 2 == 1) { // Cases noires uniquement
                    cases[i][j] = new Piece(TypePiece.PION_NOIR);
                }
            }
        }
        
        // Placer les pions blancs (en bas)
        for (int i = 5; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                if ((i + j) % 2 == 1) { // Cases noires uniquement
                    cases[i][j] = new Piece(TypePiece.PION_BLANC);
                }
            }
        }
    }
    
    /**
     * Récupère la pièce à une position donnée
     * @param position La position
     * @return La pièce à cette position, ou null si la position est invalide
     */
    public Piece getPiece(Position position) {
        if (position.estValide()) {
            return cases[position.getLigne()][position.getColonne()];
        }
        return null;
    }
    
    /**
     * Effectue un déplacement sur le plateau
     * @param mouvement Le mouvement à effectuer
     */
    public void deplacer(Mouvement mouvement) {
        Position debut = mouvement.getDebut();
        Position fin = mouvement.getFin();
        Piece piece = getPiece(debut);
        
        // Déplacer la pièce
        cases[fin.getLigne()][fin.getColonne()] = piece;
        cases[debut.getLigne()][debut.getColonne()] = new Piece(TypePiece.VIDE);
        
        // Supprimer les pièces prises
        for (Position prise : mouvement.getPrises()) {
            cases[prise.getLigne()][prise.getColonne()] = new Piece(TypePiece.VIDE);
        }
        
        // Promotion en dame si la pièce atteint la dernière ligne
        if ((piece.getType() == TypePiece.PION_BLANC && fin.getLigne() == 0) ||
            (piece.getType() == TypePiece.PION_NOIR && fin.getLigne() == TAILLE - 1)) {
            piece.promouvoir();
        }
    }
    
    
    /**
     * Vérifie si un mouvement est valide
     * @param mouvement Le mouvement à vérifier
     * @param joueurBlanc true si c'est au tour du joueur blanc, false sinon
     * @return true si le mouvement est valide, false sinon
     */
    public boolean estMouvementValide(Mouvement mouvement, boolean joueurBlanc) {
        Position debut = mouvement.getDebut();
        Position fin = mouvement.getFin();
        Piece piece = getPiece(debut);
        
        // Vérifications de base
        if (!debut.estValide() || !fin.estValide()) {
            return false;
        }
        
        // Vérifier que la case de départ contient une pièce du bon joueur
        if (piece == null || piece.estVide() || (joueurBlanc && !piece.estBlanc()) || (!joueurBlanc && !piece.estNoir())) {
            return false;
        }
        
        // Vérifier que la case d'arrivée est vide
        Piece pieceFin = getPiece(fin);
        if (pieceFin == null || !pieceFin.estVide()) {
            return false;
        }

        List<Mouvement> tousLesMouvements = getMouvementsPossibles(joueurBlanc);
    boolean prisesObligatoires = !tousLesMouvements.isEmpty() && tousLesMouvements.get(0).contientPrise();
    
    // Si des prises sont obligatoires, le mouvement doit être une prise
    if (prisesObligatoires && !mouvement.contientPrise()) {
        return false;
    }
    
        
        // Vérifier si le déplacement est diagonal
        int diffLigne = fin.getLigne() - debut.getLigne();
        int diffColonne = fin.getColonne() - debut.getColonne();
        
        if (Math.abs(diffLigne) != Math.abs(diffColonne)) {
            return false;
        }
        
        // Pour les pions simples, le déplacement ne peut se faire qu'en avant
        if (!piece.estDame()) {
            if ((piece.estBlanc() && diffLigne >= 0) || (piece.estNoir() && diffLigne <= 0)) {
                return false;
            }
            
            // Déplacement simple (1 case)
            if (Math.abs(diffLigne) == 1) {
                return true;
            }
            
            // Déplacement avec prise (2 cases)
            if (Math.abs(diffLigne) == 2) {
                int ligneIntermediaire = (debut.getLigne() + fin.getLigne()) / 2;
                int colonneIntermediaire = (debut.getColonne() + fin.getColonne()) / 2;
                Position positionIntermediaire = new Position(ligneIntermediaire, colonneIntermediaire);
                Piece pieceIntermediaire = getPiece(positionIntermediaire);
                
                // Vérifier si la pièce intermédiaire est une pièce adverse
                if (pieceIntermediaire != null && 
                    ((piece.estBlanc() && pieceIntermediaire.estNoir()) ||
                    (piece.estNoir() && pieceIntermediaire.estBlanc()))) {
                    mouvement.ajouterPrise(positionIntermediaire);
                    return true;
                }
            }
            
            return false;
        } else {
            // Traitement pour les dames
            int pas = Math.abs(diffLigne);
            int dirLigne = diffLigne > 0 ? 1 : -1;
            int dirColonne = diffColonne > 0 ? 1 : -1;
            
            // Vérifier s'il y a des pièces sur le chemin
            int piecesSurChemin = 0;
            Position dernierePiece = null;
            
            for (int i = 1; i < pas; i++) {
                int li = debut.getLigne() + i * dirLigne;
                int ci = debut.getColonne() + i * dirColonne;
                Position positionIntermediaire = new Position(li, ci);
                Piece pieceIntermediaire = getPiece(positionIntermediaire);
                
                if (pieceIntermediaire != null && !pieceIntermediaire.estVide()) {
                    // Si pièce de même couleur, mouvement invalide
                    if ((piece.estBlanc() && pieceIntermediaire.estBlanc()) ||
                        (piece.estNoir() && pieceIntermediaire.estNoir())) {
                        return false;
                    }
                    
                    // Si plus d'une pièce sur le chemin, mouvement invalide
                    if (piecesSurChemin > 0) {
                        return false;
                    }
                    
                    piecesSurChemin++;
                    dernierePiece = positionIntermediaire;
                }
            }
            
            // Si aucune pièce sur le chemin, c'est un déplacement simple valide
            if (piecesSurChemin == 0) {
                return true;
            }
            
            // Si une pièce sur le chemin, c'est une prise valide
            if (piecesSurChemin == 1) {
                mouvement.ajouterPrise(dernierePiece);
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * Recherche tous les mouvements possibles pour un joueur
     * @param joueurBlanc true pour les blancs, false pour les noirs
     * @return Liste des mouvements possibles
     */
  public List<Mouvement> getMouvementsPossibles(boolean joueurBlanc) {
    List<Mouvement> mouvements = new ArrayList<>();
    
    // Parcourir toutes les pièces du joueur
    for (int i = 0; i < TAILLE; i++) {
        for (int j = 0; j < TAILLE; j++) {
            Position debut = new Position(i, j);
            Piece piece = getPiece(debut);
            
            // Vérifier si c'est une pièce du joueur actuel
            if (piece != null && ((joueurBlanc && piece.estBlanc()) || (!joueurBlanc && piece.estNoir()))) {
                // Récupérer les prises potentielles pour cette pièce
                List<Mouvement> prisesEnChaine = getPrisesEnChaine(debut, joueurBlanc);
                if (!prisesEnChaine.isEmpty()) {
                    mouvements.addAll(prisesEnChaine);
                }
                
                // Ajouter aussi les déplacements simples si aucune prise n'est disponible
                // Les prises sont obligatoires donc on n'ajoute les déplacements simples
                // que s'il n'y a pas de prise disponible pour ce joueur
                if (mouvements.isEmpty() || !mouvements.get(0).contientPrise()) {
                    if (!piece.estDame()) {
                        // Pour les pions simples
                        int direction = joueurBlanc ? -1 : 1;
                        for (int dj : new int[]{-1, 1}) {
                            int newRow = i + direction;
                            int newCol = j + dj;
                            Position fin = new Position(newRow, newCol);
                            if (fin.estValide() && getPiece(fin) != null && getPiece(fin).estVide()) {
                                Mouvement newMove = new Mouvement(debut, fin);
                                mouvements.add(newMove);
                            }
                        }
                    } else {
                        // Pour les dames - mouvements diagonaux dans toutes les directions
                        for (int dirLigne : new int[]{-1, 1}) {
                            for (int dirColonne : new int[]{-1, 1}) {
                                // On parcourt la diagonale jusqu'à trouver un obstacle
                                for (int distance = 1; distance < TAILLE; distance++) {
                                    Position fin = new Position(i + dirLigne * distance, j + dirColonne * distance);
                                    if (!fin.estValide()) break;
                                    
                                    Piece pieceFin = getPiece(fin);
                                    if (pieceFin == null || !pieceFin.estVide()) break;
                                    
                                    // La diagonale est libre jusqu'ici, on peut ajouter ce mouvement
                                    mouvements.add(new Mouvement(debut, fin));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Si des prises sont disponibles, on ne garde que les prises (obligation de prendre)
    if (!mouvements.isEmpty() && mouvements.get(0).contientPrise()) {
        mouvements.removeIf(m -> !m.contientPrise());
    }
    
    return mouvements;
}

// Remplacer la méthode toStringWithMoves par cette version améliorée
public String toStringWithMoves(List<Mouvement> mouvements) {
    StringBuilder sb = new StringBuilder();
    sb.append("Légende: \u001B[37mW\u001B[0m=Pion blanc, \u001B[30mB\u001B[0m=Pion noir, \u001B[37mWD\u001B[0m=Dame blanche, \u001B[30mBD\u001B[0m=Dame noire\n");
    
    // En-têtes de colonnes
    sb.append("     ");
    for (char c = 'A'; c < 'A'+TAILLE; c++) {
        sb.append(" ").append(c).append("  ");
    }
    sb.append("\n");
    
    // Ligne de séparation haute
    sb.append("   ╔");
    for (int j = 0; j < TAILLE; j++) {
        sb.append("═══");
        if (j < TAILLE-1) sb.append("╦");
    }
    sb.append("╗\n");
    
    // Cases du plateau
    for (int i = 0; i < TAILLE; i++) {
        sb.append(String.format("%2d ║", i+1));
        
        for (int j = 0; j < TAILLE; j++) {
            Position pos = new Position(i, j);
            boolean estDestination = false;
            int moveIndex = 0;
            
            // Vérifier si c'est une destination de mouvement
            for (int k = 0; k < mouvements.size(); k++) {
                if (mouvements.get(k).getFin().equals(pos)) {
                    estDestination = true;
                    moveIndex = k + 1;
                    break;
                }
            }
            
            if (estDestination) {
                sb.append(String.format(" \u001B[32m%2d\u001B[0m ", moveIndex));
            } else {
                Piece p = cases[i][j];
                String pieceStr = p.toString();
                sb.append(String.format(" %s ", pieceStr)); // Supprimer les couleurs pour uniformité
            }
            sb.append("║");
        }
        sb.append("\n");
        
        // Ligne de séparation entre les rangées
        if (i < TAILLE-1) {
            sb.append("   ╠");
            for (int j = 0; j < TAILLE; j++) {
                sb.append("═══");
                if (j < TAILLE-1) sb.append("╬");
            }
            sb.append("╣\n");
        }
    }
    
    // Ligne de séparation basse
    sb.append("   ╚");
    for (int j = 0; j < TAILLE; j++) {
        sb.append("═══");
        if (j < TAILLE-1) sb.append("╩");
    }
    sb.append("╝\n");
    
    // Légende des mouvements si nécessaire
   if (!mouvements.isEmpty()) {
    sb.append("\nMouvements possibles :\n");
    for (int i = 0; i < mouvements.size(); i++) {
        Mouvement m = mouvements.get(i);
        sb.append(String.format(" %2d: %s -> %s", i+1, 
            positionToChessNotation(m.getDebut()),
            positionToChessNotation(m.getFin())));
        
        if (m.contientPrise()) {
            sb.append(" (prise)");
        }
        sb.append("\n");
    }
}
    
    return sb.toString();
}
public String positionToChessNotation(Position pos) {
    // Convert column to letter (a-j for columns 0-9)
    char col = (char) ('a' + pos.getColonne());
    // Convert row to number (1-10 for rows 0-9)
    int row = pos.getLigne() + 1;
    return "" + col + row;
}
public List<Mouvement> getMouvementsPossibles(Position pos, boolean joueurBlanc) {
    List<Mouvement> allMoves = getTousMouvementsPossibles(joueurBlanc);
    List<Mouvement> pieceMoves = new ArrayList<>();
    
    for (Mouvement m : allMoves) {
        if (m.getDebut().equals(pos)) {
            pieceMoves.add(m);
        }
    }
    return pieceMoves;
}
    /**
     * Recherche les prises possibles en chaîne depuis une position
     * @param position Position de départ
     * @param joueurBlanc true pour les blancs, false pour les noirs
     * @return Liste des mouvements de prise possibles
     */
 // Modifier la méthode getPrisesEnChaine pour mieux gérer les prises multiples
public List<Mouvement> getPrisesEnChaine(Position position, boolean joueurBlanc) {
    List<Mouvement> prises = new ArrayList<>();
    Piece piece = getPiece(position);
    
    if (piece == null || piece.estVide() || (joueurBlanc && !piece.estBlanc()) || (!joueurBlanc && !piece.estNoir())) {
        return prises;
    }
    
    // Nouvelle approche récursive pour trouver toutes les prises possibles
    Set<Position> positionsPrises = new HashSet<>();
    List<Mouvement> mouvementsComplets = new ArrayList<>();
    
    trouverPrisesCompletes(position, joueurBlanc, new ArrayList<>(), positionsPrises, new Mouvement(position, position), mouvementsComplets);
    
    // Garder uniquement les prises avec le maximum de pièces capturées
    if (!mouvementsComplets.isEmpty()) {
        int maxPrises = mouvementsComplets.stream()
            .mapToInt(m -> m.getPrises().size())
            .max()
            .orElse(0);
        
        return mouvementsComplets.stream()
            .filter(m -> m.getPrises().size() == maxPrises)
            .collect(Collectors.toList());
    }
    
    return prises;
}

// Nouvelle méthode récursive pour trouver les prises complètes
private void trouverPrisesCompletes(Position position, boolean joueurBlanc, 
                                   List<Position> chemin, Set<Position> positionsPrises,
                                   Mouvement mouvementActuel, List<Mouvement> resultats) {
    
    List<Mouvement> prisesSimples = trouverPrisesSimples(position, joueurBlanc);
    prisesSimples.removeIf(m -> positionsPrises.contains(m.getPrises().get(0)));
    
    if (prisesSimples.isEmpty()) {
        if (mouvementActuel.contientPrise()) {
            resultats.add(mouvementActuel);
        }
        return;
    }
    
    for (Mouvement prise : prisesSimples) {
        Position nouvellePosition = prise.getFin();
        Position positionPrise = prise.getPrises().get(0);
        
        // Simuler la prise
        Piece piece = getPiece(position);
        cases[position.getLigne()][position.getColonne()] = new Piece(TypePiece.VIDE);
        cases[positionPrise.getLigne()][positionPrise.getColonne()] = new Piece(TypePiece.VIDE);
        Piece pieceOriginale = cases[nouvellePosition.getLigne()][nouvellePosition.getColonne()];
        cases[nouvellePosition.getLigne()][nouvellePosition.getColonne()] = piece;
        
        // Créer un nouveau mouvement avec toutes les prises
        Mouvement nouveauMouvement = new Mouvement(mouvementActuel.getDebut(), nouvellePosition);
        mouvementActuel.getPrises().forEach(nouveauMouvement::ajouterPrise);
        nouveauMouvement.ajouterPrise(positionPrise);
        
        // Explorer récursivement
        Set<Position> nouvellesPositionsPrises = new HashSet<>(positionsPrises);
        nouvellesPositionsPrises.add(positionPrise);
        
        trouverPrisesCompletes(nouvellePosition, joueurBlanc, chemin, nouvellesPositionsPrises, nouveauMouvement, resultats);
        
        // Annuler la simulation
        cases[position.getLigne()][position.getColonne()] = piece;
        cases[positionPrise.getLigne()][positionPrise.getColonne()] = new Piece(joueurBlanc ? TypePiece.PION_NOIR : TypePiece.PION_BLANC);
        cases[nouvellePosition.getLigne()][nouvellePosition.getColonne()] = pieceOriginale;
    }
}

// Nouvelle méthode pour trouver les prises simples depuis une position
private List<Mouvement> trouverPrisesSimples(Position position, boolean joueurBlanc) {
    List<Mouvement> prises = new ArrayList<>();
    Piece piece = getPiece(position);
    if (piece == null || piece.estVide()) return prises;

    // Gestion des pions (à conserver)
    if (!piece.estDame()) {
        int direction = piece.estBlanc() ? -1 : 1;
        for (int dirCol : new int[]{-1, 1}) {
            Position posAdverse = new Position(
                position.getLigne() + direction,
                position.getColonne() + dirCol
            );
            Position posFin = new Position(
                position.getLigne() + 2 * direction,
                position.getColonne() + 2 * dirCol
            );

            if (posAdverse.estValide() && posFin.estValide()) {
                Piece pieceAdverse = getPiece(posAdverse);
                Piece pieceFin = getPiece(posFin);

                if (pieceAdverse != null && pieceFin != null &&
                    ((piece.estBlanc() && pieceAdverse.estNoir()) || 
                     (piece.estNoir() && pieceAdverse.estBlanc())) &&
                    pieceFin.estVide()) {
                    
                    Mouvement m = new Mouvement(position, posFin);
                    m.ajouterPrise(posAdverse);
                    prises.add(m);
                }
            }
        }
    } 
    // Gestion des dames (version corrigée)
    else {
        for (int dirLigne : new int[]{-1, 1}) {
            for (int dirCol : new int[]{-1, 1}) {
                Position positionPrise = null;
                
                // 1. Trouver la pièce à prendre
                for (int dist = 1; dist < TAILLE; dist++) {
                    Position posCourante = new Position(
                        position.getLigne() + dist * dirLigne,
                        position.getColonne() + dist * dirCol
                    );
                    
                    if (!posCourante.estValide()) break;
                    
                    Piece p = getPiece(posCourante);
                    if (p == null) break;
                    
                    if (p.estVide()) continue;
                    
                    if ((piece.estBlanc() && p.estNoir()) || 
                        (piece.estNoir() && p.estBlanc())) {
                        positionPrise = posCourante;
                        break;
                    } else {
                        break; // Pièce alliée bloque le chemin
                    }
                }
                
                // 2. Si prise trouvée, vérifier les cases derrière
                if (positionPrise != null) {
                    int lignePrise = positionPrise.getLigne();
                    int colPrise = positionPrise.getColonne();
                    
                    for (int dist = 1; dist < TAILLE; dist++) {
                        Position posFin = new Position(
                            lignePrise + dist * dirLigne,
                            colPrise + dist * dirCol
                        );
                        
                        if (!posFin.estValide()) break;
                        
                        Piece pFin = getPiece(posFin);
                        if (pFin == null || !pFin.estVide()) break;
                        
                        Mouvement m = new Mouvement(position, posFin);
                        m.ajouterPrise(positionPrise);
                        prises.add(m);
                    }
                }
            }
        }
    }
    return prises;
}
// Nouvelle méthode pour prolonger les prises en chaîne de manière itérative
private List<Mouvement> prolongerPrisesEnChaine(Mouvement priseInitiale, boolean joueurBlanc) {
    List<Mouvement> resultat = new ArrayList<>();
    List<Mouvement> aTraiter = new ArrayList<>();
    aTraiter.add(priseInitiale);
    
    while (!aTraiter.isEmpty()) {
        Mouvement priseActuelle = aTraiter.remove(0);
        
        // Créer un plateau temporaire pour simuler la prise
        Plateau plateauTemp = this.clone();
        
        // Effectuer la prise sur le plateau temporaire
        plateauTemp.deplacer(priseActuelle);
        
        // Marquer toutes les positions déjà prises pour éviter de les reprendre
        Set<Position> positionsPrises = new HashSet<>(priseActuelle.getPrises());
        
        // Rechercher les prises suivantes possibles depuis la position d'arrivée
        List<Mouvement> prisesSimplesSuivantes = plateauTemp.trouverPrisesSimples(priseActuelle.getFin(), joueurBlanc);
        
        // Filtrer pour éviter de reprendre des pièces déjà prises
        prisesSimplesSuivantes.removeIf(m -> {
            for (Position p : m.getPrises()) {
                // Vérifier si une position équivalente existe déjà dans les positions prises
                for (Position posPrise : positionsPrises) {
                    if (p.equals(posPrise)) {  // Utiliser equals plutôt que de comparer les coordonnées
                        return true;
                    }
                }
            }
            return false;
        });
        
        if (prisesSimplesSuivantes.isEmpty()) {
            // Si pas de prises suivantes, cette prise est terminée
            resultat.add(priseActuelle);
        } else {
            // Pour chaque prise simple suivante, créer un nouveau mouvement prolongé
            for (Mouvement priseSuivante : prisesSimplesSuivantes) {
                Mouvement mouvementProlonge = new Mouvement(priseInitiale.getDebut(), priseSuivante.getFin());
                
                // Copier toutes les prises du mouvement actuel
                for (Position positionPrise : priseActuelle.getPrises()) {
                    mouvementProlonge.ajouterPrise(positionPrise);
                }
                
                // Ajouter la nouvelle prise
                for (Position positionPrise : priseSuivante.getPrises()) {
                    mouvementProlonge.ajouterPrise(positionPrise);
                }
                
                // Ajouter ce mouvement prolongé à la liste à traiter
                aTraiter.add(mouvementProlonge);
            }
        }
    }
    
    return resultat;
}
 
   @Override
public String toString() {
    return toStringWithMoves(new ArrayList<>()); // Affiche sans mouvements
}
 
    /**
     * Clone le plateau (utile pour la simulation de coups)
     * @return Un nouveau plateau identique à celui-ci
     */
    public Plateau clone() {
        Plateau clone = new Plateau();
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                clone.cases[i][j] = new Piece(cases[i][j].getType());
            }
        }
        return clone;
    }
    
    /**
     * Compte le nombre de pièces de chaque couleur
     * @return Un tableau contenant [nombre de pièces blanches, nombre de pièces noires]
     */
    public int[] compterPieces() {
        int[] compteur = new int[2]; // [blancs, noirs]
        
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                Piece piece = cases[i][j];
                if (piece.estBlanc()) {
                    compteur[0]++;
                } else if (piece.estNoir()) {
                    compteur[1]++;
                }
            }
        }
        
        return compteur;
    }
   public Position jouerCoup(Mouvement mouvement, boolean joueurBlanc) {
    deplacer(mouvement);
    return mouvement.getFin();
}

public boolean estPartieTerminee() {
    int[] compteur = compterPieces();
    if (compteur[0] == 0 || compteur[1] == 0) return true;
    
    boolean blancsPeuventJouer = !getMouvementsPossibles(true).isEmpty();
    boolean noirsPeuventJouer = !getMouvementsPossibles(false).isEmpty();
    
    return !blancsPeuventJouer || !noirsPeuventJouer;
}
public boolean estPrisesEnChaineEnCours(Position position, boolean joueurBlanc) {
    List<Mouvement> prises = getPrisesEnChaine(position, joueurBlanc);
    return !prises.isEmpty();
}

}

/**
 * Classe Jeu pour gérer la logique du jeu
 */
class Jeu {
    private Plateau plateau;
    private boolean tourBlanc;
    private boolean partieTerminee;
    private Position positionDernierePrise;
    private boolean prisesEnChaineEnCours;
    private int coupsSansPrise;
private static final int MAX_COUPS_SANS_PRISE = 50;
private List<String> historiqueCoups = new ArrayList<>();
private List<String> historiquesEtats = new ArrayList<>(); // Pour la détection de répétition


    
    /**
     * Constructeur du jeu
     * Initialise une nouvelle partie
     */
    public Jeu() {
        plateau = new Plateau();
        tourBlanc = true; // Les blancs commencent
        partieTerminee = false;
        positionDernierePrise = null;
        prisesEnChaineEnCours = false;
    }
    private String genererNotationCoup(Mouvement mouvement) {
    StringBuilder notation = new StringBuilder();
    
    // Position de départ en notation échecs (ex: E3)
    Position debut = mouvement.getDebut();
    char colonneDebut = (char) ('A' + debut.getColonne());
    int ligneDebut = debut.getLigne() + 1;
    notation.append(colonneDebut).append(ligneDebut);
    
    // Séparateur différent selon qu'il y a prise ou non
    if (mouvement.contientPrise()) {
        notation.append("x");
    } else {
        notation.append("-");
    }
    
    // Position d'arrivée
    Position fin = mouvement.getFin();
    char colonneFin = (char) ('A' + fin.getColonne());
    int ligneFin = fin.getLigne() + 1;
    notation.append(colonneFin).append(ligneFin);
    
    // Ajouter des informations sur les prises si nécessaire
    if (mouvement.contientPrise() && mouvement.getPrises().size() > 1) {
        notation.append(" (").append(mouvement.getPrises().size()).append(" prises)");
    }
    
    return notation.toString();
}
// Ajouter cette méthode pour afficher des informations de tour

// Méthode pour afficher l'historique des coups
public String getHistoriqueCoups() {
    StringBuilder historique = new StringBuilder("Historique des coups:\n");
    
    for (int i = 0; i < historiqueCoups.size(); i++) {
        if (i % 2 == 0) {
            historique.append((i/2 + 1)).append(". ");
        }
        historique.append(historiqueCoups.get(i));
        
        if (i % 2 == 0) {
            historique.append(" ");
        } else {
            historique.append("\n");
        }
    }
    
    return historique.toString();
}
    
    /**
     * @return Le plateau de jeu
     */
    public Plateau getPlateau() {
        return plateau;
    }
    
    /**
     * @return true si c'est le tour des blancs, false sinon
     */
    public boolean estTourBlanc() {
        return tourBlanc;
    }
    
    /**
     * @return true si la partie est terminée, false sinon
     */
    public boolean estPartieTerminee() {
        return partieTerminee;
    }
    
    /**
     * @return true si des prises en chaîne sont en cours, false sinon
     */
    public boolean estPrisesEnChaineEnCours() {
        return prisesEnChaineEnCours;
    }
    
    /**
     * @return La position de la dernière prise pour les prises en chaîne, null si aucune
     */
    public Position getPositionDernierePrise() {
        return positionDernierePrise;
    }
    
    /**
     * Vérifie si le mouvement est autorisé dans le contexte actuel du jeu
     * @param mouvement Le mouvement à vérifier
     * @return true si le mouvement est autorisé, false sinon
     */
    
// 1. Correction de la méthode estMouvementAutorise dans la classe Jeu
public boolean estMouvementAutorise(Mouvement mouvement) {
    // Si prises en chaîne en cours, seule la pièce qui vient de prendre peut jouer
    if (prisesEnChaineEnCours) {
        if (!mouvement.getDebut().equals(positionDernierePrise)) {
            return false;
        }
        
        List<Mouvement> prisesEnChaine = plateau.getPrisesEnChaine(positionDernierePrise, tourBlanc);
        
        if (prisesEnChaine.isEmpty()) {
            prisesEnChaineEnCours = false;
            return false;
        }
        
        for (Mouvement m : prisesEnChaine) {
            if (m.getDebut().equals(mouvement.getDebut()) && m.getFin().equals(mouvement.getFin())) {
                mouvement.getPrises().clear();
                for (Position prise : m.getPrises()) {
                    mouvement.ajouterPrise(prise);
                }
                return true;
            }
        }
        return false;
    }
    
    // Obtenir les mouvements possibles pour la pièce spécifique
    List<Mouvement> mouvementsPossibles = plateau.getMouvementsPossibles(mouvement.getDebut(), tourBlanc);
    
    for (Mouvement m : mouvementsPossibles) {
        if (m.getDebut().equals(mouvement.getDebut()) && m.getFin().equals(mouvement.getFin())) {
            if (m.contientPrise()) {
                mouvement.getPrises().clear();
                for (Position prise : m.getPrises()) {
                    mouvement.ajouterPrise(prise);
                }
            }
            return true;
        }
    }
    
    return false;
}

    /**
     * Joue le coup
     * @param mouvement Le mouvement à jouer
     */
 public void jouerCoup(Mouvement mouvement) {
        if (verifierEgaliteRepetition()) {
            System.out.println("Égalité par répétition de position !");
            partieTerminee = true;
            return;
        }

        String notation = genererNotationCoup(mouvement);
        historiqueCoups.add(notation);
        
        if (!estMouvementAutorise(mouvement)) {
            System.out.println("Mouvement non autorisé!");
            return;
        }

        boolean contientPrise = mouvement.contientPrise();
        Position nouvellePosition = plateau.jouerCoup(mouvement, tourBlanc);
        Piece piece = plateau.getPiece(nouvellePosition);

        // Gestion promotion
        boolean promotion = false;
        if ((piece.getType() == TypePiece.DAME_BLANCHE && mouvement.getFin().getLigne() == 0) ||
            (piece.getType() == TypePiece.DAME_NOIRE && mouvement.getFin().getLigne() == 7)) {
            System.out.println("Promotion! Un pion est devenu une dame.");
            promotion = true;
        }

        // Mise à jour compteur
        if (contientPrise) coupsSansPrise = 0;
        else coupsSansPrise = promotion ? 0 : coupsSansPrise + 1;

        // Gestion prise en chaîne
        if (contientPrise) {
            positionDernierePrise = nouvellePosition;
            prisesEnChaineEnCours = !plateau.getPrisesEnChaine(nouvellePosition, tourBlanc).isEmpty();
            if (!prisesEnChaineEnCours) changerJoueur();
        } else {
            changerJoueur();
        }

        verifierFinPartie();
    }

    private void changerJoueur() {
        tourBlanc = !tourBlanc;
        prisesEnChaineEnCours = false;
        positionDernierePrise = null;
    }

    private void verifierFinPartie() {
        if (coupsSansPrise >= MAX_COUPS_SANS_PRISE) {
            partieTerminee = true;
            System.out.println("Égalité! " + MAX_COUPS_SANS_PRISE + " coups sans prise.");
            return;
        }

        partieTerminee = plateau.estPartieTerminee();
        if (partieTerminee) {
            int[] compteur = plateau.compterPieces();
            if (compteur[0] == 0) System.out.println("Les Noirs ont gagné!");
            else if (compteur[1] == 0) System.out.println("Les Blancs ont gagné!");
            else if (plateau.getMouvementsPossibles(true).isEmpty()) 
                System.out.println("Les Blancs n'ont plus de mouvements possibles. Les Noirs ont gagné!");
            else 
                System.out.println("Les Noirs n'ont plus de mouvements possibles. Les Blancs ont gagné!");
        }
    }

    // Méthode pour vérifier la répétition de position
    private boolean verifierEgaliteRepetition() {
        String etatActuel = serialiserEtat();
        
        // Compter les occurrences dans les 15 derniers coups
        int limite = Math.min(historiquesEtats.size(), 15);
        int repetitions = 0;
        
        for (int i = historiquesEtats.size() - 1; i >= Math.max(0, historiquesEtats.size() - limite); i--) {
            if (historiquesEtats.get(i).equals(etatActuel)) {
                repetitions++;
                if (repetitions >= 3) return true;
            }
        }
        
        historiquesEtats.add(etatActuel);
        return false;
    }

    private String serialiserEtat() {
        StringBuilder sb = new StringBuilder();
        
        // Sérialisation du plateau
        for (int i = 0; i < Plateau.getTaille(); i++) {
            for (int j = 0; j < Plateau.getTaille(); j++) {
                Piece p = plateau.getPiece(new Position(i, j));
                sb.append(p.estBlanc() ? 'B' : p.estNoir() ? 'N' : 'V');
                if (p.estDame()) sb.append('D');
            }
        }
        
        // Ajout du tour actuel
        sb.append(tourBlanc ? 'T' : 'F');
        return sb.toString();
    }


}   
public class JeuDeDames {
    private static Scanner scanner = new Scanner(System.in);
    
    // Add this method
    /**
     * Convertit une position en notation d'échecs (ex: A3)
     * @param position La position à convertir
     * @return La notation d'échecs correspondante
     */
    public  static String positionToChessNotation(Position position) {
        char colonne = (char) ('A' + position.getColonne());
        int ligne = position.getLigne() + 1;
        return String.valueOf(colonne) + ligne;
    }
    
    // Add this method
    /**
     * Gère la sélection d'un mouvement dans la liste des mouvements possibles
     * @param jeu Le jeu en cours
     * @param moves La liste des mouvements possibles
     * @param scanner Le scanner pour lire l'entrée utilisateur
     */
   // Remplacer la méthode handleMoveSelection par cette version améliorée
private static void handleMoveSelection(Jeu jeu, List<Mouvement> moves, Scanner scanner) {
    if (moves.isEmpty()) {
        System.out.println("Aucun mouvement possible pour cette pièce.");
        System.out.println("Raison : " + getReasonForNoMoves(jeu));
        return;
    }
    
    System.out.print("Sélectionnez un mouvement (numéro) ou 'r' pour revenir: ");
    String input = scanner.nextLine().trim();
    
    if (input.equalsIgnoreCase("R")) {
        System.out.println("Retour à la sélection de pièce.");
        return;
    }
    
    try {
        int moveIndex = Integer.parseInt(input) - 1;
        if (moveIndex >= 0 && moveIndex < moves.size()) {
            Mouvement selectedMove = moves.get(moveIndex);
            if (jeu.estMouvementAutorise(selectedMove)) {
                jeu.jouerCoup(selectedMove);
            } else {
                System.out.println("Mouvement non autorisé. Raison : " + 
                    getMoveRejectionReason(jeu, selectedMove));
            }
        } else {
            System.out.println("Numéro de mouvement invalide. Veuillez choisir entre 1 et " + moves.size());
        }
    } catch (NumberFormatException e) {
        System.out.println("Entrée invalide. Veuillez entrer un numéro de mouvement ou 'r' pour revenir.");
    }
}


// Nouvelle méthode pour expliquer pourquoi il n'y a pas de mouvements
private static String getReasonForNoMoves(Jeu jeu) {
    Position lastPos = jeu.getPositionDernierePrise();
    if (jeu.estPrisesEnChaineEnCours() && lastPos != null) {
        Piece p = jeu.getPlateau().getPiece(lastPos);
        if (p != null && p.estDame()) {
            return "La dame doit continuer sa prise en chaîne mais aucune prise supplémentaire n'est possible.";
        } else {
            return "Le pion doit continuer sa prise en chaîne mais aucune prise supplémentaire n'est possible.";
        }
    }
    
    return "Aucun déplacement ou prise possible avec cette pièce.";
}

// Nouvelle méthode pour expliquer pourquoi un mouvement est rejeté
private static String getMoveRejectionReason(Jeu jeu, Mouvement mouvement) {
    if (jeu.estPrisesEnChaineEnCours()) {
        if (!mouvement.getDebut().equals(jeu.getPositionDernierePrise())) {
            return "Vous devez continuer la prise en chaîne avec la pièce en " + 
                   positionToChessNotation(jeu.getPositionDernierePrise());
        }
        
        if (!mouvement.contientPrise()) {
            return "Vous devez effectuer une prise lorsque c'est possible.";
        }
    }
    
    List<Mouvement> possibleMoves = jeu.getPlateau().getMouvementsPossibles(mouvement.getDebut(), jeu.estTourBlanc());
    boolean priseObligatoire = possibleMoves.stream().anyMatch(Mouvement::contientPrise);
    
    if (priseObligatoire && !mouvement.contientPrise()) {
        return "Une prise est obligatoire lorsque disponible.";
    }
    
    return "Mouvement non conforme aux règles du jeu de dames.";
} 
   public static void main(String[] args) {
   
    Jeu jeu = new Jeu();
    boolean quitter = false;
    Scanner scanner = new Scanner(System.in);
    
    System.out.println("Bienvenue au jeu de Dames!");
    System.out.println("Les pions blancs sont représentés par 'W'");
    System.out.println("Les pions noirs sont représentés par 'B'");
    System.out.println("Pour jouer, sélectionnez une pièce puis choisissez un mouvement");
    System.out.println("Tapez 'q' pour quitter la partie (abandon)");
    
    while (!quitter && !jeu.estPartieTerminee()) {
        int[] compteur = jeu.getPlateau().compterPieces();
        System.out.println("\nPièces restantes - Blancs: " + compteur[0] + " | Noirs: " + compteur[1]);
        
        // Afficher le plateau sans les mouvements
        System.out.println("\n" + jeu.getPlateau().toString());
        
        System.out.println("\n=== Tour des " + (jeu.estTourBlanc() ? "Blancs (W)" : "Noirs (B)") + " ===");
       
        // Gestion des prises en chaîne
        if (jeu.estPrisesEnChaineEnCours()) {
             System.out.println("Prise en chaine possible avec la pièce en " 
    + JeuDeDames.positionToChessNotation(jeu.getPositionDernierePrise()));
            Position selectedPos = jeu.getPositionDernierePrise();
            List<Mouvement> moves = jeu.getPlateau().getMouvementsPossibles(selectedPos, jeu.estTourBlanc());
            
            // Afficher le plateau avec les mouvements numérotés
            System.out.println("\n" + jeu.getPlateau().toStringWithMoves(moves));
            handleMoveSelection(jeu, moves, scanner);
            continue;
        }
        
        // Tour normal - sélection d'une pièce
        System.out.print("Sélectionnez une pièce (ex: A3) ou 'q' pour quitter: ");
        String input = scanner.nextLine().trim().toUpperCase();
        
        if (input.equalsIgnoreCase("Q")) {
            quitter = true;
            // Déclarer le joueur adverse comme vainqueur en cas d'abandon
            System.out.println("\n" + jeu.getPlateau().toString());
            if (jeu.estTourBlanc()) {
                System.out.println("Les Blancs ont abandonné! Les Noirs sont déclarés vainqueurs!");
            } else {
                System.out.println("Les Noirs ont abandonné! Les Blancs sont déclarés vainqueurs!");
            }
            continue;
        }
        
        try {
            // Convertir l'entrée en position
            int col = input.charAt(0) - 'A';
            int row = Integer.parseInt(input.substring(1)) - 1;
            Position selectedPos = new Position(row, col);
            
            // Vérifier que la position est valide
            if (!selectedPos.estValide()) {
                System.out.println("Position invalide. Réessayez.");
                continue;
            }
            
            // Vérifier que la pièce appartient au joueur actuel
            Piece pieceSelectionnee = jeu.getPlateau().getPiece(selectedPos);
            if (pieceSelectionnee == null || pieceSelectionnee.estVide() || 
                (jeu.estTourBlanc() && !pieceSelectionnee.estBlanc()) || 
                (!jeu.estTourBlanc() && !pieceSelectionnee.estNoir())) {
                System.out.println("Vous ne pouvez pas sélectionner cette pièce.");
                continue;
            }
            
            // Obtenir les mouvements pour la pièce sélectionnée
            List<Mouvement> moves = jeu.getPlateau().getMouvementsPossibles(selectedPos, jeu.estTourBlanc());
            
            if (moves.isEmpty()) {
                System.out.println("Aucun mouvement possible pour cette pièce. Choisissez une autre pièce.");
                continue;
            }
            
            // Afficher le plateau avec les mouvements numérotés
            System.out.println("\n" + jeu.getPlateau().toStringWithMoves(moves));
            handleMoveSelection(jeu, moves, scanner);
            
        } catch (Exception e) {
            System.out.println("Entrée invalide. Format: LettreChiffre (ex: A3)");
        }
    }
    
    // Gestion de fin de jeu (si partie terminée normalement)
    if (jeu.estPartieTerminee() && !quitter) {
        System.out.println("\n" + jeu.getPlateau().toString());
        int[] compteur = jeu.getPlateau().compterPieces();
        
        if (compteur[0] == 0) {
            System.out.println("Les Noirs ont gagné!");
        } else if (compteur[1] == 0) {
            System.out.println("Les Blancs ont gagné!");
        } else if (jeu.getPlateau().getMouvementsPossibles(true).isEmpty()) {
            System.out.println("Les Blancs n'ont plus de mouvements possibles. Les Noirs ont gagné!");
        } else {
            System.out.println("Les Noirs n'ont plus de mouvements possibles. Les Blancs ont gagné!");
        }
    }
    
    scanner.close();
}
}