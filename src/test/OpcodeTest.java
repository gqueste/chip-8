package test;

import static org.junit.Assert.*;

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
		assertEquals("PC n'' a pas �t� incr�ment�", pcTemoin+2, chip8.getPC());
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
