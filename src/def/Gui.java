package def;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JTextArea;
import java.awt.Font;

public class Gui {
	public JFrame frmMtbf;
	private JTextField txtAdmin;
	private JTextField textField_Range_1;									// Start range
	private JTextField textField_Range_2;									// End range
	private JButton btnStart = new JButton("Start");
	private JButton btnStop = new JButton("Stop");
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextArea textArea = new JTextArea();							// Main text area
	
	private MTBF mtBF;
	private Thread bfThread;												// MTBF class thread
	private Thread resultThread;											// Thread to update textArea text
	private int maxThreads = Runtime.getRuntime().availableProcessors();	// get max threads supported by pc
	private int threads = maxThreads;
	private boolean isRangeOk = true;										// boolean to check BF range
	private boolean isStopped = false;										// boolean to check if stopped by user

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMtbf = new JFrame();
		frmMtbf.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				setButtons();

			}
		});
		/* 
		 * func to get back focus from input text fields
		 */
		frmMtbf.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frmMtbf.requestFocus();
			}
		});
		frmMtbf.setTitle("MT BruteForce");
		frmMtbf.setBounds(100, 100, 450, 300);
		frmMtbf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel_RightSide = new JPanel();
		panel_RightSide
				.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel panel_Main = new JPanel();
		panel_Main.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout groupLayout = new GroupLayout(frmMtbf.getContentPane());
		groupLayout
				.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
						groupLayout.createSequentialGroup().addContainerGap()
								.addComponent(panel_Main, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(panel_RightSide,
										GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_RightSide, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 240,
										Short.MAX_VALUE)
								.addComponent(panel_Main, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 240,
										Short.MAX_VALUE))
						.addContainerGap()));

		GroupLayout gl_panel_Main = new GroupLayout(panel_Main);
		gl_panel_Main.setHorizontalGroup(gl_panel_Main.createParallelGroup(Alignment.LEADING).addComponent(textArea,
				GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE));
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		gl_panel_Main.setVerticalGroup(gl_panel_Main.createParallelGroup(Alignment.LEADING).addComponent(textArea,
				GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE));
		panel_Main.setLayout(gl_panel_Main);

		JLabel lblLogin = new JLabel("Login:");

		txtAdmin = new JTextField();
		txtAdmin.setEnabled(false);
		txtAdmin.setText("admin");
		txtAdmin.setColumns(10);

		JLabel lblRange1 = new JLabel("Range:");
		
		textField_Range_1 = new JTextField();
		textField_Range_1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				warn();
			}
			@Override
			public void focusGained(FocusEvent e) {
				isRangeOk = false;
				btnStart.setEnabled(false);
				btnStop.setEnabled(false);
			}
			// pattern to accept only decimals with one to 3 digits
			Pattern pt = Pattern.compile("\\d{1,3}");
			public void warn() {
				// check if theres a input and it matches the pattern
				if (textField_Range_1.getText().length() > 0 && !pt.matcher(textField_Range_1.getText()).matches()) {
					JOptionPane.showMessageDialog(null, "Error: Please enter a number with atleast 1 digit", "Error Massage",
							JOptionPane.ERROR_MESSAGE);
					isRangeOk = false;
					btnStart.setEnabled(false);
					btnStop.setEnabled(false);
				} else if (textField_Range_1.getText().length() == 0) {
					JOptionPane.showMessageDialog(null, "Error: Please enter a number bigger than 0", "Error Massage",
							JOptionPane.ERROR_MESSAGE);
					isRangeOk = false;
					btnStart.setEnabled(false);
					btnStop.setEnabled(false);
				} else {
					isRangeOk = true;
					setButtons();
				}
			}
		});
		textField_Range_1.setText("0");
		textField_Range_1.setColumns(10);

		textField_Range_2 = new JTextField();
		textField_Range_2.setText("999");
		textField_Range_2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				warn();
			}
			@Override
			public void focusGained(FocusEvent e) {
				isRangeOk = false;
				btnStart.setEnabled(false);
				btnStop.setEnabled(false);
			}
			Pattern pt = Pattern.compile("\\d{1,3}");
			public void warn() {
				if (textField_Range_2.getText().length() > 0 && !pt.matcher(textField_Range_2.getText()).matches()) {
					JOptionPane.showMessageDialog(null, "Error: Please enter a number with atleast 1 digit", "Error Massage",
							JOptionPane.ERROR_MESSAGE);
					isRangeOk = false;
					btnStart.setEnabled(false);
					btnStop.setEnabled(false);
				} else if (textField_Range_2.getText().length() == 0) {
					JOptionPane.showMessageDialog(null, "Error: Please enter number bigger than 0", "Error Massage",
							JOptionPane.ERROR_MESSAGE);
					isRangeOk = false;
					btnStart.setEnabled(false);
					btnStop.setEnabled(false);
				} else {
					isRangeOk = true;
					setButtons();
				}
			}
		});

		textField_Range_2.setColumns(10);

		JLabel lbllblRange2 = new JLabel("to");
		lbllblRange2.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblThreads = new JLabel("Threads:");
		JRadioButton rdbtnNewRadioButton = new JRadioButton("One");
		buttonGroup.add(rdbtnNewRadioButton);
		// set threads number to one
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setThreadNumber(1);
			}
		});

		JRadioButton rdbtnTwo = new JRadioButton("Two");
		buttonGroup.add(rdbtnTwo);
		// set threads number to two
		rdbtnTwo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setThreadNumber(2);
			}
		});

		JRadioButton rdbtnMax = new JRadioButton("Max");
		buttonGroup.add(rdbtnMax);
		// set thread number to max avaible
		rdbtnMax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setThreadNumber(maxThreads);
			}
		});
		rdbtnMax.setSelected(true);
		/*
		 * Start button functionality
		 */
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//set gui
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				
				isStopped = false;
				
				// build MTBF class
				mtBF = new MTBF.Builder()
						.LOGIN("admin")
						.RANGE_START(Integer.parseInt(textField_Range_1.getText()))
						.RANGE_END(Integer.parseInt(textField_Range_2.getText()))
						.THREADS(getThreadNumber())
						.build();
				// start new thread
				bfThread = new Thread(new Runnable() {
					@Override
					public void run() {
						mtBF.setThreadsNumber(getThreadNumber());
						mtBF.run();
					}
				});

				bfThread.start();
				
				setButtons();
				showResults();
			}
		});
		/*
		 * Stop button functionality
		 */
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// disable stop button
				btnStop.setEnabled(false);
				synchronized (this) {
					mtBF.stopThreads();
				}
				// set stop boolean to true
				isStopped = true;
				// enable start button
				btnStart.setEnabled(true);
			}
		});
		// stop button disabled on startup
		btnStop.setEnabled(false);
		GroupLayout gl_panel_RightSide = new GroupLayout(panel_RightSide);
		gl_panel_RightSide.setHorizontalGroup(gl_panel_RightSide.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_RightSide.createSequentialGroup()
						.addGroup(gl_panel_RightSide.createParallelGroup(Alignment.LEADING).addComponent(lblLogin)
								.addComponent(lblRange1))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panel_RightSide.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_RightSide.createSequentialGroup().addComponent(txtAdmin, 90, 90, 90)
										.addGap(34))
								.addGroup(gl_panel_RightSide.createSequentialGroup()
										.addComponent(textField_Range_1, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(lbllblRange2, GroupLayout.PREFERRED_SIZE, 16,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(textField_Range_2,
												GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)))
						.addGap(21))
				.addGroup(gl_panel_RightSide.createSequentialGroup().addComponent(lblThreads)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(rdbtnNewRadioButton)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(rdbtnTwo, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(rdbtnMax, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(gl_panel_RightSide.createSequentialGroup().addContainerGap().addComponent(btnStart)
						.addPreferredGap(ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
						.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		gl_panel_RightSide.setVerticalGroup(gl_panel_RightSide.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_RightSide.createSequentialGroup()
						.addGroup(gl_panel_RightSide.createParallelGroup(Alignment.BASELINE).addComponent(lblLogin)
								.addComponent(txtAdmin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panel_RightSide.createParallelGroup(Alignment.BASELINE).addComponent(lblRange1)
								.addComponent(textField_Range_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lbllblRange2).addComponent(textField_Range_2, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panel_RightSide.createParallelGroup(Alignment.BASELINE).addComponent(lblThreads)
								.addComponent(rdbtnNewRadioButton).addComponent(rdbtnTwo).addComponent(rdbtnMax))
						.addGap(18).addGroup(gl_panel_RightSide.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnStart).addComponent(btnStop))
						.addContainerGap(105, Short.MAX_VALUE)));
		panel_RightSide.setLayout(gl_panel_RightSide);
		frmMtbf.getContentPane().setLayout(groupLayout);
	}
	/*
	 * setButtons func
	 * sets the buttons according to current situation
	 */
	private void setButtons() {
		// if BF proccess is running
		if (bfThread != null && bfThread.isAlive() && isRangeOk == true) {
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
		// if BF proccess is terminated
		} else if (bfThread != null && bfThread.getState().toString() == "TERMINATED" && isRangeOk == true) {
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
			// check if terminated by user
			if (isStopped) {
				synchronized (this) {
					textArea.setText(mtBF.getResult() + "\nStopped.");
				}
			} else {
				synchronized (this) {
					textArea.setText(mtBF.getResult());
				}
			}
		// check the range
		} else if (isRangeOk == false) {
			btnStart.setEnabled(false);
			btnStop.setEnabled(false);
		} else if (isRangeOk == true && bfThread == null) {
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
		}
	}
	/*
	 * set threads number
	 */
	private void setThreadNumber(int thread) {
		this.threads = thread;
	}
	/*
	 * get threads number
	 */
	public int getThreadNumber() {
		return this.threads;
	}
	/*
	 * Show BF results on textarea
	 */
	private void showResults() {
		// start new thread so GUI doesnt freeze
		resultThread = new Thread() {
			public void run() {
				while (bfThread.getState().toString() != "TERMINATED") {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							synchronized (this) {
								textArea.setText(mtBF.getResult());
							}
						}
					});
					try {
						Thread.sleep(10);
					} catch (Exception e) {
					}
				}
			}
		};

		resultThread.start();
	}
}
