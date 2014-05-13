package test;

import static org.junit.Assert.*;

import org.junit.Test;

import chip8.Chip8;

public class OpcodeTest {

	@Test
	public void test00E0() {
		Chip8 chip8 = new Chip8();
		int pc = chip8.getPC();
		byte display[][] = new byte[64][32];
		for(int i=0;i<64;i++){
			for(int j=0;j<32;j++){
				display[i][j] = 0;
			}
		}
		
		chip8.opcode(0x00E0);
		
		
		assertEquals("L'écran doit etre nettoyé",display, chip8.getDisplay());
		assertEquals(pc+2, chip8.getPC());
	}

}
