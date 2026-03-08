package UserInterface;

import javax.swing.*;

public class RegistrationFormStaff {

    private JPanel panelUser;

    public RegistrationFormStaff() {
        JFrame frame = new JFrame("Truema Clinic - Staff");
            frame.setContentPane(this.panelUser);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

    }


}
