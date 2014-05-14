package chip8;

public class Chip8 {

	private int PC;
	private int SP;
	private String I;
	private int[] V;
	private byte[][] display;
	
	/**
	 * Constructeur
	 */
	public Chip8(){
		PC = 0;
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
		
		
		switch (opcode) {
		case 0x0000 :
			//TODO Appel d'un programme une Addresse
			break;
		/*case 0x00E0:
			this.display = initDisplay();
			this.PC += 2;
			break;*/
		case 0x1000:
			//TODO saut à une addresse
			break;
		case 0x2000:
			//TODO Appel d'une sous routine
		case 0x3000:
			//TODO Skip si egale
			break;
		case 0x4000:
			//TODO Skip si pas egale
			break;
		case 0x5000:
			//TODO Skip si egale
			break;
		case 0x6000:
			//TODO Set de x
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
			//TODO
			break;
		case 0xC000:
			//TODO
			break;
		case 0xD000:
			//TODO
			break;
		case 0xE000:
			//TODO
			break;
		case 0xF000:
			//TODO
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

	public int getSP() {
		return SP;
	}

	public void setSP(int sP) {
		SP = sP;
	}

	public String getI() {
		return I;
	}

	public void setI(String i) {
		I = i;
	}

	public int[] getV() {
		return V;
	}

	public void setV(int[] v) {
		V = v;
	}
	
	public byte[][] getDisplay() {
		return display;
	}

	public void setDisplay(byte[][] display) {
		this.display = display;
	}

}
