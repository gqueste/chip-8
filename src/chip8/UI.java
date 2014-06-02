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
	private Thread threadJeu;

	public UI() {
		fenetreJeu = new JFrame();
		fenetreJeu.setSize(new Dimension(800,600));
		fenetreJeu.setTitle("Emulateur Chip8 : Projet CHIP-8 EMN 2014");
		fenetreJeu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetreJeu.setResizable(true);
		fenetreJeu.getContentPane().setBackground(Color.BLACK);
		
		ecran = new Ecran(null);
		fenetreJeu.add(ecran);
		touche = new ToucheListener();
	}
	
	/**
	 * Initialisation de l'interface de l'émulateur
	 */
	public void initInterface() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuRom = new JMenu("Rom");
		menuRom.setMnemonic(KeyEvent.VK_R);
		JMenuItem itemLoadRom = new JMenuItem("Charger Rom", KeyEvent.VK_C);
		itemLoadRom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		itemLoadRom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				File romObtenue = selectRom();
				if(romObtenue != null) {
					chip8.setCycle(false);
					threadJeu.interrupt();
					chargeRom(romObtenue);
				}
			}
		});

		menuRom.add(itemLoadRom);
		menuBar.add(menuRom);
		fenetreJeu.setJMenuBar(menuBar);

		//Empêche de lancer l'émulateur sans une rom validée
		do {
			rom = this.selectRom();
		}while (this.getRom() == null);
		
		//Charge la rom validée
		this.chargeRom(rom);		
	}

	/**
	 * Méthode appelée en cas de changement de rom par l'utilisateur
	 * @param romLancee, File, nouvelleRom sélectionnée
	 */
	public void chargeRom(File romLancee) {
		fenetreJeu.remove(ecran);
		chip8 = new Chip8(romLancee, touche);
		ecran = new Ecran(chip8.getDisplay());
		chip8.setEcran(ecran);
		fenetreJeu.add(ecran);
		fenetreJeu.revalidate();
		fenetreJeu.repaint();
		fenetreJeu.addKeyListener(touche);
		fenetreJeu.setVisible(true);
				
		threadJeu = new Thread() {
	        public void run() {
	        	do{
	    			chip8.lire();
	    		}while(chip8.isCycle());
	        }
	    };
	    threadJeu.start();
	}

	private File selectRom(){
		File ret = null;
		chooser = new JFileChooser(File.separator+System.getProperty("user.home")+File.separator+"git"+File.separator+"chip-8"+File.separator+"roms");
		chooser.showOpenDialog(null);
		ret = chooser.getSelectedFile();
		return ret;
	}

	/**
	 * Retourne la rom utilisée par l'émulateur
	 * @return File la rom courante
	 */
	public File getRom() {
		return this.rom;
	}
}
