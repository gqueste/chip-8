package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.naming.LimitExceededException;

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
	public void testloadMemory() {
		this.chip8.loadMemory();
		assertEquals("PC n'a pas été initialisé au bon endroit", 0x200, chip8.getPC());
	}

	@Test
	public void test00E0() {
		chip8.opcode(0x00E0);
		assertEquals("L'écran n'a pas été nettoyé",this.displayTemoin, chip8.getDisplay());
		assertEquals("PC n' a pas été incrémenté", pcTemoin+2, chip8.getPC());
	}
	
	@Test
	public void test00EE() {
		chip8.setPC(0);
		chip8.setSP((byte) 1);
		int[] stackTemoin = new int[16];
		stackTemoin[1] = 2;
		chip8.setStack(stackTemoin);
		
		chip8.opcode(0x00EE);
		assertEquals("SP non décrémenté", 0, chip8.getSP());
		assertEquals("PC précédent non récupéré", stackTemoin[1] +2, chip8.getPC());
	}
	
	@Test
	public void test0NNN_NonValide() {
		byte SPTemoin = chip8.getSP();
		pcTemoin = chip8.getPC();
		chip8.opcode(0x0AAA);
		assertEquals("PC a été modifié", pcTemoin, chip8.getPC());
		assertEquals("SP a été modifié", SPTemoin, chip8.getSP());
	}
	
	@Test
	public void test1NNN() {
		pcTemoin = chip8.getPC();
		chip8.opcode(0x1333);
		assertNotEquals("PC n'a pas été modifié", pcTemoin, chip8.getPC());
		assertEquals("PC mal modifié", (0x1333 & 0x0FFF), chip8.getPC());
	}
	
	@Test
	public void test2NNN() {
		pcTemoin = chip8.getPC();
		byte SPTemoins = chip8.getSP();
		chip8.opcode(0x2333);
		assertNotEquals("PC n'a pas été modifié", pcTemoin, chip8.getPC());
		
		//Verification de la sauvegarde
		int pcSauvegarde = chip8.getStack()[chip8.getSP()];
		assertEquals("PC mal sauvegardé", pcSauvegarde, pcTemoin);
		
		//Incrémentation du SP
		SPTemoins += 2;
		assertEquals("SP non incrémenté", SPTemoins, chip8.getSP());
		
		//changement du PC
		assertEquals("PC non modifié", (0x2333 & 0x0FFF), chip8.getPC());
	}
	
	@Test
	public void test3NNN() {
		byte[] VTemoin = new byte[16];	
		
		//TestValide
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) 0x0044;
		chip8.setV(VTemoin);
		chip8.opcode(0x3444);
		assertEquals("PC non incrémenté 2 fois", pcTemoin + 4, this.chip8.getPC());
		
		//test non valide
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) 0x0045;
		chip8.setV(VTemoin);
		chip8.opcode(0x3444);
		assertEquals("PC non incrémenté 1 fois", pcTemoin +2, this.chip8.getPC());
	}
	
	@Test
	public void test4NNN() {
		byte[] VTemoin = new byte[16];	
		
		//Test Valide
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) 0x0045;
		chip8.setV(VTemoin);
		chip8.opcode(0x4444);
		assertEquals("PC non incrémenté 2 fois", pcTemoin + 4, this.chip8.getPC());
		
		//test Non valide
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) 0x0044;
		chip8.setV(VTemoin);
		chip8.opcode(0x4444);
		assertEquals("PC non incrémenté 1 fois", pcTemoin +2, this.chip8.getPC());
	}
	
	@Test
	public void test5NNN() {
		byte[] VTemoin = new byte[16];	
		
		//Test valide
		byte VxTemoin = (byte) 0x0001;
		byte VyTemoin = (byte) 0x0001;
		VTemoin[3] = VxTemoin;
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) VyTemoin;
		chip8.setV(VTemoin);
		chip8.opcode(0x5340);
		assertEquals("Vx pas correct", VxTemoin, chip8.getV()[3]);
		assertEquals("Vy pas correct", VyTemoin, chip8.getV()[4]);
		assertEquals("PC pas incrémenté 2 fois", pcTemoin + 4, this.chip8.getPC());
		
		
		//Test non valide : finit pas par 0
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoin);
		chip8.opcode(0x5334);
		assertEquals("Instruction n'a pas été ignorée", pcTemoin, this.chip8.getPC());
		
		//Test non valide : pas egal
		VxTemoin = (byte) 0x0001;
		VyTemoin = (byte) 0x0002;
		VTemoin[3] = VxTemoin;
		pcTemoin = chip8.getPC();
		VTemoin[4] = (byte) VyTemoin;
		chip8.setV(VTemoin);
		chip8.opcode(0x5340);
		assertEquals("Vx pas correct", VxTemoin, chip8.getV()[3]);
		assertEquals("Vy pas correct", VyTemoin, chip8.getV()[4]);
		assertEquals("PC mal incrémenté", pcTemoin + 2, this.chip8.getPC());
				
	}
	
	@Test
	public void test6NNN() {
		byte[] VTemoin = new byte[16];	
		byte VxTemoin = (byte) 0x0001;
		VTemoin[3] = VxTemoin;
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoin);
		chip8.opcode(0x6344);
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
		assertEquals("Vx non modifié", 0x0044, this.chip8.getV()[3]);
	}
	
	@Test
	public void test7NNN() {
		byte[] VTemoin = new byte[16];	
		byte VxTemoin = (byte) 0x0001;
		VTemoin[3] = VxTemoin;
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoin);
		chip8.opcode(0x7304);
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
		assertEquals("Vx non modifié", (0x0004 + 0x0001), this.chip8.getV()[3]);
	}
	
	@Test
	public void testLimitationNbreOperations(){
		for (int i = 0; i < 100; i++) {
			if(chip8.limitationNbreOperations()) {
				System.out.println("Limit true");
			}
			else {
				System.out.println("Limit false");
			}
		}
	}
}
