package chip8;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * Classe qui sert de pilote graphique
 * @author Quentin, GwenaÃ«l, Gabriel
 *
 */

public class Ecran extends JComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color couleurPixel;
	private byte[][] ramGraphique;
	private int nbPixelsAxeX;
	private int nbPixelsAxeY;
	
	public Ecran (byte[][] ramGraphique,int nbPixelsAxeX,int nbPixelsAxeY){
		this.ramGraphique = ramGraphique;
		this.nbPixelsAxeX = nbPixelsAxeX;
		this.nbPixelsAxeY = nbPixelsAxeY;
		//Affichage Monochrome donc blanc et noir
		couleurPixel = Color.WHITE;
	}
	
	public void paintComponent (Graphics graph){
		// Affichage 64 * 32 pixels Chip8 ou 128 * 64 pixels en SChip8
		// Déclaration des variables
		// Affichage 2D
		Graphics2D pix = (Graphics2D) graph;
		pix.setColor(couleurPixel);
//		PixelChip8[][] pixels = new PixelChip8[64][32];
		for(int x = 0 ; x < nbPixelsAxeX ; ++x){
			for(int y = 0 ; y < nbPixelsAxeY ; ++y){
				if(ramGraphique[x][y] == 1){
//					PixelChip8 pixel = new PixelChip8(true);
//					pixels[x][y] = pixel;
//					int xMul = pixel.getLargeur();
//					int yMul = pixel.getHauteur();
//					rect = new Rectangle2D.Double();
					pix.fillRect(x*8,y*8,8,8);
//					pix.draw(rect);
				}
			}
		}
	}
}
