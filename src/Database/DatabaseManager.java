package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class DatabaseManager {
    public int login(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean registerStaff(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Returns true if insert was successful

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null, "Username already exists!");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
        public boolean updatePassword(String username, String newPassword){
            String query = "UPDATE users SET password = ? WHERE username = ?";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, newPassword);
                pstmt.setString(2, username);

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;

            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getAllUsers() {
        List<Object[]> userList = new ArrayList<>();
        String sql = "SELECT id, username, password FROM users";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                userList.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public List<Map<String, String>> getAllPatients() {
        List<Map<String, String>> patientList = new ArrayList<>();
        String sql = "SELECT * FROM patients";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, String> patient = new HashMap<>();
                patient.put("id", String.valueOf(rs.getInt("id")));
                patient.put("name", rs.getString("name"));
                patient.put("schedule_date", rs.getString("schedule_date"));
                patient.put("schedule_time", rs.getString("schedule_time"));
                patient.put("schedule_type", rs.getString("schedule_type"));
                patient.put("status", rs.getString("status"));
                patientList.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patientList;
    }

    public boolean savePatient(String name, int age, String dob, String sex, String addr,
                               String email, String contact, String sDate, String sTime, String sType) {
        String sql = "INSERT INTO patients (name, age, date_birth, sex, home_address, email, contact_number, schedule_date, schedule_time, schedule_type, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Scheduled')";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, dob);
            pstmt.setString(4, sex);
            pstmt.setString(5, addr);
            pstmt.setString(6, email);
            pstmt.setString(7, contact);
            pstmt.setString(8, sDate);
            pstmt.setString(9, sTime);
            pstmt.setString(10, sType);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Map<String, String> getPatientById(int id) {
        Map<String, String> data = new HashMap<>();
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                data.put("name", rs.getString("name"));
                data.put("age", String.valueOf(rs.getInt("age")));
                data.put("dob", rs.getString("date_birth"));
                data.put("sex", rs.getString("sex"));
                data.put("address", rs.getString("home_address"));
                data.put("email", rs.getString("email"));
                data.put("contact", rs.getString("contact_number"));
                data.put("sDate", rs.getString("schedule_date"));
                data.put("sTime", rs.getString("schedule_time"));
                data.put("sType", rs.getString("schedule_type"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public boolean updatePatientComplete(int id, String name, int age, String dob, String sex, String addr,
                                         String email, String contact, String sDate, String sTime, String sType) {
        String sql = "UPDATE patients SET name=?, age=?, date_birth=?, sex=?, home_address=?, email=?, " +
                "contact_number=?, schedule_date=?, schedule_time=?, schedule_type=? WHERE id=?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, dob);
            pstmt.setString(4, sex);
            pstmt.setString(5, addr);
            pstmt.setString(6, email);
            pstmt.setString(7, contact);
            pstmt.setString(8, sDate);
            pstmt.setString(9, sTime);
            pstmt.setString(10, sType);
            pstmt.setInt(11, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePatientStatus(int id, String status) {
        String sql = "UPDATE patients SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getPatientCount(String condition) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM patients " + condition;

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting patients: " + e.getMessage());
        }
        return count;
    }

}