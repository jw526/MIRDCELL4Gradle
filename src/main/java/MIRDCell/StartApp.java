/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MIRDCell;

import javax.swing.JFrame;
import javax.swing.JApplet;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import GUI.Home1;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;


/**
 * Created Jianchao
 */
public class StartApp {
	public static void main(String[] args) throws IOException{
		/*
		 * The whole reason for this is so that Dr howell can test it easier because his netbeans cant run cellApplet for some reas
		 */
		JFrame frame = new JFrame(  );
		frame.setSize( 1096, 749 );
		final JApplet program = new Home1();
		frame.getContentPane().add(program);
		frame.addWindowListener( new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
                            URL iconURL = getClass().getResource("/res/icon.png");
                            // iconURL is null when not found
                            if(iconURL != null){
                                ImageIcon icon = new ImageIcon(iconURL);
                                frame.setIconImage(icon.getImage());
                            }
                            
			}

			@Override
			public void windowClosing(WindowEvent e) {
				program.stop();
				program.destroy();
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				program.stop();
				program.destroy();
				System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}
		} );
                //ImageIcon icon = new ImageIcon("/resources/MIRDcell.png");
                //frame.setIconImage(ImageIO.read(new File("../resources/MIRDcell.png")));
		frame.setVisible( true );
		program.init();
		program.start();
	}
}
