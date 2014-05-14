package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import chip8.Chip8;

public class OpcodeTest {
	
	private Chip8 chip8;
	private int pcTemoin;
	private byte[][] displayTemoin;
	
	@Before
	public void setUp() {
		this.chip8 = new Chip8();
		this.pcTemoin = chip8.getPC();
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
		assertEquals("L'�cran doit etre nettoy�",this.displayTemoin, chip8.getDisplay());
		assertEquals("PC n' a pas �t� incr�ment�", pcTemoin+2, chip8.getPC());
	}
	
	@Test
	public void test00EE() {
		chip8.setPC(0);
		chip8.setSP((byte) 1);
		int[] stackTemoin = new int[16];
		stackTemoin[0] = 2;
		chip8.setStack(stackTemoin);
		
		chip8.opcode(0x00EE);
		assertEquals("SP non d�cr�ment�", 0, chip8.getSP());
		assertEquals("PC pr�c�dent non r�cup�r�", stackTemoin[0] + 2, chip8.getPC());
	}
	
	@Test
	public void test0NNN_NonValide() {
		int SPTemoin = chip8.getSP();
		pcTemoin = chip8.getPC();
		chip8.opcode(0x0AAA);
		assertEquals("PC a �t� modifi�", pcTemoin, chip8.getPC());
		assertEquals("SP a �t� modifi�", SPTemoin, chip8.getSP());
	}
	
	@Test
	public void test1NNN() {
		pcTemoin = chip8.getPC();
		chip8.opcode(0x1333);
		assertNotEquals("PC n'a pas �t� modifi�", pcTemoin, chip8.getPC());
	}

}
