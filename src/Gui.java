import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;

public class Gui {

	JFrame frame;
	public JTextArea txtrLog = new JTextArea();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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
		frame = new JFrame();
		frame.setBounds(100, 100, 396, 503);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//	panel.setLayout(gridBagLayout);

		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{390, 0};
		gridBagLayout.rowHeights = new int[]{1, 299, 66, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
			
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.gridx = 7;
			gbc_lblNewLabel.gridy = 6;
			txtrLog.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
	//		panel.add(lblNewLabel, gbc_lblNewLabel);
			

			
			txtrLog.setEditable(false);
			txtrLog.setColumns(100);
			txtrLog.setTabSize(4);
			txtrLog.setRows(1);
			txtrLog.setText("log");
			GridBagConstraints gbc_txtrLog = new GridBagConstraints();
			gbc_txtrLog.gridheight = 3;
			gbc_txtrLog.fill = GridBagConstraints.BOTH;
			gbc_txtrLog.gridx = 0;
			gbc_txtrLog.gridy = 0;
			
			
			
			
			frame.getContentPane().add(txtrLog, gbc_txtrLog);
		  frame.pack();
	      
	}
	
}

