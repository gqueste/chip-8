package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import chip8.Chip8;
import chip8.ToucheListener;

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

	@SuppressWarnings("deprecation")
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
		stackTemoin[0] = 2;
		chip8.setStack(stackTemoin);

		chip8.opcode(0x00EE);
		assertEquals("SP non décrémenté", 0, chip8.getSP());
		assertEquals("PC précédent non récupéré", stackTemoin[0] + 2, chip8.getPC());
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
		SPTemoins ++;
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
		assertEquals("Vx non modifié", (0x0004 + VxTemoin), this.chip8.getV()[3]);
	}


	@Test
	public void testLimitationNbreOperations(){
		for (int i = 0; i < 100; i++) {
			if(i == 0) {
				assertEquals("Alors que première instruction, limitationON", false, chip8.limitationNbreOperations());
			}
			else if(i == 99) {
				assertEquals("Alors que dernière instruction, limitationOFF", true, chip8.limitationNbreOperations());
			}
			else {
				chip8.limitationNbreOperations();
			}
		}
	}

	public void test8XY0(){
		byte y = chip8.getV()[2];
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8420);

		assertEquals(y, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY1(){
		byte x = chip8.getV()[4];
		byte y = chip8.getV()[2];
		byte res = (byte) (x | y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8421);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY2(){
		byte x = chip8.getV()[4];
		byte y = chip8.getV()[2];
		byte res = (byte) (x & y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8422);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY3(){
		byte x = chip8.getV()[4];
		byte y = chip8.getV()[2];
		byte res = (byte) (x ^ y);
		pcTemoin = chip8.getPC();

		chip8.opcode(0x8423);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY4(){
		byte x = chip8.getV()[4];
		byte y = chip8.getV()[2];
		byte res = (byte) (x + y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8424);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY5(){
		byte x = chip8.getV()[4];
		byte y = chip8.getV()[2];
		byte res = (byte) (x - y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8425);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY6(){
		byte x = chip8.getV()[4];
		byte res = (byte) (x / 2);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8426);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY7(){
		byte x = chip8.getV()[4];
		byte y = chip8.getV()[2];
		byte res = (byte) (y - x);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8427);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XYE(){
		byte x = chip8.getV()[4];
		byte res = (byte) (x*2);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8425);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test9XY0(){
		byte x = chip8.getV()[4];
		byte y = chip8.getV()[2];
		int pc = chip8.getPC();

		chip8.opcode(0x9420);

		if(x != y){
			assertEquals(pc + 4, chip8.getPC());
		}
		else{
			assertEquals(pc + 2, chip8.getPC());
		}
	}

	@Test
	public void testANNN(){
		pcTemoin = chip8.getPC();
		chip8.opcode(0xA666);

		assertEquals(0x0666, chip8.getI());
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void testBNNN(){
		byte[] VTemoin = new byte[16];	
		int nnn = (0xB304 & 0x0FFF);
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoin);
		chip8.opcode(0xB304);
		assertEquals("PC mal incrémenté", VTemoin[0]+nnn, this.chip8.getPC());
	}

	@Test
	public void testCNNN(){
		pcTemoin = chip8.getPC();
		chip8.opcode(0xC777);
		assertEquals("PC mal incrémenté",pcTemoin+2,this.chip8.getPC());
	}

	@Test
	public void testDNNN(){
		byte[] VTemoins = new byte[16];
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.opcode(0xD12F);
		int nbByte = (0xD12F & 0xF);
		// Flag de collision
		VTemoins[0xF] = 0;
		//place de X et Y 
		int xPlace = (VTemoins[1]&0xFF);
		int yPlace = (VTemoins[2]&0xFF);

		//Boucle d'affichage
		for(int axeY = 0; axeY < nbByte; axeY++){
			int pixel = this.chip8.getMemory()[this.chip8.getI()+axeY];
			for(int axeX = 0 ; axeX<8 ; axeX++){
				//On vérifie que le pixel n'est pas hors de "l ecran"
				if((pixel & (0x80>>axeX)) != 0 ){
					if((xPlace + axeX)>63){
						continue;
					}
					if((yPlace + axeY)>31){
						continue;
					}
					if(this.chip8.getDisplay()[xPlace+axeX][yPlace+axeY] == 1){
						assertEquals("problème Test",VTemoins[0xF], 1);
					}
					assertEquals("Problème changement",this.chip8.getDisplay()[xPlace+axeX][yPlace+axeY],1);
				}
			}
		}
		assertEquals("PC mal incrémenté",pcTemoin+2,this.chip8.getPC());
	}
	
	@Test
	public void testEX9E(){
		byte[] VTemoins = new byte[16];
		byte VxTemoin = (byte) 0x07;
		VTemoins[7] = VxTemoin;
		ToucheListener touche = new ToucheListener();
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.setInput(touche);
		chip8.setKey((byte)0x07);
		chip8.opcode(0xE79E);
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
		
	}
	
	@Test
	public void testEXA1(){
		byte[] VTemoins = new byte[16];
		byte VxTemoin = (byte) 0x07;
		VTemoins[7] = VxTemoin;
		ToucheListener touche = new ToucheListener();
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.setInput(touche);
		chip8.opcode(0xE7A1);
		assertEquals("PC non incrémenté", pcTemoin+4, this.chip8.getPC());
	}

	@Test
	public void testFX07(){
		byte[] VTemoins = new byte[16];
		chip8.setV(VTemoins);
		chip8.opcode(0xF707);
		byte dTimer = (byte)chip8.getDelay_timer();
		assertEquals("Le setter n'a pas fonctionné",dTimer,this.chip8.getV()[6]);
	}

	@Test
	public void testFX0A(){
		byte[] VTemoins = new byte[16];
		ToucheListener touche = new ToucheListener();
		chip8.setV(VTemoins);
		chip8.setInput(touche);
		chip8.setKey((byte)0x07);
		chip8.opcode(0xF70A);
		assertEquals("Le setter n'a pas fonctionné",0x07,this.chip8.getV()[7]);
	}

	@Test
	public void testFX15(){
		byte[] VTemoins = new byte[16];
		byte VxTemoin = (byte) 0x07;
		VTemoins[9] = VxTemoin;
		chip8.setV(VTemoins);
		chip8.opcode(0xF915);
		assertEquals("Le setter n'a pas fonctionné",(this.chip8.getV()[9] & 0xFF),this.chip8.getDelay_timer());
	}

	@Test
	public void testFX18(){
		byte[] VTemoins = new byte[16];
		byte VxTemoin = (byte) 0x07;
		VTemoins[9] = VxTemoin;
		chip8.setV(VTemoins);
		chip8.opcode(0xF918);
		assertEquals("Le setter n'a pas fonctionné",(this.chip8.getV()[9] & 0xFF),this.chip8.getSound_timer());
	}

	@Test
	public void testFX1E(){
		byte[] VTemoins = new byte[16];
		short tmpI = chip8.getI();
		chip8.setV(VTemoins);
		chip8.opcode(0xF91E);
		assertEquals("Le setter n'a pas fonctionné",chip8.getI(),tmpI+this.chip8.getV()[9]);
	}

	@Test
	public void testFX29(){
		byte[] VTemoins = new byte[16];
		chip8.setV(VTemoins);
		chip8.opcode(0xF929);
		assertEquals("Le setter n'a pas fonctionné",chip8.getI(),this.chip8.getV()[9]*5);

	}

	@Test
	public void testFX33(){
		byte[] VTemoins = new byte[16];
		chip8.setV(VTemoins);
		char tmpChaine[] = String.valueOf((int)(VTemoins[4] & 0xFF)).toCharArray();
		char BCD[] = {0,0,0};
		for(int place=0,count=2;place<tmpChaine.length;place++,count--){
			BCD[count]=tmpChaine[place];
		}
		chip8.opcode(0xF433);
		for(int i=0;i<3;i++){
			if(BCD[i]==0){
				assertEquals("Inégalité",
						this.chip8.getMemory()[this.chip8.getI() + i],
						0);
			}else{
				assertEquals("Inégalité",
						this.chip8.getMemory()[this.chip8.getI() + i],
						(byte)Character.getNumericValue(BCD[i]));
			}
		}
	}

	@Test
	public void testFX55(){
		int iter = 9;
		byte[] VTemoins = new byte[16];
		chip8.setV(VTemoins);
		byte[] memory = chip8.getMemory();
		short i = chip8.getI();
		chip8.opcode(0xF955);
		for(int j = 0 ; j < iter ; j++){
			assertEquals("Inégalité à la "+j+"ème itérations",
					memory[i+j],VTemoins[j] );

		}

	}

	@Test
	public void testFX65(){
		int iter = 9;
		byte[] VTemoins = new byte[16];
		chip8.setV(VTemoins);
		byte[] memory = chip8.getMemory();
		short i = chip8.getI();
		chip8.opcode(0xF965);
		for(int j = 0 ; j < iter ; j++){
			assertEquals("Inégalité à la "+j+"ème itérations",
					VTemoins[j],
					memory[i+j] );

		}

	}
}
