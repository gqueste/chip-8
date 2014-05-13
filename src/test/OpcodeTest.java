package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import chip8.Chip8;

public class OpcodeTest {
	
	private Chip8 chip8;
	private int pc;
	private byte[][] displayTemoin;
	
	@Before
	public void setUp() {
		this.chip8 = new Chip8();
		this.pc = chip8.getPC();
		this.displayTemoin = new byte[64][32];
		for(int i=0;i<64;i++){
			for(int j=0;j<32;j++){
				this.displayTemoin[i][j] = 0;
			}
		}
	}

	@Test
	public void test00E0() {
		chip8.opcode(0x00E0);
		assertEquals("L'écran doit etre nettoyé",this.displayTemoin, chip8.getDisplay());
		assertEquals("PC n'' a pas été incrémenté", pc+2, chip8.getPC());
	}

}
