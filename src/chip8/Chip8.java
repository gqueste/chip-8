package chip8;

public class Chip8 {

	private int PC;
	private byte SP;
	private short I;
	private byte[] V;
	private byte[][] display;
	private int[] stack;
	
	/**
	 * Constructeur
	 */
	public Chip8(){
		PC = 0;
		this.SP = 0;
		this.setStack(new int[16]);
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
				//TODO Appel d'un programme une Addresse ??
				System.out.println("Instruction non reconnue");
			}
			else {
				if(opcode == 0x00E0) {
					//clear the screen
					this.display = initDisplay();
					this.PC += 2;
				}
				else if (opcode == 0x00EE){
					//returns from a subroutine
					this.SP --;
					this.PC = this.stack[this.SP];
					this.PC += 2;
				}
			}
			break;

		case 0x1000:
			// saut �une addresse
			PC = (opcode & 0x0FFF);			
			break;
			
		case 0x2000:
			//TODO Appel d'une sous routine
			break;
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
