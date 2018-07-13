package def;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		run();
	}
	private static void run(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			 } catch (Exception e) { }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					Gui window = new Gui();
					window.frmMtbf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
					window.frmMtbf.setFont(new Font("Tahoma", Font.PLAIN, 12));
					window.frmMtbf.setSize((int) (screenSize.getWidth() / 2), (int) (screenSize.getHeight() / 2));
					window.frmMtbf.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}
