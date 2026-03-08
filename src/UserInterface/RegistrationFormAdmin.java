package UserInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


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
    private JFrame frame;

    //

    private void deleteUserFromDatabase(int id) {
        String url = "jdbc:sqlite:src/Database/truema.db";
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(frame, "User deleted successfully!");
                loadUsersToTable();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error deleting user: " + ex.getMessage());
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

        String url = "jdbc:sqlite:src/Database/truema.db";
        String sql = "SELECT id, username, password FROM users";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                });
            }
            userTable.setModel(model);
            userTable.getColumnModel().getColumn(0).setMinWidth(0);
            userTable.getColumnModel().getColumn(0).setMaxWidth(0);
            userTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage());
        }
    }

    public RegistrationFormAdmin() {
        JFrame frame = new JFrame("Truema Clinic - Admin");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setContentPane(this.panelUserAdmin);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


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

            }
        });

        createStaffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateAccount();
            }
        });
        frame.setVisible(true);
    }
}
