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
		this.V = new byte[16];
		display = initDisplay();
	}

	/**
	 * Initialiser l'écran
	 * @return
	 */
	private byte[][] initDisplay() {
		byte[][] screen = new byte[64][32];
		for(int i=0;i<64;i++){
			for(int j=0;j<32;j++){
				screen[i][j] = 0;
			}
		}
		return screen;
	}

	/**
	 * Lecture des instructions
	 * @param opcode
	 */
	public void opcode(int opcode){
		
		int x = ((opcode & 0x0F00) >> 8);
		int y = ((opcode & 0x00F0) >> 4);
		int kk = (opcode & 0x00FF);
		int nnn = (opcode & 0x0FFF);
		
		int first = opcode & 0xF000;
		int last = opcode & 0x000F;
		
		
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
					this.PC = this.stack[this.SP];
					this.SP --;
					this.PC += 2;
				}
			}
			break;

		case 0x1000:
			// saut à une addresse
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
				PC += 4;
			}
			else {
				PC += 2;
			}
			break;
			
		case 0x4000:
			//Skip si pas egale
			if(V[x] != (byte)kk) {
				PC += 4;
			}
			else {
				PC += 2;
			}
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
			byte vx = this.V[x];
			byte vy = this.V[y];
			if(last == 0x0000){
				this.V[x] = vy;
			}
			else if(last == 0x0001){
				this.V[x] = (byte) (vx | vy);
			}
			else if(last == 0x0002){
				this.V[x] = (byte) (vx & vy);
			}
			else if(last == 0x0003){
				this.V[x] = (byte) (vx ^ vy);
			}
			else if(last == 0x0004){
				byte res = (byte) (vx + vy);
				if(res > 255){
					this.V[15] = 1;
				}
				else{
					this.V[15] = 0;
				}
			}
			else if(last == 0x0005){
				if(vx > vy){
					this.V[15] = 1;
				}
				else{
					this.V[15] = 0;
				}
				this.V[x] = (byte) (vx - vy);
			}
			else if(last == 0x0006){
				byte lastOfVX = (byte) (this.V[x] & 0x000F);
				if(lastOfVX == 1){
					this.V[15] = 1;
				}
				else{
					this.V[15] = 0;
				}
			}
			else if(last == 0x0007){
				if(vy > vx){
					this.V[15] = 1;
				}
				else{
					this.V[15] = 0;
				}
				this.V[x] = (byte) (vy - vx);
			}
			else if(last == 0x000E){
				if(vx > 7){
					this.V[15] = 1;
				}
				else{
					this.V[15] = 0;
				}
				this.V[x] *= 2;
			}
			this.PC += 2;
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
