package chip8;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class UI {

	private JFrame fenetreJeu;
	private JFileChooser chooser;
	private File rom;
	private ToucheListener touche;
	private Chip8 chip8;
	private Ecran ecran;

	public UI() {
		fenetreJeu = new JFrame();
		fenetreJeu.setSize(new Dimension(800,600));
		fenetreJeu.setTitle("Emulateur Chip8 : Projet CHIP-8 EMN 2014");
		fenetreJeu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetreJeu.setResizable(true);
		fenetreJeu.getContentPane().setBackground(Color.BLACK);
		
		touche = new ToucheListener();

		JMenuBar menuBar = new JMenuBar();
		JMenu menuRom = new JMenu("Rom");
		menuRom.setMnemonic(KeyEvent.VK_R);

		JMenuItem itemLoadRom = new JMenuItem("Charger Rom", KeyEvent.VK_C);
		itemLoadRom.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, ActionEvent.ALT_MASK));
		itemLoadRom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				File romObtenue = selectRom();
				if(romObtenue != null) {
					chip8.setCycle(false);
					changeRom(romObtenue);
				}
			}
		});


		menuRom.add(itemLoadRom);
		menuBar.add(menuRom);
		fenetreJeu.setJMenuBar(menuBar);


		do {
			rom = this.selectRom();
		}while (this.getRom() == null);


		chip8 = new Chip8(rom, touche);
		ecran = new Ecran(chip8.getDisplay());
		chip8.setEcran(ecran);
		fenetreJeu.add(ecran);
		fenetreJeu.addKeyListener(touche);
		fenetreJeu.setVisible(true);

		do{
			chip8.lire();
			System.out.println("continue");
		}while(chip8.isCycle());
	}

	/**
	 * 
	 * @param romLancee, nouvelleRom mise
	 */
	public void changeRom(File romLancee) {
		
		fenetreJeu.remove(ecran);
		Chip8 chip82 = new Chip8(romLancee, touche);
		Ecran ecran2 = new Ecran(chip82.getDisplay());
		chip82.setEcran(ecran2);
		fenetreJeu.add(ecran2);
		fenetreJeu.revalidate();
		fenetreJeu.repaint();
		
		//TODO : freeze ici. rom tourne bien, mais freeze de toute la fenêtre
		//I don't know why
		
		do{
			chip82.lire();
		}while(chip82.isCycle());
	}

	private File selectRom(){
		File ret = null;
		chooser = new JFileChooser(File.separator+System.getProperty("user.home")+File.separator+"git"+File.separator+"chip-8"+File.separator+"roms");
		chooser.showOpenDialog(null);
		ret = chooser.getSelectedFile();
		return ret;
	}

	public File getRom() {
		return this.rom;
	}
}
