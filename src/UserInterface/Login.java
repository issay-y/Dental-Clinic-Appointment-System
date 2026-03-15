package UserInterface;

import Database.DatabaseManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {

    private JPanel panel1;
    private JPanel PanelRight;
    private JLabel logo;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton LOGINButton;
    private JFrame frame;

    public Login() {
            frame = new JFrame();
            frame.setTitle("Truema Dental Clinic"); //sets title of frame
            frame.setContentPane(this.panel1); //connect the form to the java file
            frame.setSize(520, 500); //sets the dimension of the frame
            frame.setLocationRelativeTo(null); //location of the frame
            frame.setResizable(false);//prevents frame from being resized
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit out of application
           // visibility of ui

        LOGINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = textField1.getText();
                String pass = new String(passwordField1.getPassword());

                DatabaseManager manager = new DatabaseManager();
                int userId = manager.login(user, pass);

                if (userId != -1) {
                    String welcomeMessage;

                    if (userId == 1) { // Assuming ID 1 is the Admin
                        welcomeMessage = "Welcome Admin " + user + "!";
                        JOptionPane.showMessageDialog(panel1, welcomeMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                        new RegistrationFormAdmin();
                    } else { // All other valid IDs are Staff
                        welcomeMessage = "Welcome Staff " + user + "!";
                        JOptionPane.showMessageDialog(panel1, welcomeMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                        new RegistrationFormStaff();
                    }
                    disposeCurrentFrame();
                } else {
                    // Login failed
                    JOptionPane.showMessageDialog(panel1, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    passwordField1.setText(""); // Clear the password field for a retry
                }
            }
        });
        frame.setVisible(true);
    }

    private void disposeCurrentFrame() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(panel1);
        if (currentFrame != null) {
            currentFrame.dispose();
        }
    }

    public static void main (String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}

