package com.starbox.puzzlecar2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.starbox.puzzlecar2.MainClass;
import com.starbox.puzzlecar2.PayCar2;




public class Main implements PayCar2 {
	static Main m;
	JFrame frame;
	public static void main(String[] args) {	
		m = new Main();
		m.createGUI();	
	}
	
	public void createGUI(){
		frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		JPanel panel= new JPanel();	
		
		JButton startButton = new JButton("Small");
        panel.add(startButton, BorderLayout.WEST);
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {     
        		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();        		
        		cfg.title = "Game";        		
        		cfg.width = 1024;
        		cfg.height =720;
        		MainClass mc=new MainClass(m); 
        		new LwjglApplication(mc, cfg);         		
        		mc.setPremium(true);
            }
        });
        startButton = new JButton("medium");
        panel.add(startButton, BorderLayout.WEST);
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {     
        		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();        		
        		cfg.title = "Game";        		
        		cfg.width = 1280;
        		cfg.height =800;
        		MainClass mc=new MainClass(m); 
        		new LwjglApplication(mc, cfg);         		
        		mc.setPremium(true);
            }
        });
        startButton = new JButton("Big");
        panel.add(startButton, BorderLayout.WEST);
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {     
        		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();        		
        		cfg.title = "Game";        		
        		cfg.width = 1450;
        		cfg.height =800;
        		MainClass mc=new MainClass(m); 
        		new LwjglApplication(mc, cfg);         		
        		mc.setPremium(true);
            }
        });
        final JTextField edit1 = new JTextField("1280",4);
        final JTextField edit2 = new JTextField("800",4);
        final JCheckBox cb = new JCheckBox("premium",true);
        panel.add(edit1, BorderLayout.CENTER);
        panel.add(edit2, BorderLayout.CENTER);
        panel.add(cb, BorderLayout.CENTER);
        startButton = new JButton("Start");
        panel.add(startButton, BorderLayout.SOUTH);
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {     
        		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();        		
        		cfg.title = "Game";        		
        		cfg.width = Integer.valueOf(edit1.getText());
        		cfg.height =Integer.valueOf(edit2.getText());
        		MainClass mc=new MainClass(m); 
        		new LwjglApplication(mc, cfg);         		
        		mc.setPremium(cb.isSelected());
            }
        });
        frame.getContentPane().add(panel);          
        frame.setSize(500, 150); 
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
	}

	@Override
	public void payClick() {
        JOptionPane.showMessageDialog(frame, "Денег давай");
	}

	public void rateClick() {

        JOptionPane.showMessageDialog(frame, "коментарий давай");
	}
	@Override
	public String getAId() {
		
		return "";
	}

	@Override
	public int getAccuracy() {
		return 0;
	}

	@Override
	public void showToast(final CharSequence str) {
		System.out.println(str);		
	}

	
}
