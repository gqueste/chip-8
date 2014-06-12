package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import chip8.Chip8;

@SuppressWarnings("deprecation")
public class OpcodeTest {

	private Chip8 chip8;
	private int pcTemoin;
	private byte[][] displayTemoin;

	@Before
	public void setUp() {
		this.chip8 = new Chip8();
		this.pcTemoin = chip8.getPC();
		this.displayTemoin = new byte[64][32];
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
		//Démonstration des problèmes entre différents émulateurs
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
	public void test00CN(){
		pcTemoin = chip8.getPC();
		byte[][] displayTemoin2 = new byte[64][128];
		for(int i=0 ; i<64 ; i++){
			displayTemoin2[1][i]=1;
		}
		byte[][] displayTest = new byte[64][128];
		for(int i=0 ; i<64 ; i++){
			displayTest[0][i]=1;
		}
		chip8.setDisplay(displayTest);
		chip8.setsChipMode(true);
		chip8.opcode(0x00C1);
		assertEquals("PC mal incrémenté",pcTemoin+2,chip8.getPC());
		assertEquals("Display mal bougé",displayTemoin2,chip8.getDisplay());
	}

	@Test
	public void test00FB(){
		pcTemoin = chip8.getPC();
		byte[][] displayTemoin2 = new byte[128][64];
		for(int i=5 ; i<64 ; i++){
			displayTemoin2[6][i]=1;
		}
		byte[][] displayTest = new byte[128][64];
		for(int i=0 ; i<64 ; i++){
			displayTest[6][i]=1;
		}
		chip8.setDisplay(displayTest);
		chip8.setsChipMode(true);
		chip8.opcode(0x00FB);
		assertEquals("PC mal incrémenté",pcTemoin+2,chip8.getPC());
		assertEquals("Display mal déplacé",displayTemoin2,chip8.getDisplay());
	}

	@Test
	public void test00FC(){
		pcTemoin = chip8.getPC();
		byte[][] displayTemoin2 = new byte[128][64];
		for(int i=0 ; i<60 ; i++){
			displayTemoin2[6][i]=1;
		}
		byte[][] displayTest = new byte[128][64];
		for(int i=0 ; i<64 ; i++){
			displayTest[6][i]=1;
		}
		chip8.setDisplay(displayTest);
		chip8.setsChipMode(true);
		chip8.opcode(0x00FC);
		assertEquals("PC mal incrémenté",pcTemoin+2,chip8.getPC());
		assertEquals("Display mal bougé",displayTemoin2,chip8.getDisplay());
	}

	@Test
	public void test00FD(){
		boolean cycle = chip8.isCycle();
		chip8.opcode(0x00FD);
		assertNotEquals("boolean mal modifié",cycle,chip8.isCycle());
	}

	@Test
	public void test00FE(){
		pcTemoin = chip8.getPC();
		boolean sChipMode = chip8.issChipMode();
		chip8.opcode(0x00FE);
		assertEquals("boolean mal modifié",sChipMode,chip8.issChipMode());
		assertEquals("PC mal incrémenté",pcTemoin+2,chip8.getPC());
	}

	@Test
	public void test00FF(){
		pcTemoin = chip8.getPC();
		boolean sChipMode = chip8.issChipMode();
		chip8.opcode(0x00FF);
		if(sChipMode){
			assertEquals("boolean mal modifié",sChipMode,chip8.issChipMode());
		}else{
			assertNotEquals("boolean mal modifié",sChipMode,chip8.issChipMode());
		}
		assertEquals("PC mal incrémenté",pcTemoin+2,chip8.getPC());
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
		int pcSauvegarde = chip8.getStack()[chip8.getSP()];
		assertEquals("PC mal sauvegardé", pcSauvegarde, pcTemoin);
		assertEquals("SP non incrémenté", SPTemoins+1, chip8.getSP());
		assertEquals("PC non modifié", (0x2333 & 0x0FFF), chip8.getPC());
	}

	@Test
	public void test3NNN() {
		int[] VTemoin = new int[16];	

		//Egalité
		pcTemoin = chip8.getPC();
		VTemoin[4] = 44;
		chip8.setV(VTemoin);
		chip8.opcode(0x342C);
		assertEquals("PC non incrémenté 2 fois", pcTemoin + 4, this.chip8.getPC());

		//Non égalité
		pcTemoin = chip8.getPC();
		VTemoin[4] = 45;
		chip8.setV(VTemoin);
		chip8.opcode(0x3444);
		assertEquals("PC non incrémenté 1 fois", pcTemoin +2, this.chip8.getPC());
	}

	@Test
	public void test4NNN() {
		int[] VTemoin = new int[16];	

		//Non Egalité
		pcTemoin = chip8.getPC();
		VTemoin[4] = 45;
		chip8.setV(VTemoin);
		chip8.opcode(0x4444);
		assertEquals("PC non incrémenté 2 fois", pcTemoin + 4, this.chip8.getPC());

		//Egalité
		pcTemoin = chip8.getPC();
		VTemoin[4] = 44;
		chip8.setV(VTemoin);
		chip8.opcode(0x442C);
		assertEquals("PC non incrémenté 1 fois", pcTemoin +2, this.chip8.getPC());
	}

	@Test
	public void test5NNN() {
		int[] VTemoin = new int[16];	

		//Egalité
		int VxTemoin = 01;
		int VyTemoin = 01;
		VTemoin[3] = VxTemoin;
		pcTemoin = chip8.getPC();
		VTemoin[4] = VyTemoin;
		chip8.setV(VTemoin);
		chip8.opcode(0x5340);
		assertEquals("Vx pas correct", VxTemoin, chip8.getV()[3]);
		assertEquals("Vy pas correct", VyTemoin, chip8.getV()[4]);
		assertEquals("PC pas incrémenté 2 fois", pcTemoin + 4, this.chip8.getPC());

		//Non égalité
		VxTemoin = 01;
		VyTemoin = 02;
		VTemoin[3] = VxTemoin;
		pcTemoin = chip8.getPC();
		VTemoin[4] = VyTemoin;
		chip8.setV(VTemoin);
		chip8.opcode(0x5340);
		assertEquals("Vx pas correct", VxTemoin, chip8.getV()[3]);
		assertEquals("Vy pas correct", VyTemoin, chip8.getV()[4]);
		assertEquals("PC mal incrémenté", pcTemoin + 2, this.chip8.getPC());
	}

	@Test
	public void test6NNN() {
		int[] VTemoin = new int[16];	
		int VxTemoin = 01;
		VTemoin[3] = VxTemoin;
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoin);
		chip8.opcode(0x6344);
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
		assertEquals("Vx non modifié", 0x0044, this.chip8.getV()[3]);
	}

	@Test
	public void test7NNN() {
		int[] VTemoin = new int[16];	
		int VxTemoin = 01;
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
		int y = 0002;
		int[] vTemoin = new int[16];
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8420);

		assertEquals(y, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY1(){
		int x = 02;
		int y = 1;
		int[] vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		int res = (x | y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8421);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY2(){
		int x = 02;
		int y =  1;
		int[] vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		int res =  (x & y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8422);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY3(){
		int x = 002;
		int y = 1;
		int[] vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		int res = (x ^ y);
		pcTemoin = chip8.getPC();

		chip8.opcode(0x8423);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY4(){
		//Retenue
		int x = 127;
		int y = 130;
		int[] vTemoin = new int[16];
		vTemoin[4] =  x;
		vTemoin[2] =  y;
		chip8.setV(vTemoin);
		int res =  (x + y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8424);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
		assertEquals("Il n'existe pas de retenue", 1, chip8.getV()[15]);
		
		//Pas retenue
		x = 3;
		y = 4;
		vTemoin = new int[16];
		vTemoin[4] =  x;
		vTemoin[2] =  y;
		chip8.setV(vTemoin);
		res =  (x + y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8424);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
		assertEquals("Il existe une retenue", 0, chip8.getV()[15]);
		
	}

	@Test
	public void test8XY5(){
		//Vx < Vy
		int x = 02;
		int y = 03;
		int[] vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		int res = (x - y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8425);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("Borrow existe", 0, chip8.getV()[15]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());

		//Vx > Vy
		x = 03;
		y = 02;
		vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		res = (x - y);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8425);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("Borrow n'existe pas", 1, chip8.getV()[15]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}

	@Test
	public void test8XY6(){
		//Last significative bit is not 1
		int x = 4;
		int[] vTemoin = new int[16];
		vTemoin[4] = x;
		chip8.setV(vTemoin);
		int res = (x / 2);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8426);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("1 en dernier bit significatif", 0, chip8.getV()[15]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());

		//Last significative bit is 1
		x = 1;
		vTemoin = new int[16];
		vTemoin[4] = x;
		chip8.setV(vTemoin);
		res = (x / 2);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8426);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("Pas de 1 en dernier bit significatif", 1, chip8.getV()[15]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}
	
	@Test
	public void test8XY7(){
		//Vy > Vx
		int x =  3;
		int y =  2;
		int[] vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		int res = (y - x);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8427);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("borrow existe", 0, chip8.getV()[15]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
		
		//Vx > Vy
		x = 2;
		y = 3;
		vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		res = (y - x);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x8427);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("Pas de borrow", 1, chip8.getV()[15]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}


	@Test
	public void test8XYE(){
		int x = 2;
		int[] vTemoin = new int[16];
		vTemoin[4] = x;
		chip8.setV(vTemoin);
		int res = (x*2);
		pcTemoin = chip8.getPC();
		chip8.opcode(0x842E);

		assertEquals(res, chip8.getV()[4]);
		assertEquals("PC non incrémenté", pcTemoin + 2, chip8.getPC());
	}
	

	@Test
	public void test9XY0(){
		//Inegalité
		int x =  22;
		int y = 23;
		int[] vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		int pc = chip8.getPC();

		chip8.opcode(0x9420);
		assertEquals(pc + 4, chip8.getPC());
		
		//Egalité
		x = 22;
		y = 22;
		vTemoin = new int[16];
		vTemoin[4] = x;
		vTemoin[2] = y;
		chip8.setV(vTemoin);
		pc = chip8.getPC();

		chip8.opcode(0x9420);
		assertEquals(pc + 2, chip8.getPC());
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
		int[] VTemoin = new int[16];	
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
		int[] VTemoins = new int[16];
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
		int[] VTemoins = new int[16];
		int VxTemoin = 0x07;
		int[] keysTemoins = new int[16];
		keysTemoins[7]=1;
		VTemoins[7] = VxTemoin;
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.setKeys(keysTemoins);
		chip8.opcode(0xE79E);
		assertEquals("PC non incrémenté", pcTemoin+4, this.chip8.getPC());

	}

	@Test
	public void testEXA1(){
		int[] VTemoins = new int[16];
		int VxTemoin = (byte) 0x07;
		int[] keysTemoins = new int[16];
		keysTemoins[7]=0;
		VTemoins[7] = VxTemoin;
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.setKeys(keysTemoins);
		chip8.opcode(0xE7A1);
		assertEquals("PC non incrémenté", pcTemoin+4, this.chip8.getPC());
	}

	@Test
	public void testFX07(){
		int[] VTemoins = new int[16];
		this.pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.opcode(0xF707);
		byte dTimer = (byte)chip8.getDelay_timer();
		assertEquals("Le setter n'a pas fonctionné",dTimer,this.chip8.getV()[6]);
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
	}

	@Test
	public void testFX0A(){
		int[] VTemoins = new int[16];
		int VxTemoin = (byte) 0x07;
		int[] keysTemoins = new int[16];
		keysTemoins[7]=1;
		VTemoins[7] = VxTemoin;
		pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.setKeys(keysTemoins);
		chip8.opcode(0xF70A);
		assertEquals("Le setter n'a pas fonctionné",0x07,this.chip8.getV()[7]);
	}

	@Test
	public void testFX15(){
		int[] VTemoins = new int[16];
		int VxTemoin = (byte) 0x07;
		this.pcTemoin = chip8.getPC();
		VTemoins[9] = VxTemoin;
		chip8.setV(VTemoins);
		chip8.opcode(0xF915);
		assertEquals("Le setter n'a pas fonctionné",(this.chip8.getV()[9] & 0xFF),this.chip8.getDelay_timer());
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
	}

	@Test
	public void testFX18(){
		int[] VTemoins = new int[16];
		int VxTemoin = (byte) 0x07;
		this.pcTemoin = chip8.getPC();
		VTemoins[9] = VxTemoin;
		chip8.setV(VTemoins);
		chip8.opcode(0xF918);
		assertEquals("Le setter n'a pas fonctionné",(this.chip8.getV()[9] & 0xFF),this.chip8.getSound_timer());
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
	}

	@Test
	public void testFX1E(){
		int[] VTemoins = new int[16];
		short tmpI = (short) chip8.getI();
		this.pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.opcode(0xF91E);
		assertEquals("Le setter n'a pas fonctionné",chip8.getI(),tmpI+this.chip8.getV()[9]);
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
	}

	@Test
	public void testFX29(){
		int[] VTemoins = new int[16];
		this.pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.opcode(0xF929);
		assertEquals("Le setter n'a pas fonctionné",chip8.getI(),this.chip8.getV()[9]*5);
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());
	}

	@Test
	public void testFX30(){
		int[] VTemoins = new int[16];
		this.pcTemoin = chip8.getPC();
		chip8.setV(VTemoins);
		chip8.opcode(0xF930);
		assertEquals("Le setter n'a pas fonctionné",chip8.getI(),this.chip8.getV()[9]*10 + 0x50);
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());		
	}

	@Test
	public void testFX33(){
		int[] VTemoins = new int[16];
		this.pcTemoin = chip8.getPC();
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
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());		
	}

	@Test
	public void testFX55(){
		int iter = 9;
		this.pcTemoin = chip8.getPC();
		int[] VTemoins = new int[16];
		chip8.setV(VTemoins);
		byte[] memory = chip8.getMemory();
		short i = (short) chip8.getI();
		chip8.opcode(0xF955);
		for(int j = 0 ; j < iter ; j++){
			assertEquals("Inégalité à la "+j+"ème itérations",
					memory[i+j],VTemoins[j] );

		}
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());		
	}

	@Test
	public void testFX65(){
		int iter = 9;
		this.pcTemoin = chip8.getPC();
		int[] VTemoins = new int[16];
		chip8.setV(VTemoins);
		byte[] memory = chip8.getMemory();
		short i = (short) chip8.getI();
		chip8.opcode(0xF965);
		for(int j = 0 ; j < iter ; j++){
			assertEquals("Inégalité à la "+j+"ème itérations",
					VTemoins[j],
					memory[i+j] );

		}
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());		
	}

	@Test
	public void testFX75(){
		int iter = 9;
		this.pcTemoin = chip8.getPC();
		int[] VTemoins = new int[16];
		chip8.setV(VTemoins);
		byte[] rplUserFlag = chip8.getRPLUserFlag();
		chip8.opcode(0xF965);
		for(int j = 0 ; j < iter ; j++){
			assertEquals("Inégalité à la "+j+"ème itérations",
					VTemoins[j],
					rplUserFlag[j] );
		}
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());		
	}

	@Test
	public void testFX85(){
		int iter = 9;
		this.pcTemoin = chip8.getPC();
		int[] VTemoins = new int[16];
		chip8.setV(VTemoins);
		byte[] rplUserFlag = chip8.getRPLUserFlag();
		chip8.opcode(0xF965);
		for(int j = 0 ; j < iter ; j++){
			assertEquals("Inégalité à la "+j+"ème itérations",
					rplUserFlag[j],
					VTemoins[j]);
		}
		assertEquals("PC non incrémenté", pcTemoin+2, this.chip8.getPC());		
	}
}
