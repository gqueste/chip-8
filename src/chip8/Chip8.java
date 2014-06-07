package chip8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Chip8 {

	//private final int LIMIT_NUMBER-INSTRUCTION;
	
	public final static double DEFAULT_INSTRUCTIONS_RATE = 20;

	private int PC, delay_timer, sound_timer;
	private byte SP,key;
	private short I;
	private byte[] V, memory,rom, RPLUserFlag;
	private byte[][] display;
	private int[] stack;
	private Random random;
	private ToucheListener input;
	// Attributs relatifs au temps et rythme d'interprétation des instructions
	private double rate, per, allowance;
	private long current, passed, last_checked;
	private AudioStream lecteur;
	private boolean sChipMode = false;
	private boolean cycle = true;
	private Ecran ecranJeu;
	
	private int[] keys;

	private int nbPixelsAxeYChip8 = 32;
	private int nbPixelsAxeXChip8 = 64;

	/**
	 * Constructeur pour les tests où la rom n'est pas nécessaire
	 */
	public Chip8(){
		this.PC = 0;
		this.SP = 0;
		this.setStack(new int[16]);
		this.V = new byte[16];
		this.RPLUserFlag = new byte[16];
		this.random = new Random(567765);
		this.memory = new byte[4096];
		System.out.println("Load");
		// 14 instructions pour 100 ms
		this.setRate(14);
		per = 100;
		allowance = rate;
		last_checked = System.currentTimeMillis();

		display = new byte[nbPixelsAxeXChip8][nbPixelsAxeXChip8];
		for(int i=0;i<nbPixelsAxeXChip8;i++){
			for(int j=0;j<nbPixelsAxeYChip8;j++){
				this.display[i][j] = 0;
			}
		}
	}

	/**
	 * Constructeur pour l'émulateur
	 * @param rom
	 * @param touche
	 */
	public Chip8(File rom, ToucheListener touche) {
		this.I = 0x0;
		this.PC = 0;
		this.SP = 0;
		this.stack = new int[16];
		this.V = new byte[16];
		this.random = new Random(567765);
		this.memory = new byte[4096];
		this.keys = new int[16];
		loadMemory();
		// 20 instructions pour 100 ms
		this.setRate(DEFAULT_INSTRUCTIONS_RATE);
		per = 100;
		allowance = rate;
		last_checked = System.currentTimeMillis();

		display = new byte[nbPixelsAxeXChip8][nbPixelsAxeXChip8];
		for(int i=0;i<64;i++){
			for(int j=0;j<32;j++){
				this.display[i][j] = 0;
			}
		}

		this.input = touche;
		this.key = -1;
		System.out.println("loadRom");
		loadRom(rom);
		System.out.println("lu");
	}

	private void loadSound() {
		try {
			this.lecteur=new AudioStream(new FileInputStream("src/tone.wav"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Charge la rom en mémoire
	 * @param romPath
	 */
	public void loadRom(File rom){
		InputStream stream;
		try{
			stream = new FileInputStream(rom);
			int tmp=0,taille = 0 ;
			for(int x = 0; tmp !=-1; x++){
				tmp=stream.read();
				if(tmp==-1){
					taille = x;
				}
			}
			stream.close();
			this.rom = new byte[taille];
			stream = new FileInputStream(rom);
			stream.read(this.rom,0,taille);
			System.arraycopy(this.rom, 0, this.getMemory(), 0x200, taille);
			stream.close();
			this.lire();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Charge la mémoire avec les font sprites
	 * Place le PC à 0x200
	 */
	public void loadMemory() {

		// Set the font sprites
		int[] font = {
				0xF0, 0x90, 0x90, 0x90, 0xF0,
				0x20, 0x60, 0x20, 0x20, 0x70,
				0xF0, 0x10, 0xF0, 0x80, 0xF0,
				0xF0, 0x10, 0xF0, 0x10, 0xF0,
				0x90, 0x90, 0xF0, 0x10, 0x10,
				0xF0, 0x80, 0xF0, 0x10, 0xF0,
				0xF0, 0x80, 0xF0, 0x90, 0xF0,
				0xF0, 0x10, 0x20, 0x40, 0x40,
				0xF0, 0x90, 0xF0, 0x90, 0xF0,
				0xF0, 0x90, 0xF0, 0x10, 0xF0,
				0xF0, 0x90, 0xF0, 0x90, 0x90,
				0xE0, 0x90, 0xE0, 0x90, 0xE0,
				0xF0, 0x80, 0x80, 0x80, 0xF0,
				0xE0, 0x90, 0x90, 0x90, 0xE0,
				0xF0, 0x80, 0xF0, 0x80, 0xF0,
				0xF0, 0x80, 0xF0, 0x80, 0x80
		};
		for(int x = 0; x < font.length; x++) {
			memory[x] = (byte)font[x];
		}
		PC = 0x200;		
	}


	/**
	 * Détermine s'il y a suffisamment de temps pour interpréter
	 * une nouvelle opération
	 * @return boolean, True si une nouvelle instruction ne peut pas être lue
	 */
	public boolean limitationNbreOperations() {
		current = System.currentTimeMillis();
		passed = current - last_checked;
		last_checked = current;
		allowance += passed * (rate / per);

		if(allowance > rate)
			allowance = rate;

		if(allowance < 1.0) {
			return true;
		}
		else
		{
			allowance -= 1.0;
			return false;
		}
	}

	/**
	 * Controle l'exécution de l'interpreter
	 * Limite le nombre d'exécutions lues
	 * Récupère le code depuis la mémoire
	 * Met à jour les delay et sound timers
	 */
	public void lire() {
		if(limitationNbreOperations()) {
			return;
		}

		int msb, lsb, total;

		msb = (((int)memory[PC]) & 0xFF);
		lsb = (((int)memory[PC + 1]) & 0xFF);
		total = ((msb << 8) | lsb);
		int opcodeRecupere = total;
		opcode(opcodeRecupere);

		if(delay_timer > 0){
			delay_timer--;
		}
		if(sound_timer > 0) {
			if(sound_timer == 1) {
				loadSound();
				AudioPlayer.player.start(this.lecteur);
			}
			sound_timer--;
		}
	}

	/**
	 * Interprète le code d'opération reçu
	 * @param opcode, int
	 */
	public void opcode(int opcode){

		int x = ((opcode & 0x0F00) >> 8);
		int y = ((opcode & 0x00F0) >> 4);
		int kk = (opcode & 0xFF);
		int nnn = (opcode & 0xFFF);

		int first = opcode & 0xF000;
		int last = opcode & 0x000F;

		//		System.out.println("Opcode : " +  String.format("0x%4s", Integer.toHexString(opcode)).replace(' ', '0'));
		switch (first) {
		case 0x0000 :
			if(x != 0x0000) {
				System.out.println("Opcode non reconnu : " + String.format("%02X", opcode));
			}
			else {
				if(opcode == 0x00E0) {
					//clear the screen
					for(int i=0;i<64;i++){
						for(int j=0;j<32;j++){
							this.display[i][j] = 0;
						}
					}
					this.PC += 2;
				}
				else if (opcode == 0x00EE){
					//returns from a subroutine
					this.SP --;
					this.PC = this.stack[this.SP];
					this.PC += 2;
				}
				else if (opcode == 0x00C0){
					scrollDown(last);
					this.PC += 2;
				}
				else if (opcode == 0x00FB){
					if(sChipMode){
						scrollRight();
					}
					this.PC +=2;
				}
				else if (opcode == 0x00FC){
					if(sChipMode){
						scrollLeft();
					}
					this.PC +=2;
				}
				else if (opcode == 0x00FD){
					this.setCycle(false);
				}
				else if (opcode == 0x00FE){
					sChipMode=false;
					this.PC +=2;
				}
				else if (opcode == 0x00FF){
					sChipMode=true;
					if(issChipMode()){
						System.out.println("SCmode");
						nbPixelsAxeXChip8 = 128;
						nbPixelsAxeYChip8 = 64;
						display = new byte[nbPixelsAxeXChip8][nbPixelsAxeYChip8];
						for(int i=0;i<nbPixelsAxeXChip8;i++){
							for(int j=0;j<nbPixelsAxeYChip8;j++){
								this.display[i][j] = 0;
							}
						}
					}
					this.PC +=2;
				}
			}
			break;

		case 0x1000:
			// Jump to adress
			PC = (short)(opcode & 0x0FFF);			
			break;

		case 0x2000:
			// Call a subroutine
			stack[SP] = PC;
			SP++;
			PC = (short)(opcode & 0x0FFF);
			break;

		case 0x3000:
			//Skip if equal
			if(V[x] == (byte)kk) {
				PC += 4;
			}
			else {
				PC +=2;
			}
			break;

		case 0x4000:
			//Skip if not equal
			if(V[x] != (byte)kk) {
				PC += 4;
			}
			else {
				PC +=2;
			}
			break;

		case 0x5000:
			if(V[x] == V[y]) {
				PC += 4;
			}
			else {
				PC += 2;
			}
			break;

		case 0x6000:
			//Set of Vx
			V[x] = (byte) kk;
			PC += 2;
			break;

		case 0x7000:
			//Add to x
			V[x] = (byte)(V[x] + kk);
			PC += 2;
			break;

		case 0x8000:
		{
			int mask = (opcode & 0xF);			
			switch(mask) {
			case 0x0:
			{
				// Set Vx = Vy.
				V[x] = V[y];

				break;
			}

			case 0x1:
			{
				// Set Vx = Vx OR Vy.

				V[x] = (byte)(V[x] | V[y]);

				break;
			}

			case 0x2:
			{
				/*
				 * Set Vx = Vx AND Vy.
				 */

				V[x] = (byte)(V[x] & V[y]);				
				break;
			}

			case 0x3:
			{
				/*
				 * Set Vx = Vx XOR Vy.
				 */

				V[x] ^= V[y];

				break;
			}

			case 0x4:
			{
				/*
				 * Set Vx = Vx + Vy, set VF = carry.
				 */

				V[x] = (byte)(V[x] + V[y]);

				if((V[x] + V[y]) > 255)
					V[0xF] = 1;
				else
					V[0xF] = 0;

				break;
			}

			case 0x5:
			{
				/*
				 * Set Vx = Vx - Vy, set VF = NOT borrow.
				 */

				if(V[x] > V[y])
					V[0xF] = 1;
				else
					V[0xF] = 0;

				V[x] = (byte)(V[x] - V[y]);

				break;
			}

			case 0x6:
			{
				/*
				 * Set Vx = Vx SHR 1.
				 */

				// Set carry flag if LSb of Vx is set
				if((V[x] & 0x1) == 1) {
					V[0xF] = 1;
				}
				else {
					V[0xF] = 0;
				}

				V[x] >>= 1;

						break;
			}

			case 0x7:
			{
				/*
				 * Set Vx = Vy - Vx, set VF = NOT borrow.
				 */

				V[0xF] = (V[y] > V[x]) ? (byte)1 : 0;

				V[x] = (byte)(V[y] - V[x]);

				break;
			}

			case 0xE:
			{
				/*
				 * Set Vx = Vx SHL 1.
				 */

				// Set flag register if MSb of Vx is set
				V[0xF] = (((V[x] & 0x80) >> 7) == 1) ? (byte)1 : 0;

				V[x] <<= 1;

				break;
			}

			// End masking
			}

			PC += 2;
			break;
		}
		case 0x9000:
			//Skips
			if(this.V[x] == this.V[y]){
				this.PC += 2;
			}
			else{
				this.PC += 4;
			}
			break;
		case 0xA000:
			//Set I
			this.I = (short) nnn;
			this.PC += 2;
			break;
		case 0xB000:
			/**
			 * BNNN : Instruction pour sauter à l'adresse NNN depuis le registre v0 
			 */
			PC = (short)(nnn + V[0]);
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
			int nbByte = (opcode & 0x000F);
			// Flag de collision
			V[0xF] = 0;
			//place de X et Y 
			int xPlace = (V[x]&0xFF);
			int yPlace = (V[y]&0xFF);

			//Boucle d'affichage
			int valeurX = 0;
			if(!sChipMode){
				valeurX = 8;
			}else{
				valeurX = 16;
			}
				for(short axeY = 0; axeY < nbByte; axeY++){
					short pixel = memory[I+axeY];
					//				System.out.println(String.format("pixel : %x, %x", pixel, I+axeY));
					for(short axeX = 0 ; axeX<valeurX ; axeX++){
						//On vérifie que le pixel n'est pas hors de "l ecran"
						if((pixel & (0x80>>axeX)) > 0 ){
							if((xPlace + axeX)>nbPixelsAxeXChip8){
								continue;
							}
							if((yPlace + axeY)>nbPixelsAxeYChip8){
								continue;
							}
							if(display[xPlace+axeX][yPlace+axeY] == 1){
								V[0xF] = 1;
							}
							display[xPlace+axeX][yPlace+axeY] ^= 1;
						}
						//					System.out.println(String.format("pixel : %x, %x", pixel, xPlace+axeX));
					}
				}
			if(ecranJeu!=null){
				ecranJeu.repaint();
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
				if (this.keys[this.V[x]] == 1){
					System.out.println("input attendus effectué EX9E");
					PC+=4;
				}else{
					PC+=2;
				}
			}else if(kk == 0xA1){
				//FIXME problème de latence
				//On skip si la bonne touche n est pas pressée
				key = input.getInput();
				if(this.keys[this.V[x]] == 0){
					PC+=4;
				}else{
					System.out.println("input attendus effectué EXA1");
					PC+=2;
				}
			}
			break;
		case 0xF000:
			/**
			 * Toutes les instructions qui commence par F
			 */
			switch(kk){
			case 0x07:
				//On set la valeur du Vx à celle du delay_timer
				V[x] = (byte)delay_timer;
				break;
			case 0x0A:
				//On récupère une valeur d'input et on la stocke dans Vx
				//Petite boucle pour éviter une boucle infinie qui rend impossible la récuperation de l'input
				//Thread.yield();
				while(key == -1){
					key = input.getInput();
					try {
						Thread.sleep(1);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
				System.out.println("input attendus effectué FX0A");
				V[x] = key;
				break;
			case 0x15:
				// On set le delay_timer à Vx
				delay_timer = (V[x] & 0xFF);
				break;
			case 0x18:
				// on set le sound_timer à Vx
				sound_timer = (V[x] & 0xFF);
				break;
			case 0x1E:
				//on set I à I+Vx
				I = (short)(I+V[x]);
				break;
			case 0x29:
				//On set de I avec la position du sprite de l'octet Vx
				I = (short)(V[x]*5);
				break;
			case 0x30:
				I = (short)(V[x]*10);
				break;
			case 0x33:
				//On stock la repr�sentation BCD du registre vr dans I,I+1,I+2
				byte number = this.V[x];

                for (int i = 3; i > 0; i--) {
                    this.memory[this.I + i - 1] = (byte) (number % 10);
                    number /= 10;
                }
                
                break;
			case 0x55:
				for(int i = 0; i <= x; i++)
					memory[I + i] = V[i];

				break;
			case 0x65:
				for(int i = 0;i<=x;i++){
					V[i] = memory[I + i];
				}
				break;
			case 0x75:
				for(int i=0 ; i<=x ; i++){
					RPLUserFlag[i] = V[i];
				}
				break;
			case 0x85:
				for(int i=0 ; i<=x ; i++){
					V[i] = RPLUserFlag[i];
				}
				break;
			default:
				break;
			}
			PC +=2;
			break;
		default:
			break;
		}
	}

	private void scrollLeft() {
		for(int i=0; i<128; i++)
		{
			for(int j=63; j>=0; j--)
			{
				if(j<60)
					display[i][j]=display[i][j+4];
			}
		}
	}

	private void scrollRight() {
		for(int i=0; i<128; i++)
		{
			for(int j=63; j>=0; j--)
			{
				if(j>4)
					display[i][j]=display[i][j-4];
			}
		}

	}

	private void scrollDown(int last) {
		for(int i=0; i<128; i++)
		{
			for(int j=63; j>=0; j--)
			{
				if(j>=last)
					display[i][j]=display[i-last][j];
			}
		}

	}

	public void lirePremierOpcode(){
		this.lire();
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

	public byte[] getMemory() {
		return memory;
	}

	public void setMemory(byte[] memory) {
		this.memory = memory;
	}

	public int getDelay_timer() {
		return delay_timer;
	}

	public void setDelay_timer(int delay_timer) {
		this.delay_timer = delay_timer;
	}

	public int getSound_timer() {
		return sound_timer;
	}

	public void setSound_timer(int sound_timer) {
		this.sound_timer = sound_timer;
	}

	public byte getKey() {
		return key;
	}

	public void setKey(byte key) {
		this.key = key;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public ToucheListener getInput() {
		return input;
	}

	public void setInput(ToucheListener input) {
		this.input = input;
	}

	public byte[] getRom() {
		return rom;
	}

	public void setRom(byte[] rom) {
		this.rom = rom;
	}

	public byte[] getRPLUserFlag() {
		return RPLUserFlag;
	}

	public void setRPLUserFlag(byte[] rPLUserFlag) {
		RPLUserFlag = rPLUserFlag;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getPer() {
		return per;
	}

	public void setPer(double per) {
		this.per = per;
	}

	public double getAllowance() {
		return allowance;
	}

	public void setAllowance(double allowance) {
		this.allowance = allowance;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public long getPassed() {
		return passed;
	}

	public void setPassed(long passed) {
		this.passed = passed;
	}

	public long getLast_checked() {
		return last_checked;
	}

	public void setLast_checked(long last_checked) {
		this.last_checked = last_checked;
	}

	public AudioStream getLecteur() {
		return lecteur;
	}

	public void setLecteur(AudioStream lecteur) {
		this.lecteur = lecteur;
	}

	public boolean issChipMode() {
		return sChipMode;
	}

	public void setsChipMode(boolean sChipMode) {
		this.sChipMode = sChipMode;
	}

	public boolean isCycle() {
		return cycle;
	}

	public void setCycle(boolean cycle) {
		this.cycle = cycle;
	}

	public Ecran getEcran() {
		return ecranJeu;
	}

	public void setEcran(Ecran ecran) {
		ecranJeu = ecran;
	}

	public Ecran getEcranJeu() {
		return ecranJeu;
	}

	public void setEcranJeu(Ecran ecranJeu) {
		this.ecranJeu = ecranJeu;
	}

	public int getNbPixelsAxeYChip8() {
		return nbPixelsAxeYChip8;
	}

	public void setNbPixelsAxeYChip8(int nbPixelsAxeYChip8) {
		this.nbPixelsAxeYChip8 = nbPixelsAxeYChip8;
	}

	public int getNbPixelsAxeXChip8() {
		return nbPixelsAxeXChip8;
	}

	public void setNbPixelsAxeXChip8(int nbPixelsAxeXChip8) {
		this.nbPixelsAxeXChip8 = nbPixelsAxeXChip8;
	}

	public void setKey(int key, boolean down) {
		System.out.println(key);
        if (key == -1) {
            return;
        }
        this.keys[key] = down ? 1 : 0;
    }

}
