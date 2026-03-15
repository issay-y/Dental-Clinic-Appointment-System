package UserInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Database.DatabaseManager;
import java.util.List;
import java.util.Map;


public class RegistrationFormAdmin {

    private JPanel panelUserAdmin;
    private JButton viewStaffButton;
    private JButton patientRegistrationButton;
    private JButton scheduleViewingButton;
    private JButton logoutButton;
    private JPanel cardPanel;
    private JPanel panelStaff;
    private JPanel panelRegisteration;
    private JPanel panelSchedule;
    private JButton refreshButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton createStaffButton;
    private JTable userTable;
    private JScrollPane userScroll;
    private JPanel panelCreateStaff;
    private JTextField textFieldUsername;
    private JTextField textFieldPassword;
    private JTextField textFieldConfirm;
    private JButton SAVEButton;
    private JButton CANCELButton;
    private JPasswordField PasswordFieldConfirm;
    private JPasswordField PasswordFieldPassword;
    private JButton ADDAPPOINTMENTButton;
    private JButton EDITButton;
    private JButton DELETEButton;
    private JTable scheduleTable;
    private JButton homeButton;
    private JPanel card;
    private JPanel container;
    private JScrollPane patientScroll;
    private JPanel panelCreatePatient;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField4;
    private JTextField textField5;
    private JComboBox comboBox2;
    private JButton submitButton;
    private JTextField textField3;
    private JComboBox comboBox1;
    private JFrame frame;

    private JPanel createSimplifiedPatientCard(Map<String, String> patientData) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0x3399FF), 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.anchor = GridBagConstraints.WEST;

        String dateString = patientData.get("schedule_date");
        String apptDate = (dateString != null) ? dateString.split(" ")[0] : "N/A";

        String[][] displayData = {
                {"Name:", patientData.get("name")},
                {"Appointment Date:", apptDate},
                {"Appointment Schedule:", patientData.get("schedule_time")},
                {"Status:", patientData.get("status")}
        };

        Font labelFont = new Font("Century Gothic", Font.BOLD, 14);
        Font dataFont = new Font("Century Gothic", Font.PLAIN, 14);

        for (int i = 0; i < displayData.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.0;
            JLabel label = new JLabel(displayData[i][0]);
            label.setFont(labelFont);
            card.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            JLabel value = new JLabel(displayData[i][1]);
            value.setFont(dataFont);

            if (displayData[i][0].equals("Status:") && "Scheduled".equalsIgnoreCase(displayData[i][1])) {
                value.setForeground(new Color(0x28A745));
            }
            card.add(value, gbc);
        }

        card.putClientProperty("id", Integer.parseInt(patientData.get("id")));

        return card;
    }

    //

    private void deleteUserFromDatabase(int id) {
        DatabaseManager db = new DatabaseManager();
        boolean success = db.deleteUser(id);

        if (success) {
            JOptionPane.showMessageDialog(frame, "User deleted successfully!");
            loadUsersToTable();
        } else {
            JOptionPane.showMessageDialog(frame, "Error deleting user. Please check the logs.");
        }
    }

    public void loadUsersToTable() {
        String[] columns = {"ID", "Username", "Password"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DatabaseManager db = new DatabaseManager();
        List<Object[]> users = db.getAllUsers();

        for (Object[] user : users) {
            model.addRow(user);
        }

        userTable.setModel(model);
        userTable.getColumnModel().getColumn(0).setMinWidth(0);
        userTable.getColumnModel().getColumn(0).setMaxWidth(0);
        userTable.getColumnModel().getColumn(0).setWidth(0);
    }

    public void loadSimplifiedList() {
        JPanel patientContainer = new JPanel();
        patientContainer.setLayout(new BoxLayout(patientContainer, BoxLayout.Y_AXIS));
        patientContainer.setBackground(new Color(245, 245, 245));

        DatabaseManager db = new DatabaseManager();
        List<Map<String, String>> patients = db.getAllPatients();

        if (patients.isEmpty()) {
        } else {
            for (Map<String, String> patientData : patients) {
                JPanel patientCard = createSimplifiedPatientCard(patientData);
                patientCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
                patientContainer.add(patientCard);
                patientContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        patientScroll.setViewportView(patientContainer);
        patientScroll.revalidate();
        patientScroll.repaint();
    }

    public RegistrationFormAdmin() {
        JFrame frame = new JFrame("Truema Clinic - Admin");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setContentPane(this.panelUserAdmin);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTableHeader header = userTable.getTableHeader();
            header.setForeground(new Color(0xC9BDD));
            header.setBackground(Color.WHITE);
            header.setFont(new Font("Century Gothic", Font.BOLD, 14));

        CardLayout cl = (CardLayout) cardPanel.getLayout();

        viewStaffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(cardPanel, "Card1");
                loadUsersToTable();
            }
        });

        patientRegistrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(cardPanel, "Card2");
                loadSimplifiedList();
            }
        });

        scheduleViewingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(cardPanel, "Card3");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login();
                frame.dispose();
            }
        });


        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUsersToTable();
            }
        });


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();

                if (selectedRow == 0) {
                    JOptionPane.showMessageDialog(frame, "The first row is protected and cannot be deleted.");
                    return;
                }

                if (selectedRow != -1) {
                    Object idValue = userTable.getValueAt(selectedRow, 0);
                    int userId = Integer.parseInt(idValue.toString());
                    int confirm = JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to delete this user:?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteUserFromDatabase(userId);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a user from the table first.");
                }
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a user first.");
                    return;
                }

                JPasswordField newPassField = new JPasswordField();
                JPasswordField confirmPassField = new JPasswordField();
                Object[] message = {
                        "New Password:", newPassField,
                        "Confirm Password:", confirmPassField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Update Password", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String newPass = new String(newPassField.getPassword());
                    String confirmPass = new String(confirmPassField.getPassword());

                    if (newPass.isEmpty() || !newPass.equals(confirmPass)) {
                        JOptionPane.showMessageDialog(null, "Passwords must match and cannot be empty.");
                        return;
                    }

                    String username = userTable.getValueAt(selectedRow, 1).toString();

                    DatabaseManager db = new DatabaseManager();
                    boolean success = db.updatePassword(username, newPass);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Password updated successfully!");
                        loadUsersToTable();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: Could not update database.");
                    }
                }
            }
        });

        createStaffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(cardPanel, "Card4");
            }
        });
        CANCELButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(cardPanel, "Card1");
            }
        });
        SAVEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newStaff = textFieldUsername.getText();
                String newPassword = new String(PasswordFieldPassword.getPassword());
                String newConfirm = new String(PasswordFieldConfirm.getPassword());

                if (newStaff.isEmpty() || newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }
                if (newPassword.equals(newConfirm)) {
                    DatabaseManager db = new DatabaseManager();
                    boolean success = db.registerStaff(newStaff, newPassword);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Registration Successful!");
                        textFieldUsername.setText("");
                        PasswordFieldPassword.setText("");
                        PasswordFieldConfirm.setText("");
                        cl.show(cardPanel, "Card1");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Passwords do not match!");
                }
            }
        });

        ADDAPPOINTMENTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(cardPanel, "Card5");
            }
        });

        frame.setVisible(true);


    }
}
