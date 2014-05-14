package chip8;

public class Chip8 {

	private int PC;
	private String SP;
	private String I;
	private String[] V;
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
		switch (opcode) {
		case 0x0000 :
			//TODO Appel d'un programme une Addresse
			break;
		case 0x00E0:
			this.display = initDisplay();
			this.PC += 2;
			break;
		case 0x00EE:
			//TODO Retourn d'une sous routine
			break;
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
		case 0x5000:
			//TODO Skip si egale
		case 0x6000:
			//TODO Set de x
		case 0x7000:
			//TODO Add to x
		case 0x8000:
			//TODO setter
		case 0x8001:
			//TODO setter
		case 0x8002:
			//TODO setter
		case 0x8003:
			//TODO setter
		case 0x8004:
			//TODO addition
		case 0x8005:
			//TODO soustraction
		case 0x8006:
			//TODO Shift
		case 0x8007:
			//TODO Setter
		case 0x800E:
			//TODO Shift
		case 0x9000:
			//TODO Skips
		case 0xA000:
			//TODO
		case 0xB000:
			//TODO
		case 0xC000:
			//TODO
		case 0xD000:
			//TODO
		case 0xE09E:
			//TODO
		case 0xE0A1:
			//TODO
		case 0xF007:
			//TODO
		case 0xF00A:
			//TODO
		case 0xF015:
			//TODO
		case 0xF018:
			//TODO
		case 0xF01E:
			//TODO
		case 0xF029:
			//TODO
		case 0xF033:
			//TODO
		case 0xF055:
			//TODO
		case 0xF065:
			//TODO
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

	public String getSP() {
		return SP;
	}

	public void setSP(String sP) {
		SP = sP;
	}

	public String getI() {
		return I;
	}

	public void setI(String i) {
		I = i;
	}

	public String[] getV() {
		return V;
	}

	public void setV(String[] v) {
		V = v;
	}
	
	public byte[][] getDisplay() {
		return display;
	}

	public void setDisplay(byte[][] display) {
		this.display = display;
	}

}
