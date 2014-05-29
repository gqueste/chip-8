package main;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import chip8.Chip8;
import chip8.Ecran;
import chip8.ToucheListener;

public class Main {

	public static void main(String[] args) {
		JFrame fenetreJeu = new JFrame();
		fenetreJeu.setSize(new Dimension(800,600));
		fenetreJeu.setTitle("Emulateur Chip8 : Projet CHIP-8 EMN 2014");
		fenetreJeu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetreJeu.setResizable(true);
		fenetreJeu.getContentPane().setBackground(Color.BLACK);
		// On ouvre un explorateur pour choisir une ROM
		JFileChooser chooser = new JFileChooser(File.separator+System.getProperty("user.home")+File.separator+"git"+File.separator+"chip-8"+File.separator+"roms");
		int returnVal;
		do{
			returnVal = chooser.showOpenDialog(null);
			if(returnVal == JFileChooser.CANCEL_OPTION){
				return;
			}
		}while (returnVal!=JFileChooser.APPROVE_OPTION);

		File rom = chooser.getSelectedFile();
		ToucheListener touche = new ToucheListener();
		Chip8 chip8 = new Chip8(rom, touche);

		Ecran ecran = new Ecran(chip8.getDisplay());
		Chip8.setEcran(ecran);
		fenetreJeu.add(ecran);
		fenetreJeu.addKeyListener(touche);
		fenetreJeu.setVisible(true);

		do{
			chip8.lire();
		}while(chip8.isCycle());
		
	}

}
