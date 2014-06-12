package chip8;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class UI {

	private JFrame fenetreJeu;
	private JFileChooser chooser;
	private File rom;
	private ToucheListener touche;
	private Chip8 chip8;
	private Ecran ecran;
	private Thread threadJeu;
	private JRadioButtonMenuItem rbVitesse1;
	private JRadioButtonMenuItem rbVitesse2;
	private JRadioButtonMenuItem rbVitesse3;
	private JRadioButtonMenuItem rbVitesse4;

	public UI() {
		fenetreJeu = new JFrame();
		fenetreJeu.setSize(new Dimension(800,600));
		fenetreJeu.setTitle("Emulateur Chip8 : Projet CHIP-8 EMN 2014");
		fenetreJeu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetreJeu.setResizable(true);
		fenetreJeu.getContentPane().setBackground(Color.BLACK);
		
		ecran = new Ecran(null,128,64);
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
		
		
		JMenu menuVitesse = new JMenu("Vitesse");
		menuVitesse.setMnemonic(KeyEvent.VK_V);
		ButtonGroup groupVitesseButtons = new ButtonGroup();
		rbVitesse1 = new JRadioButtonMenuItem("Vitesse / 2");
		rbVitesse1.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				chip8.setRate(Chip8.DEFAULT_INSTRUCTIONS_RATE / 2);
			}
		});
		
		rbVitesse2 = new JRadioButtonMenuItem("Vitesse Normale");
		rbVitesse2.setSelected(true);
		rbVitesse2.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				chip8.setRate(Chip8.DEFAULT_INSTRUCTIONS_RATE);
			}
		});
		
		rbVitesse3 = new JRadioButtonMenuItem("Vitesse x 2");
		rbVitesse3.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				chip8.setRate(Chip8.DEFAULT_INSTRUCTIONS_RATE * 2);
			}
		});
		
		rbVitesse4 = new JRadioButtonMenuItem("Vitesse x 4");
		rbVitesse4.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				chip8.setRate(Chip8.DEFAULT_INSTRUCTIONS_RATE * 4);
			}
		});
		groupVitesseButtons.add(rbVitesse1);
		groupVitesseButtons.add(rbVitesse2);
		groupVitesseButtons.add(rbVitesse3);
		groupVitesseButtons.add(rbVitesse4);

		menuVitesse.add(rbVitesse1);
		menuVitesse.add(rbVitesse2);
		menuVitesse.add(rbVitesse3);
		menuVitesse.add(rbVitesse4);
		
		JMenu menuAbout = new JMenu("A propos");
		menuAbout.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent arg0) {
				JOptionPane.showMessageDialog(fenetreJeu, "Projet réalisé par : \n"
						+ "Gwénaël PROVOST\n"
						+ "Gabriel QUESTE\n"
						+ "Quentin VICTOOR\n"
						+ "Promotion 2016"
						+ "dans le cadre du module SCRUM de la Formation Ingénierie Logicielle"
						+ "de l'Ecole des Mines de Nantes\n"
						+ "Superviseur : Florent Marchand de Kerchove");
			}
			
			@Override
			public void menuDeselected(MenuEvent arg0) {}
			
			@Override
			public void menuCanceled(MenuEvent arg0) {}
		});
		
		menuBar.add(menuRom);
		menuBar.add(menuVitesse);
		menuBar.add(menuAbout);
		fenetreJeu.setJMenuBar(menuBar);

		//Empêche de lancer l'émulateur sans une rom validée
		do {
			rom = this.selectRom();
		}while (this.getRom() == null);
		
		//Charge la rom validée
		this.chargeRom(rom);		//chip8-java-master
	}

	/**
	 * Méthode appelée en cas de changement de rom par l'utilisateur
	 * @param romLancee, File, nouvelleRom sélectionnée
	 */
	public void chargeRom(File romLancee) {
		fenetreJeu.remove(ecran);
		chip8 = new Chip8(romLancee, touche);
		chip8.lirePremierOpcode();
		ecran = new Ecran(chip8.getDisplay(),chip8.getNbPixelsAxeXChip8(),chip8.getNbPixelsAxeYChip8());
		chip8.setEcran(ecran);
		fenetreJeu.add(ecran);
		fenetreJeu.revalidate();
		fenetreJeu.repaint();
		fenetreJeu.addKeyListener(touche);
		fenetreJeu.setVisible(true);
		rbVitesse2.setSelected(true);
				
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
