package chip8;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ToucheListener implements KeyListener {
	private byte keyHexa = -1;

	public ToucheListener(){
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

	}

	public byte getInput() {
		// Method pour retourner la valeur de keyHexa
		// Ne pas oublier de reset la valeur de keyHexa
		byte valeur = keyHexa; 
		keyHexa = -1;
		return valeur;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//Switch pour récupérer l'input
				// on fait un lowercase comme a pas besoin de gérer la casse
				int keyCode = e.getKeyCode();
				System.out.println("Touche appuy�e : "+keyCode);
				switch(e.getKeyCode()) {
				case KeyEvent.VK_1:
					keyHexa = 0x01;
					break;
				case KeyEvent.VK_2:
					keyHexa = 0x02;
					break;
				case KeyEvent.VK_3:
					keyHexa = 0x03;
					break;
				case KeyEvent.VK_4:
					keyHexa = 0x0C;
					break;
				case KeyEvent.VK_A:
					keyHexa = 0x04;
					break;
				case KeyEvent.VK_Z:
					keyHexa = 0x05;
					break;
				case KeyEvent.VK_E:
					keyHexa = 0x06;
					break;
				case KeyEvent.VK_R:
					keyHexa = 0x0D;
					break;
				case KeyEvent.VK_Q:
					keyHexa = 0x07;
					break;
				case KeyEvent.VK_S:
					keyHexa = 0x08;
					break;
				case KeyEvent.VK_D:
					keyHexa = 0x09;
					break;
				case KeyEvent.VK_F:
					keyHexa = 0x0E;
					break;
				case KeyEvent.VK_W:
					keyHexa = 0x0A;
					break;
				case KeyEvent.VK_X:
					keyHexa = 0x00;
					break;
				case KeyEvent.VK_C:
					keyHexa = 0x0B;
					break;
				case KeyEvent.VK_V:
					keyHexa = 0x0F;
					break;
				}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private int renvoie(int i) {
		return i;
		
	}

}
