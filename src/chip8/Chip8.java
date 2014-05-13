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
		case 0x00E0:
			this.display = initDisplay();
			this.PC += 2;
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
