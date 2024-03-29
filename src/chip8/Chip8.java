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
	private byte SP;
	private int I;
	private int[] V;
	private byte[] memory,rom, RPLUserFlag;
	private byte[][] display;
	private int[] stack;
	private Random random;
	// Attributs relatifs au temps et rythme d'interprétation des instructions
	private double rate, per, allowance;
	private long current, passed, last_checked;
	private AudioStream lecteur;
	private boolean sChipMode = false;
	private boolean cycle = true;
	private Ecran ecranJeu;

	private int[] keys;

	private int nbPixelsAxeXChip8 = 64;
	private int nbPixelsAxeYChip8 = 32;

	/**
	 * Constructeur pour les tests où la rom n'est pas nécessaire
	 */
	public Chip8(){
		this.PC = 0;
		this.SP = 0;
		this.setStack(new int[16]);
		this.V = new int[16];
		this.RPLUserFlag = new byte[16];
		this.random = new Random(567765);
		this.memory = new byte[4096];
		// 14 instructions pour 100 ms
		this.setRate(14);
		per = 100;
		allowance = rate;
		last_checked = System.currentTimeMillis();

		display = new byte[nbPixelsAxeXChip8][nbPixelsAxeYChip8];
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
	public Chip8(File rom) {
		this.I = 0x0;
		this.PC = 0;
		this.SP = 0;
		this.stack = new int[16];
		this.V = new int[16];
		this.random = new Random(567765);
		this.memory = new byte[4096];
		this.keys = new int[16];
		loadMemory();
		// 20 instructions pour 100 ms
		this.setRate(DEFAULT_INSTRUCTIONS_RATE);
		per = 100;
		allowance = rate;
		last_checked = System.currentTimeMillis();
		display = new byte[nbPixelsAxeYChip8][nbPixelsAxeXChip8];
		loadRom(rom);
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

		msb = ((memory[PC]));
		lsb = ((memory[PC + 1]) & 0xFF);
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
		int[] opcodeNibble = new int[4];
		opcodeNibble[0] = ((opcode & 0xF000) >> 12);
		opcodeNibble[1] = ((opcode & 0x0F00) >> 8);
		opcodeNibble[2] = ((opcode & 0x00F0) >> 4);
		opcodeNibble[3] = ((opcode & 0x000F) >> 0);
		switch (opcodeNibble[0]) {
		case 0x0 :
			if(opcodeNibble[1] == 0x0) {
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
				else if (opcodeNibble[2]==0xc){
					if(sChipMode){
						scrollDown(opcodeNibble[3]);
						if(ecranJeu!=null){
							ecranJeu.repaint();
						}
					}
					this.PC += 2;
				}
				else if (opcode == 0x00FB){
					if(sChipMode){
						scrollRight();
						if(ecranJeu!=null){
							ecranJeu.repaint();
						}
					}
					this.PC +=2;
				}
				else if (opcode == 0x00FC){
					if(sChipMode){
						scrollLeft();
						if(ecranJeu!=null){
							ecranJeu.repaint();
						}
					}
					this.PC +=2;
				}
				else if (opcode == 0x00FD){
					this.setCycle(false);
				}
				else if (opcode == 0x00FE){
					sChipMode=false;
					if(!issChipMode()){
						nbPixelsAxeXChip8 = 64;
						nbPixelsAxeYChip8 = 32;
						display = new byte[nbPixelsAxeYChip8][nbPixelsAxeXChip8];
					}
					this.PC +=2;
				}
				else if (opcode == 0x00FF){
					sChipMode=true;
					if(issChipMode()){
						nbPixelsAxeXChip8 = 128;
						nbPixelsAxeYChip8 = 64;
						display = new byte[nbPixelsAxeYChip8][nbPixelsAxeXChip8];
					}
					this.PC +=2;
				}
			}
			break;

		case 0x1:
			// Jump to adress
			PC = (opcode & 0x0FFF);			
			break;

		case 0x2:
			// Call a subroutine
			stack[SP] = PC;
			SP++;
			PC = (opcode & 0x0FFF);
			break;

		case 0x3:
			//Skip if equal
			if(V[opcodeNibble[1]] == (opcode & 0x00FF)) {
				PC += 4;
			}
			else {
				PC +=2;
			}
			break;

		case 0x4:
			//Skip if not equal
			if(V[opcodeNibble[1]] != (opcode & 0x00FF)) {
				PC += 4;
			}
			else {
				PC +=2;
			}
			break;

		case 0x5:
			if(V[opcodeNibble[1]] == V[opcodeNibble[2]]) {
				PC += 4;
			}
			else {
				PC += 2;
			}
			break;

		case 0x6:
			//Set of Vx
			V[opcodeNibble[1]] = (byte) (opcode & 0x00FF);
			PC += 2;
			break;

		case 0x7:
			//Add to x
			V[opcodeNibble[1]] = (byte)(V[opcodeNibble[1]] + (opcode & 0x00FF));
			PC += 2;
			break;

		case 0x8:
		{
			int mask = (opcode & 0x000F);			
			switch(mask) {
			case 0x0:
			{
				// Set Vx = Vy.
				V[opcodeNibble[1]] = V[opcodeNibble[2]];

				break;
			}

			case 0x1:
			{
				// Set Vx = Vx OR Vy.

				V[opcodeNibble[1]] = (V[opcodeNibble[1]] | V[opcodeNibble[2]]);

				break;
			}

			case 0x2:
			{
				/*
				 * Set Vx = Vx AND Vy.
				 */

				V[opcodeNibble[1]] = (V[opcodeNibble[1]] & V[opcodeNibble[2]]);				
				break;
			}

			case 0x3:
			{
				/*
				 * Set Vx = Vx XOR Vy.
				 */

				V[opcodeNibble[1]] ^= V[opcodeNibble[2]];
				break;
			}

			case 0x4:
			{
				/*
				 * Set Vx = Vx + Vy, set VF = carry.
				 */
				if((V[opcodeNibble[1]] ) > (0xFF - V[opcodeNibble[2]])){
					V[0xF] = 1;
				}
				else
					V[0xF] = 0;
				V[opcodeNibble[1]] = (V[opcodeNibble[1]] + V[opcodeNibble[2]]);
				break;
			}

			case 0x5:
			{
				/*
				 * Set Vx = Vx - Vy, set VF = NOT borrow.
				 */

				if(V[opcodeNibble[1]] > V[opcodeNibble[2]])
					V[0xF] = 1;
				else
					V[0xF] = 0;

				V[opcodeNibble[1]] = (V[opcodeNibble[1]] - V[opcodeNibble[2]]);

				break;
			}

			case 0x6:
			{
				/*
				 * Set Vx = Vx SHR 1.
				 */
				int temp = V[opcodeNibble[1]] & 1;
				V[opcodeNibble[1]] = (V[opcodeNibble[1]] / 2);
				V[0xF] = temp;
				break;
			}

			case 0x7:
			{
				/*
				 * Set Vx = Vy - Vx, set VF = NOT borrow.
				 */

				V[0xF] = (V[opcodeNibble[1]] <= V[opcodeNibble[2]]) ? 1 : 0;
				V[opcodeNibble[1]] = (V[opcodeNibble[2]] - V[opcodeNibble[1]]);

				break;
			}

			case 0xE:
			{
				/*
				 * Set Vx = Vx SHL 1.
				 */

				// Set flag register if MSb of Vx is set
				V[0xF] = (((V[opcodeNibble[1]] & 0x80) >> 7) == 1) ? 1 : 0;
				V[opcodeNibble[1]] = V[opcodeNibble[1]] * 2;
				break;
			}

			// End masking
			}

			PC += 2;
			break;
		}
		case 0x9:
			//Skips
			if(this.V[opcodeNibble[1]] == this.V[opcodeNibble[2]]){
				this.PC += 2;
			}
			else{
				this.PC += 4;
			}
			break;
		case 0xA:
			//Set I
			setRegI(opcode&0x0FFF);
			this.PC += 2;
			break;
		case 0xB:
			/**
			 * BNNN : Instruction pour sauter à l'adresse NNN depuis le registre v0 
			 */
			PC = ((opcode & 0x0FFF)+V[0])&0x0FFF;
			break;
		case 0xC:
			/**
			 * CXKK Generer un byte aléatoire pour le registre Vx et y ajouter KK
			 */
			V[opcodeNibble[1]] = (byte) ((int)(Math.random()*0xFF) & (opcode & 0x00FF));
			PC += 2;
			break;
		case 0xD:
			/**
			 * DXYN : Affichage des sprites
			 */
			V[0xF] = 0;
			int x = V[opcodeNibble[1]]&0xFF;
			int y = V[opcodeNibble[2]]&0xFF;
			int last = opcodeNibble[3];
			if(issChipMode()) {
				dessinExtended(x,y,last);

			}else{
				dessin(x,y,last);
			}
			if(ecranJeu!=null){
				ecranJeu.repaint();
			}
			PC +=2;
			break;
		case 0xE:
			/**
			 * Instruction commençant par un E
			 */
			// on récupère le reste de l'instruction
			if((opcode & 0x00FF) == 0x9E){
				//On skip si la bonne touche est pressée
				if (this.keys[this.V[opcodeNibble[1]]] == 1){
					PC+=4;
				}else{
					PC+=2;
				}
			}else if((opcode & 0x00FF) == 0xA1){
				//On skip si la bonne touche n est pas pressée
				if(this.keys[this.V[opcodeNibble[1]]] == 0){
					PC+=4;
				}else{
					PC+=2;
				}
			}
			break;
		case 0xF:
			/**
			 * Toutes les instructions qui commence par F
			 */
			switch((opcode & 0x00FF)){
			case 0x07:
				//On set la valeur du Vx à celle du delay_timer
				V[opcodeNibble[1]] = (byte)delay_timer;
				PC +=2;
				break;
			case 0x0A:
				//On récupère une valeur d'input et on la stocke dans Vx
				//Petite boucle pour éviter une boucle infinie qui rend impossible la récuperation de l'input
				boolean cycle= new Boolean(true);
				while(cycle){
					for(int i =0 ; i<16 ; i++){
						if(keys[i]==1){
							V[opcodeNibble[1]] = (byte) i;
							cycle=false;
						}
					}
					try {
						Thread.sleep(1);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
				PC +=2;
				break;
			case 0x15:
				// On set le delay_timer à Vx
				delay_timer = (V[opcodeNibble[1]] & 0xFF);
				PC +=2;
				break;
			case 0x18:
				// on set le sound_timer à Vx
				sound_timer = (V[opcodeNibble[1]] & 0xFF);
				PC +=2;
				break;
			case 0x1E:
				//on set I à I+Vx
				setRegI(I+(V[opcodeNibble[1]] & 0x00FF));
				PC +=2;
				break;
			case 0x29:
				//On set de I avec la position du sprite de l'octet Vx
				setRegI(V[opcodeNibble[1]]*5);
				PC +=2;
				break;
			case 0x30:
				setRegI(0x50 + V[opcodeNibble[1]]*10);
				PC +=2;
				break;
			case 0x33:
				//On stock la représentation BCD du registre vr dans I,I+1,I+2
				int number = this.V[opcodeNibble[1]];
				for (int i = 3; i > 0; i--) {
					this.memory[this.I + i - 1] = (byte) (number % 10);
					number /= 10;
				}
				PC +=2;
				break;
			case 0x55:
				for(int i = 0; i <= opcodeNibble[1]; i++){
					memory[I + i] = (byte)V[i];
				}
				PC +=2;
				break;
			case 0x65:
				for(int i = 0;i<=opcodeNibble[1];i++){
					V[i] = memory[I + i];
				}
				PC +=2;
				break;
			case 0x75:
				int temp = (opcodeNibble[1] < 8 ? opcodeNibble[1] : 7);
				for(int i=0 ; i<=temp ; i++){
					RPLUserFlag[i] = (byte) V[i];
				}
				PC +=2;
				break;
			case 0x85:
				int temp2 = (opcodeNibble[1] < 8 ? opcodeNibble[1] : 7);
				for(int i=0 ; i<=temp2 ; i++){
					V[i] = RPLUserFlag[i];
				}
				PC +=2;
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	private void dessin(int x, int y, int last) {
		if(last == 0){
			last = 16;
		}
		for(short axeY = 0; axeY < last; axeY++){
			short pixel = memory[I+axeY];
			for(short axeX = 0 ; axeX<8 ; axeX++){
				if((pixel & (0x80>>axeX)) > 0 ){
					if((x + axeX)>=nbPixelsAxeXChip8){
						continue;
					}
					if((y + axeY)>=nbPixelsAxeYChip8){
						continue;
					}
					if(display[y+axeY][x+axeX] == 1){
						V[0xF] = 1;
					}
					display[y+axeY][x+axeX] ^= 1;
				}
			}
		}
	}

	private void dessinExtended(int x, int y, int last) {
		if(last == 0){
			last = 16;
			for (int axeY = 0 ; axeY < last ; axeY++){
				int pixel1 = memory[I+axeY*2];
				int pixel2 = memory[I+axeY*2+1];

				for(short axeX = 0 ; axeX<8 ; axeX++){
					//On vérifie que le pixel n'est pas hors de "l ecran"
					if((pixel1 & (0x80>>>axeX)) > 0 ){
						if((x + axeX)>=nbPixelsAxeXChip8){
							continue;
						}
						if((y + axeY)>=nbPixelsAxeYChip8){
							continue;
						}
						if(display[y+axeY][x+axeX] == 1){
							V[0xF] = 1;
						}
						display[y+axeY][x+axeX] ^= 1;
					}
					if((pixel2 & (0x80>>>axeX)) > 0 ){
						if((x+8+ axeX)>=nbPixelsAxeXChip8){
							continue;
						}
						if((y + axeY)>=nbPixelsAxeYChip8){
							continue;
						}
						if(display[y+axeY][8+x+axeX] == 1){
							V[0xF] = 1;
						}
						display[y+axeY][8+x+axeX] ^= 1;
					}
				}
			}
		}
		else{
			for(int axeY = 0 ; axeY < last ; axeY++){
				int pixel = memory[I+axeY];

				for(short axeX = 0 ; axeX<8 ; axeX++){
					if((pixel & (0x80>>> axeX)) > 0){
						if((x + axeX)>=nbPixelsAxeXChip8){
							continue;
						}
						if((y + axeY)>=nbPixelsAxeYChip8){
							continue;
						}
						if(display[y+axeY][x+axeX] == 1){
							V[0xF] = 1;
						}
						display[y+axeY][x+axeX] ^= 1;
					}
				}
			}
		}

	}

	private void scrollLeft() {
		for (int i=0; i<nbPixelsAxeXChip8; i++) {
            for (int j=0; j<nbPixelsAxeYChip8; j++) {
                if (i<nbPixelsAxeXChip8-4)
                    display[j][i] = display[j][i+4];
                else
                    display[j][i] = 0;
            }
        }
	}

	private void scrollRight() {
		for (int i=nbPixelsAxeXChip8-1; i>=0; i--) {
        for (int j=0; j<nbPixelsAxeYChip8; j++) {
            if (i>4)
               display[j][i] = display[j][i-4];
            else
               display[j][i] = 0;
         }
     }  

	}

	private void scrollDown(int last) {
		for (int i=0; i<nbPixelsAxeXChip8; i++) {
            for (int j=nbPixelsAxeYChip8-1; j>=0; j--) {
                if (j>=last)
                    display[j][i] = display[j-last][i];
                else
                    display[j][i] = 0;
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

	public int getI() {
		return I;
	}

	public void setI(int i) {
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

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
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

	public int[] getKeys() {
		return keys;
	}

	public void setKeys(int[] keys) {
		this.keys = keys;
	}

	public void setKey(int key, boolean down) {
		if (key == -1) {
			return;
		}
		this.keys[key] = down ? 1 : 0;
	}
	public void setRegI(int b) { I = b & 0xFFFF; }
}
