package battleship.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

import battleship.logic.Game;


public class StartupWindow extends JFrame implements ActionListener
{
    private JButton btn;

    public StartupWindow()
    {
        super("Simple GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btn = new JButton("Run");
        btn.addActionListener(this);
        btn.setActionCommand("Open");
        add(btn);
        pack();
        
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if(cmd.equals("Open"))
        {
            dispose();
            Game.run();
        }
    }
}