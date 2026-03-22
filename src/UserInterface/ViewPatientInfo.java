package UserInterface;

import Database.DatabaseManager;
import javax.swing.*;
import java.util.Map;

public class ViewPatientInfo {
    private JPanel panel1;
    private JTextField nameField;
    private JTextField homeField;
    private JTextField ageField;
    private JTextField sexField;
    private JTextField emailField;
    private JTextField contactField;
    private JTextField birthMonthField, birthDayField, birthYearField;
    private JTextField dateField, timeField, typeField;
    private JButton backButton;
    private JFrame frame;

    private void loadPatientDetails(int id) {
        DatabaseManager db = new DatabaseManager();
        Map<String, String> data = db.getPatientById(id);

        if (data != null) {
            nameField.setText(data.get("name"));
            homeField.setText(data.get("address"));
            ageField.setText(data.get("age"));
            sexField.setText(data.get("sex"));
            emailField.setText(data.get("email"));
            contactField.setText(data.get("contact"));
            dateField.setText(data.get("sDate"));
            timeField.setText(data.get("sTime"));
            typeField.setText(data.get("sType"));

            String dob = data.get("dob");
            if (dob != null && dob.contains(" ")) {
                String[] parts = dob.replace(",", "").split(" ");
                if (parts.length == 3) {
                    birthMonthField.setText(parts[0]);
                    birthDayField.setText(parts[1]);
                    birthYearField.setText(parts[2]);
                }
            }

            JTextField[] fields = {nameField, homeField, ageField, sexField, emailField,
                    contactField, birthMonthField, birthDayField,
                    birthYearField, dateField, timeField, typeField};
            for (JTextField f : fields) {
                if (f != null) f.setEditable(false);
            }
        }
    }

    public ViewPatientInfo(int patientId) {
        frame = new JFrame("Patient Information");
        frame.setContentPane(this.panel1);
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        loadPatientDetails(patientId);

        backButton.addActionListener(e -> frame.dispose());
        frame.setVisible(true);
    }
}