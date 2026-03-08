package UserInterface;

import javax.swing.*;

public class CreateAccount {

    private JPanel createAccountPanel;

    public CreateAccount () {
        JFrame frame = new JFrame("Truema Clinic - Admin");
            frame.setSize(500, 600);
            frame.setContentPane(this.createAccountPanel);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
