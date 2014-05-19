package chip8;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Touche {
	JFrame frame;
	private char key;
	private byte keyHexa = -1;
	
	public Touche(JFrame frame){
		this.frame = frame;
		
		frame.addKeyListener(new KeyListener() {
			
//			le clavier est :
//			1234
//			AZER
//			QSDF
//			WXCV
//			-------
//			123C
//			456D
//			789E
//			A0BF
			
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				//Switch pour récupérer l'input
				// on fait un lowercase comme a pas besoin de gérer la casse
				key = arg0.getKeyChar();
				System.out.println("Touche appuyée : "+key);
				switch(Character.toLowerCase(key)) {
				case '&':
					keyHexa = 0x01;
					break;
				case 'é':
					keyHexa = 0x02;
					break;
				case '"':
					keyHexa = 0x03;
					break;
				case '\'':
					keyHexa = 0x0C;
					break;
				case 'q':
					keyHexa = 0x04;
					break;
				case 'w':
					keyHexa = 0x05;
					break;
				case 'e':
					keyHexa = 0x06;
					break;
				case 'r':
					keyHexa = 0x0D;
					break;
				case 'a':
					keyHexa = 0x07;
					break;
				case 's':
					keyHexa = 0x08;
					break;
				case 'd':
					keyHexa = 0x09;
					break;
				case 'f':
					keyHexa = 0x0E;
					break;
				case 'z':
					keyHexa = 0x0A;
					break;
				case 'x':
					keyHexa = 0x00;
					break;
				case 'c':
					keyHexa = 0x0B;
					break;
				case 'v':
					keyHexa = 0x0F;
					break;
				}
			}
		});
	}
	
	public byte getInput() {
		// Method pour retourner la valeur de keyHexa
		// Ne pas oublier de reset la valeur de keyHexa
		byte valeur = keyHexa;
		keyHexa = -1;
		return valeur;
	}

}
