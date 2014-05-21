package chip8;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * Classe qui sert de pilote graphique
 * @author Quentin, Gwenaël, Gabriel
 *
 */

public class Ecran extends JComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color couleurPixel;
	private byte[][] ramGraphique;
	
	public Ecran (byte[][] ramGraphique){
		this.ramGraphique = ramGraphique;
		//Affichage Monochrome donc blanc et noir
		couleurPixel = Color.WHITE;
	}
	
	public void paintComponent (Graphics graph){
		// Affichage 64 * 32 pixels Chip8
		// Déclaration des variables
		// Affichage 2D
		Graphics2D pix = (Graphics2D) graph;
		pix.setColor(couleurPixel);
		Rectangle2D rect;
//		PixelChip8[][] pixels = new PixelChip8[64][32];
		for(int x = 0 ; x < 64 ; x++){
			for(int y = 0 ; y < 32 ; y++){
				if(ramGraphique[x][y] == 1){
//					PixelChip8 pixel = new PixelChip8(true);
//					pixels[x][y] = pixel;
//					int xMul = pixel.getLargeur();
//					int yMul = pixel.getHauteur();
					rect = new Rectangle2D.Double((double)x*8,(double)y*8, (double)8, (double)8);
					pix.fill(rect);
					pix.draw(rect);
				}
			}
		}
	}
}
