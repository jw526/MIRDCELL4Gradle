package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class EquationsImage extends JPanel {
    private static final long serialVersionUID = 1L;
    private java.awt.image.BufferedImage image;
    private JPanel canvas;

    public EquationsImage(String s) {
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream(s));
        }catch(IOException ex) {
            Logger.getLogger(EquationsImage.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.canvas = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        JScrollPane sp = new JScrollPane(canvas);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
        JFrame f = new JFrame();
        f.setContentPane(sp);
        f.setSize(800, 600);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}

/*
public class EquationsImage{

	public EquationsImage(){
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(821, 759));
		frame.add(new JLabel( new ImageIcon("./Equations.PNG")));
		frame.show();
	}

	public static void main(String[] args){
		EquationsImage e = new EquationsImage();
	}
}
*/
