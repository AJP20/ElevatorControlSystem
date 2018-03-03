import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Interface extends JFrame {

	private JPanel contentPane;
	ElevatorControlSystem ecs = new ElevatorControlSystem();

	/**
	 * Create the frame.
	 */
	public Interface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel GeneratePanel = new JPanel();
		GeneratePanel.setBounds(0, 0, 450, 278);
		contentPane.add(GeneratePanel);
		GeneratePanel.setLayout(null);
		
		JLabel lblGenerateUsers = new JLabel("Generate");
		lblGenerateUsers.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblGenerateUsers.setBounds(174, 23, 85, 25);
		GeneratePanel.add(lblGenerateUsers);
		
		JLabel lblNumberOfElevators = new JLabel("Number of Elevators:");
		lblNumberOfElevators.setBounds(106, 76, 132, 16);
		GeneratePanel.add(lblNumberOfElevators);
		
		Integer numbers[]={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		
		JComboBox comboBox_1 = new JComboBox(numbers);
		comboBox_1.setBounds(250, 72, 72, 27);
		GeneratePanel.add(comboBox_1);
		
		JButton btnDone = new JButton("Done");
		btnDone.setBounds(191, 134, 77, 29);
		GeneratePanel.add(btnDone);
		
				JPanel UpOrDownPanel = new JPanel();
				UpOrDownPanel.setBounds(0, 0, 450, 278);
				contentPane.add(UpOrDownPanel);
				UpOrDownPanel.setLayout(null);
				UpOrDownPanel.setVisible(false);
				
				JLabel label = new JLabel("Elevator");
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
				label.setBounds(175, 40, 93, 16);
				UpOrDownPanel.add(label);
				
				JRadioButton radioButton = new JRadioButton("Up");
				radioButton.setBounds(196, 68, 59, 23);
				UpOrDownPanel.add(radioButton);
				
				JRadioButton radioButton_1 = new JRadioButton("Down");
				radioButton_1.setBounds(196, 103, 68, 23);
				UpOrDownPanel.add(radioButton_1);
				
				JButton button = new JButton("Done");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ecs.directionsSelected(radioButton.isSelected(), radioButton_1.isSelected(), ecs.generateRandomCurrentFloor());
					}
				});
				button.setBounds(175, 138, 117, 29);
				UpOrDownPanel.add(button);
				
				JButton btnStep = new JButton("Step");
				btnStep.setBounds(175, 184, 117, 29);
				UpOrDownPanel.add(btnStep);
				btnStep.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ecs.step();
					}
				});
		
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Elevators generated: "+ comboBox_1.getItemAt(comboBox_1.getSelectedIndex()));
				ecs.generateElevators((Integer)comboBox_1.getItemAt(comboBox_1.getSelectedIndex()));
				GeneratePanel.setVisible(false);
				UpOrDownPanel.setVisible(true);
			}
		});
	}
}
