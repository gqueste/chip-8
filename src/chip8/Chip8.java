package chip8;

import java.util.Random;

public class Chip8 {

	private int PC, delay_timer, sound_timer, instruction_count;;
	private byte SP,key;
	private short I;
	private byte[] memoire,V;
	private byte[][] display;
	private int[] stack;
	private Random random;
	private Touche input;
	/**
	 * Constructeur
	 */
	public Chip8(){
		PC = 0;
		this.SP = 0;
		this.setStack(new int[16]);
		this.V = new byte[16];
		this.memoire = new byte[4096];
		this.random = new Random(567765);
		display = initDisplay();
	}

	private byte[][] initDisplay() {
		byte[][] screen = new byte[64][32];
		for(int i=0;i<64;i++){
			for(int j=0;j<32;j++){
				screen[i][j] = 0;
			}
		}
		return screen;
	}

	public void opcode(int opcode){

		int x = ((opcode & 0x0F00) >> 8);
		int y = ((opcode & 0x00F0) >> 4);
		int kk = (opcode & 0x00FF);
		int nnn = (opcode & 0x0FFF);

		int first = opcode & 0xF000;


		switch (first) {
		case 0x0000 :
			if(x != 0x0000) {
				//TODO Appel d'un programme une Addresse ?? abandonnÃ© par les interpreter modernes
			}
			else {
				if(opcode == 0x00E0) {
					//clear the screen
					this.display = initDisplay();
					this.PC ++;
				}
				else if (opcode == 0x00EE){
					//returns from a subroutine
					this.PC = this.stack[this.SP];
					this.SP --;
					this.PC ++;
				}
			}
			break;

		case 0x1000:
			// saut Ã  une addresse
			PC = (opcode & 0x0FFF);			
			break;

		case 0x2000:
			//Appel d'une sous routine
			SP++;
			stack[SP] = PC;
			PC = (short)(opcode & 0x0FFF);
			break;

		case 0x3000:
			//Skip si egale
			if(V[x] == (byte)kk) {
				PC += 2;
			}
			else {
				PC ++;
			}
			break;

		case 0x4000:
			//Skip si pas egale
			if(V[x] != (byte)kk) {
				PC += 2;
			}
			else {
				PC ++;
			}
			break;

		case 0x5000:
			if((opcode & 0x000F) == 0x0000) {
				//Skip si Vx = Vy
				if(V[x] == V[y]) {
					PC += 2;
				}
				else {
					PC ++;
				}
			}
			break;

		case 0x6000:
			//Set de Vx
			V[x] = (byte) kk;
			PC ++;
			break;

		case 0x7000:
			//TODO Add to x
			break;
		case 0x8000:
			//TODO setter
			break;
		case 0x9000:
			//TODO Skips
			break;
		case 0xA000:
			//TODO
			break;
		case 0xB000:
			/**
			 * BNNN : Instruction pour sauter à l'adresse NNN depuis le registre v0 
			 */
			PC = nnn+V[0];
			break;
		case 0xC000:
			/**
			 * CXKK Generer un byte aléatoire pour le registre Vx et y ajouter KK
			 */
			V[x] = (byte)(random.nextInt(255)&kk);
			PC += 2;
			break;
		case 0xD000:
			/**
			 * DXYN : Affichage des sprites
			 */
			// Nombre de Byte verticaux
			int nbByte = (opcode & 0xF);
			// Flag de collision
			V[0xF] = 0;
			//place de X et Y 
			int xPlace = (V[x]&0xFF);
			int yPlace = (V[y]&0xFF);

			//Boucle d'affichage
			for(int axeY = 0; axeY < nbByte; axeY++){
				int pixel = memoire[I+axeY];
				for(int axeX = 0 ; axeX<8 ; axeX++){
					//On vérifie que le pixel n'est pas hors de "l ecran"
					if((pixel & (0x80>>axeX)) != 0 ){
						if((xPlace & axeX)>63){
							continue;
						}
						if((yPlace & axeY)>31){
							continue;
						}
						if(display[xPlace+axeX][yPlace+axeY] == 1){
							V[0xF] = 1;
						}
						display[xPlace+axeX][yPlace+axeY] ^= 1;
					}
				}
			}
			PC +=2;
			break;
		case 0xE000:
			/**
			 * Instruction commençant par un E
			 */
			// on récupère le reste de l'instruction
			if(kk == 0x9E){
				//On skip si la bonne touche est pressée
				key = input.getInput();
				if(V[x]==key){
					PC+=4;
				}else{
					PC+=2;
				}
			}else if(kk == 0xA1){
				//On skip si la bonne touche n est pas pressée
				key = input.getInput();
				if(V[x]==key){
					PC+=2;
				}else{
					PC+=4;
				}
			}
			break;
		case 0xF000:
			/**
			 * Toutes les instructions qui commence par F
			 */
			kk = (short)(opcode & 0x00FF);
			switch(kk){
			case 0x07:
				//On set la valeur du Vx à celle du delay_timer
				V[x] = (byte)delay_timer;
				break;
			case 0x0A:
				//On récupère une valeur d'input et on la stocke dans Vx
				//Petite boucle pour éviter une boucle infinie qui rend impossible la récupération de l'input
				do{
					key = input.getInput();
					try {
						Thread.sleep(10);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				} while(key == -1);

				V[x] = key;
				break;
			case 0x15:
				// On set le delay_timer à Vx
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	//#############################################################################################
	// Getter and Setter
	//#############################################################################################
	public int getPC() {
		return PC;
	}

	public void setPC(int pC) {
		PC = pC;
	}

	public byte getSP() {
		return SP;
	}

	public void setSP(byte sP) {
		SP = sP;
	}

	public short getI() {
		return I;
	}

	public void setI(short i) {
		I = i;
	}

	public byte[] getV() {
		return V;
	}

	public void setV(byte[] v) {
		V = v;
	}

	public byte[][] getDisplay() {
		return display;
	}

	public void setDisplay(byte[][] display) {
		this.display = display;
	}

	public int[] getStack() {
		return stack;
	}

	public void setStack(int[] stack) {
		this.stack = stack;
	}

}
