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
		stackTemoin[1] = 2;
		chip8.setStack(stackTemoin);
		
		chip8.opcode(0x00EE);
		assertEquals("SP non d�cr�ment�", 0, chip8.getSP());
		assertEquals("PC pr�c�dent non r�cup�r�", stackTemoin[1] + 2, chip8.getPC());
	}
	
	@Test
	public void test0NNN_NonValide() {
		byte SPTemoin = chip8.getSP();
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
		assertEquals("PC mal modifi�", (0x1333 & 0x0FFF), chip8.getPC());
	}
	
	@Test
	public void test2NNN() {
		pcTemoin = chip8.getPC();
		byte SPTemoins = chip8.getSP();
		chip8.opcode(0x2333);
		assertNotEquals("PC n'a pas �t� modifi�", pcTemoin, chip8.getPC());
		
		//Verification de la sauvegarde
		int pcSauvegarde = chip8.getStack()[chip8.getSP()];
		assertEquals("PC mal sauvegard�", pcSauvegarde, pcTemoin);
		
		//Incr�mentation du SP
		SPTemoins ++;
		assertEquals("SP non incr�ment�", SPTemoins, chip8.getSP());
		
		//changement du PC
		assertEquals("PC non modifi�", (0x2333 & 0x0FFF), chip8.getPC());
	}
	
	@Test
	public void test3NNN() {
		byte[] VTemoin = new byte[16];	
		
		//TestValide
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) 0x0044;
		chip8.setV(VTemoin);
		chip8.opcode(0x3444);
		assertEquals("PC non incr�ment� 2 fois", pcTemoin + 4, this.chip8.getPC());
		
		//test non valide
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) 0x0045;
		chip8.setV(VTemoin);
		chip8.opcode(0x3444);
		assertNotEquals("PC incr�ment� 2 fois", pcTemoin + 4, this.chip8.getPC());
	}
	
	@Test
	public void test4NNN() {
		byte[] VTemoin = new byte[16];	
		
		//Test Valide
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) 0x0045;
		chip8.setV(VTemoin);
		chip8.opcode(0x4444);
		assertEquals("PC non incr�ment� 2 fois", pcTemoin + 4, this.chip8.getPC());
		
		//test Non valide
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) 0x0044;
		chip8.setV(VTemoin);
		chip8.opcode(0x4444);
		assertEquals("PC mal incr�ment�", pcTemoin + 2, this.chip8.getPC());
	}
	
//	@Test
//	public void test8XY0(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		
//		chip8.opcode(0x8XY0);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test8XY1(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		int res = x || y;
//		
//		chip8.opcode(0x8XY1);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test8XY2(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		
//		chip8.opcode(0x8XY2);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test8XY3(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		
//		chip8.opcode(0x8XY3);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test8XY4(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		
//		chip8.opcode(0x8XY4);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test8XY5(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		
//		chip8.opcode(0x8XY0);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test8XY6(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		
//		chip8.opcode(0x8XY0);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test8XY7(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		
//		chip8.opcode(0x8XY0);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test8XYE(){
//		int x = chip8.getV()[X];
//		int y = chip8.getV()[Y];
//		
//		chip8.opcode(0x8XY0);
//		
//		assertEquals(y, chip8.getV()[X]);
//	}
//	
//	@Test
//	public void test9XY0(){
//		
//	}
//	
//	@Test
//	public void testANNN(){
//		
//	}

}
