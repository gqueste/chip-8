package chip8;

/**
 * Classe des pixels de la CHIP 8 </br>
 * Largeur fixe à 8 pixels </br>
 * @author Quentin
 *
 */
// Classe pas vraiment utile pour le moment mais quand on va passer en Super et Mega elle risque d'être pratique
public class PixelChip8{
	int largeur = 8;
	int hauteur;
	boolean actif;
	
	/**
	 * Constructeur par défaut </br>
	 * longueur à 10 </br>
	 * actif à false
	 */
	public PixelChip8(){
		hauteur = 10;
		actif = false;
	}
	
	/**
	 * Constructeur créant un pixel de taille standard mais en choisissant son statut
	 * @param actif
	 */
	public PixelChip8(boolean actif){
		hauteur = 10;
		actif = this.actif;
	}
	
	/**
	 * Constructeur permettant de choisir la hauteur du pixel et son état
	 * @param hauteur
	 * @param actif
	 */
	public PixelChip8(int hauteur, boolean actif){
		this.hauteur = hauteur;
		this.actif = actif;
	}

	public int getLargeur() {
		return largeur;
	}

	public void setLargeur(int largeur) {
		this.largeur = largeur;
	}

	public int getHauteur() {
		return hauteur;
	}

	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}
	
	
}
