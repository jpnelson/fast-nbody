package eric;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Multipole implements ActionListener {

	String infoMessage = "<html><h1>Multipole </h1> \n GPL \n Eric McCreath 2008 ";

	static Dimension csize = new Dimension(900, 700);

	JFrame jframe;
	SimulationCanvas canvas;
	
	Particles p;
	

	JButton clearButton;
	JButton showCharge;
	JButton joinRace;
	JButton runGame;

	Graphics2D gb;
	Graphics2D g;

	private JMenuBar theMenuBar;
	private JMenu fileMenu;
	private JMenu runMenu;
	private JMenu helpMenu;
	private JMenuItem quitMenuItem;
	private JMenuItem infoMenuItem;
	private Timer timer;
	private JPanel jpanel;

	public Multipole() {
		jframe = new JFrame("Car Racing");
		canvas = new SimulationCanvas(csize,this);

		theMenuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		runMenu = new JMenu("Run");
		helpMenu = new JMenu("Help");
		theMenuBar.add(fileMenu);
		theMenuBar.add(runMenu);
		theMenuBar.add(helpMenu);
		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(this);
		infoMenuItem = new JMenuItem("Info");
		infoMenuItem.addActionListener(this);
		fileMenu.add(quitMenuItem);
		helpMenu.add(infoMenuItem);
		clearButton = new JButton("Clear Points");
		showCharge = new JButton("Calculate Charge");
		joinRace = new JButton("Join Race");
		runGame = new JButton("Run Race");
		clearButton.addActionListener(this);
		showCharge.addActionListener(this);
		joinRace.addActionListener(this);
		runGame.addActionListener(this);
		GridBagLayout gbl = new GridBagLayout();
		jpanel = new JPanel();
		jpanel.setLayout(gbl);
		gbl.setConstraints(clearButton, gc(0, 0, 1, 1, 0.2, 0.01));
		jpanel.add(clearButton);
		gbl.setConstraints(showCharge, gc(1, 0, 1, 1, 0.2, 0.01));
		jpanel.add(showCharge);
		gbl.setConstraints(joinRace, gc(2, 0, 1, 1, 0.2, 0.01));
		jpanel.add(joinRace);
		gbl.setConstraints(runGame, gc(3, 0, 1, 1, 0.2, 0.01));
		jpanel.add(runGame);
		gbl.setConstraints(canvas, gc(0, 1, 4, 1, 0.2, 1.0));
		jpanel.add(canvas);
		jframe.setJMenuBar(theMenuBar);
		(jframe.getContentPane()).add(jpanel);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.pack();
		jframe.setVisible(true);
		p = new Particles();
	}

	public static void main(String[] args) {
		Multipole cr = new Multipole();
		cr.start();
		
		
	}

	private void start() {
		gb = canvas.getBackgroundGraphics();
		g = canvas.getOffscreenGraphics();
		gb.setColor(Color.white);
		gb.fillRect(0, 0, Multipole.csize.width, Multipole.csize.height);
	}
	
	public void redraw() {
		canvas.clearOffscreen();
		p.draw(g);
		canvas.drawOffscreen();
		
	}

	boolean racing;
	
	private void race() {	
		timer = new Timer(30, this);
		racing = true;
		timer.start();
	}

	

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			canvas.clearOffscreen();
			p.draw(g);
			canvas.drawOffscreen();
			if (racing) timer.restart();
		} else if (e.getSource() == quitMenuItem) {
			System.out.println("Bye");
			System.exit(0);
		} else if (e.getSource() == infoMenuItem) {
			JOptionPane.showMessageDialog(jframe, infoMessage);

		} else if (e.getSource() == clearButton) {
			stop();
			p = new Particles();
		} else if (e.getSource() == showCharge) {
			showCharge();

		} else if (e.getSource() == joinRace) {
			
		} else if (e.getSource() == runGame) {
			race();
		}
	}

	
	

	

	private void showCharge() {
		float scale = 10.0f;
		gb.setColor(Color.white);
		gb.fillRect(0, 0, Multipole.csize.width, Multipole.csize.height);
		for (int i=0;i<csize.height;i++) {
			for (int j=0;j<csize.width;j++) {
				double c = p.charge(new Complex(j,i));
				if (c < 0.0) {
				    gb.setColor(new Color(Math.max(0.0f,Math.min(1.0f, (float)-c/scale)),0.0f,0.0f));
				} else {
					gb.setColor(new Color(0.0f,Math.max(0.0f,Math.min(1.0f, (float)c/scale)),0.0f));
				}
				gb.fillRect(j, i, 1, 1);
			}
		}
		redraw();
	}

	private void stop() {
		racing = false;
	}

	GridBagConstraints gc(int x, int y, int w, int h, double wx, double wy) {
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = wx;
		c.weighty = wy;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = w;
		c.gridheight = h;
		c.fill = GridBagConstraints.BOTH;
		return c;
	}

	
}
