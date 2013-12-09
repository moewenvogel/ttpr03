import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Gui {

	private JFrame frame;
	JPanel panel = new JPanel();
	JTextArea txtrLog = new JTextArea();
	private final JLabel lblNewLabel = new JLabel("New label");
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

		fillWater(100);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{390, 0};
		gridBagLayout.rowHeights = new int[]{1, 299, 66, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
			
	
			panel.setAutoscrolls(true);
			panel.setBackground(Color.white);
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.weighty = 1.0;
			gbc_panel.weightx = 1.0;
			gbc_panel.gridheight = 2;
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.insets = new Insets(0, 0, 5, 0);
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 0;
			frame.getContentPane().add(panel, gbc_panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 380};
			gbl_panel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 380};
			gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.gridx = 7;
			gbc_lblNewLabel.gridy = 6;
	//		panel.add(lblNewLabel, gbc_lblNewLabel);
			

			
			txtrLog.setEditable(false);
			txtrLog.setColumns(35);
			txtrLog.setTabSize(4);
			txtrLog.setRows(1);
			txtrLog.setText("log");
			GridBagConstraints gbc_txtrLog = new GridBagConstraints();
			gbc_txtrLog.fill = GridBagConstraints.BOTH;
			gbc_txtrLog.gridx = 0;
			gbc_txtrLog.gridy = 2;
			
			
			
			
			frame.getContentPane().add(txtrLog, gbc_txtrLog);
		  frame.pack();
	      
	}
	
	void fillWater(int amount){
		int cols =(int) Math.sqrt(amount);
		int rows =cols+(amount%cols > 0? 1 : 0);
		System.out.println("cols insg: "+cols);
		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("img/waterSmall.png"));
			GridBagConstraints gbc_img = new GridBagConstraints();
			gbc_img.insets = new Insets(0, 0, 0, 0);
			

			gbc_img.gridx = 0;
			gbc_img.gridy = 0;
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			panel.add(picLabel, gbc_img);

			ArrayList<GridBagConstraints> constraints= new ArrayList<GridBagConstraints>();
			int filled_rows=0;
			for(int i=1 ; i<amount; i++){
				int cur_col=i%cols;
			
				if(cur_col==0 ){
					filled_rows=filled_rows+1;
				}
				int cur_row=filled_rows;
				GridBagConstraints gbc_img2 = new GridBagConstraints();

				gbc_img2.gridy =cur_col;
				gbc_img2.gridx = cur_row;
				gbc_img2.gridheight=1;
				gbc_img2.gridwidth=1;
				gbc_img2.ipady = 0;       //reset to default
				gbc_img2.weighty = 1.0;   //request any extra vertical space
				gbc_img2.anchor = GridBagConstraints.PAGE_END; //bottom of space
				constraints.add(gbc_img2);
				System.out.println("col: "+cur_col+ " row: "+cur_row);
				JLabel label=new JLabel(new ImageIcon(myPicture));

				panel.add(label, constraints.get(i-1));
				
				
			}
			

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

