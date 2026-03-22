package UserInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import Database.DatabaseManager;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class RegistrationFormAdmin {
    private JPanel panelUserAdmin;
    private JButton viewStaffButton, patientRegistrationButton, scheduleViewingButton, logoutButton;
    private JPanel cardPanel, panelStaff, panelRegisteration, panelSchedule;
    private JButton refreshButton, deleteButton, editButton, createStaffButton;
    private JTable userTable;
    private JScrollPane userScroll;
    private JPanel panelCreateStaff;
    private JTextField textFieldUsername, textFieldPassword, textFieldConfirm;
    private JButton SAVEButton, CANCELButton;
    private JPasswordField PasswordFieldConfirm, PasswordFieldPassword;
    private JButton ADDAPPOINTMENTButton, EDITButton, DELETEButton;
    private JTable scheduleTable;
    private JButton homeButton;
    private JPanel patientCard, container;
    private JScrollPane patientScroll;
    private JPanel panelCreatePatient;
    private JTextField textField1, textField2, textField4, textField5;
    private JButton submitButton;
    private JTextField textField3;
    private JComboBox comboBox1; private JComboBox comboBox3, comboBox4, yearComboBox, comboBox2, comboBox5, comboBox6, birthYearComboBox, comboBox7, dateFilter;
    private JPanel panelStaffButton;
    private JPanel panelRegistrationSub;
    private JScrollPane scheduleScroll;
    private JPanel panelRegistrationButton;
    private JFrame frame;
    private JPanel selectedPatientCard = null;

    private JPanel createSimplifiedPatientCard(Map<String, String> patientData) {
        JPanel patientCard = new JPanel(new GridBagLayout());
        patientCard.setBackground(Color.WHITE);
        patientCard.setBorder(BorderFactory.createLineBorder(new Color(0xC9BDD), 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.anchor = GridBagConstraints.WEST;

        int patientId = Integer.parseInt(patientData.get("id"));
        patientCard.putClientProperty("id", patientId);

        MouseAdapter cardListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedPatientCard != null) {
                    selectedPatientCard.setBorder(BorderFactory.createLineBorder(new Color(0xC9BDD), 1));
                }
                selectedPatientCard = patientCard;
                patientCard.setBorder(BorderFactory.createLineBorder(new Color(0xC9BDD), 2));

                if (e.getClickCount() == 2) {
                    new ViewPatientInfo(patientId);
                }
            }
        };

        patientCard.addMouseListener(cardListener);
        String dateString = patientData.get("schedule_date");
        String apptDate = (dateString != null) ? dateString : "N/A";

        String[][] displayData = {
                {"Name:", patientData.get("name")},
                {"Appointment Date:", apptDate},
                {"Appointment Schedule:", patientData.get("schedule_time")},
                {"Type:", patientData.get("schedule_type")},
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
            label.addMouseListener(cardListener); // 3. Attach to the label
            patientCard.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            JLabel value = new JLabel(displayData[i][1]);
            value.setFont(dataFont);
            value.addMouseListener(cardListener); // 4. Attach to the value text

            if (displayData[i][0].equals("Status:") && "Scheduled".equalsIgnoreCase(displayData[i][1])) {
                value.setForeground(new Color(0x28A745));
            }
            patientCard.add(value, gbc);
        }

        return patientCard;
    }

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
        selectedPatientCard = null;
        JPanel patientContainer = new JPanel();
        patientContainer.setLayout(new BoxLayout(patientContainer, BoxLayout.Y_AXIS));
        patientContainer.setBackground(new Color(245, 245, 245));

        DatabaseManager db = new DatabaseManager();
        List<Map<String, String>> patients = db.getAllPatients();

        if (!patients.isEmpty()) {
            for (Map<String, String> patientData : patients) {
                JPanel patientCard = createSimplifiedPatientCard(patientData);
                patientCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Fixed height
                patientContainer.add(patientCard);
                patientContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            patientContainer.add(Box.createVerticalGlue());
        }

        patientScroll.setViewportView(patientContainer);
        patientScroll.revalidate();
        patientScroll.repaint();
    }

    public void loadScheduleTable(String sortOrder) {
        String[] columns = {"ID", "Date", "Time", "Client Name", "Status"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only 'Status' is editable
            }
        };

        DatabaseManager db = new DatabaseManager();
        List<Map<String, String>> patients = db.getAllPatients();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM dd, yyyy");

        patients.sort((p1, p2) -> {
            try {
                java.util.Date d1 = sdf.parse(p1.get("schedule_date"));
                java.util.Date d2 = sdf.parse(p2.get("schedule_date"));

                if ("Latest".equals(sortOrder)) {
                    return d2.compareTo(d1); // Newest to Oldest
                } else {
                    return d1.compareTo(d2); // Oldest to Newest
                }
            } catch (Exception e) {
                return 0;
            }
        });

        for (Map<String, String> p : patients) {
            model.addRow(new Object[]{
                    p.get("id"),
                    p.get("schedule_date"),
                    p.get("schedule_time"),
                    p.get("name"),
                    p.get("status")
            });
        }

        scheduleTable.setModel(model);
        scheduleTable.getTableHeader().setFont(new Font("Century Gothic", Font.BOLD, 14));
        scheduleTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        scheduleTable.setRowHeight(30);
        scheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Fixes the right margin

        scheduleTable.getColumnModel().getColumn(0).setMinWidth(0);
        scheduleTable.getColumnModel().getColumn(0).setMaxWidth(0);
        scheduleTable.getColumnModel().getColumn(0).setWidth(0);

        String[] statusOptions = {"Scheduled", "Completed", "Cancelled", "No Show"};
        JComboBox<String> statusDropdown = new JComboBox<>(statusOptions);
        scheduleTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusDropdown));

        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 4) {
                    int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                    String newStatus = model.getValueAt(row, col).toString();
                    if (db.updatePatientStatus(id, newStatus)) {
                        loadSimplifiedList();
                    }
                }
            }
        });
    }

    public RegistrationFormAdmin() {
        if (yearComboBox != null) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int startYear = currentYear - 100;
            int endYear = currentYear + 10;

            for (int i = endYear; i >= startYear; i--) {
                yearComboBox.addItem(i);
            }
            yearComboBox.setSelectedItem(currentYear);
        }

        if (birthYearComboBox != null) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int startYear = currentYear - 100;
            int endYear = currentYear + 10;

            for (int i = endYear; i >= startYear; i--) {
                birthYearComboBox.addItem(i);
            }
            birthYearComboBox.setSelectedItem(currentYear);
        }

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
        patientScroll.getVerticalScrollBar().setUnitIncrement(16);

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
                String currentFilter = (dateFilter.getSelectedItem() != null) ?
                        dateFilter.getSelectedItem().toString() : "Latest";
                loadScheduleTable("Latest");
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


        textField3.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String text = textField3.getText();
                if (!Character.isDigit(c) || text.length() >= 3) {
                    e.consume();
                }
            }
        });

        textField5.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String num = textField5.getText();
                if (!Character.isDigit(c) || num.length() >= 11) {
                    e.consume();
                }
            }
        });

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                boolean isAllowed = Character.isLetter(c) ||
                        c == ' ' ||
                        c == '.' ||
                        c == KeyEvent.VK_BACK_SPACE ||
                        c == KeyEvent.VK_DELETE;
                if (!isAllowed) {
                    e.consume();
                }
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = textField1.getText().trim();
                    String address = textField2.getText().trim();
                    String ageText = textField3.getText().trim();
                    String email = textField4.getText().trim();
                    String contact = textField5.getText().trim();

                    if (name.isEmpty() || ageText.isEmpty() || contact.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill in Name, Age, and Contact Number.");
                        return;
                    }

                    String sex = comboBox1.getSelectedItem().toString();
                    String apptTime = comboBox2.getSelectedItem().toString();

                    String dob = comboBox5.getSelectedItem() + " " +
                            comboBox6.getSelectedItem() + ", " +
                            birthYearComboBox.getSelectedItem();

                    String apptDate = comboBox3.getSelectedItem() + " " +
                            comboBox4.getSelectedItem() + ", " +
                            yearComboBox.getSelectedItem();

                    String apptType = comboBox7.getSelectedItem().toString();

                    DatabaseManager db = new DatabaseManager();
                    int age = Integer.parseInt(ageText);

                    boolean success = db.savePatient(name, age, dob, sex, address, email, contact, apptDate, apptTime, apptType);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Patient Registered Successfully!");
                        loadSimplifiedList();
                        textField1.setText("");
                        textField2.setText("");
                        textField3.setText("");
                        textField4.setText("");
                        textField5.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to save to database.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number for Age.");
                }
            }
        });

        DELETEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedPatientCard == null) {
                    JOptionPane.showMessageDialog(null, "Please select a patient record first!");
                    return;
                }

                int patientId = (int) selectedPatientCard.getClientProperty("id");

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this appointment?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    DatabaseManager db = new DatabaseManager();
                    if (db.deletePatient(patientId)) {
                        JOptionPane.showMessageDialog(null, "Deleted successfully.");
                        selectedPatientCard = null;
                        loadSimplifiedList();
                    }
                }
            }
        });

        EDITButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedPatientCard == null) {
                    JOptionPane.showMessageDialog(null, "Please select a patient patientCard first!");
                    return;
                }
                int id = (int) selectedPatientCard.getClientProperty("id");
                new EditPatient(id, RegistrationFormAdmin.this);
            }
        });

        dateFilter.addActionListener(e -> {
            String selected = dateFilter.getSelectedItem().toString();
            loadScheduleTable(selected);
        });

        frame.setVisible(true);
    }
}
