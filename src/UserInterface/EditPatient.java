package UserInterface;

import javax.swing.*;

import Database.DatabaseManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Map;

public class EditPatient {
    private JPanel panelEdit;
    private JTextField textFieldName, textFieldAge, textFieldAddress, textFieldEmail, textFieldContact;
    private JComboBox comboSex, comboBirthMonth, comboBirthDay, comboBirthYear, comboApptMonth, comboApptDay, comboApptYear, comboTime, comboType;
    private JButton saveButton;
    private JButton updateButton;
    private JButton cancelButton;
    private int patientId;
    private Object mainUI;
    private JFrame frame;

    private void refreshMainList() {
        if (mainUI instanceof RegistrationFormAdmin) {
            ((RegistrationFormAdmin) mainUI).loadSimplifiedList();
        } else if (mainUI instanceof RegistrationFormStaff) {
            ((RegistrationFormStaff) mainUI).loadSimplifiedList();
        }
    }

    private void setupYearComboBoxes() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = currentYear - 100;
        int endYear = currentYear + 10;

        if (comboBirthYear != null) {
            for (int i = endYear; i >= startYear; i--) {
                comboBirthYear.addItem(i);
            }
            comboBirthYear.setSelectedItem(currentYear);
        }

        if (comboApptYear != null) {
            for (int i = endYear; i >= startYear; i--) {
                comboApptYear.addItem(i);
            }
            comboApptYear.setSelectedItem(currentYear);
        }
    }

    private void parseAndSetDate(String dateStr, JComboBox month, JComboBox day, JComboBox year) {
        if (dateStr == null || dateStr.isEmpty()) return;

        try {
            String[] parts = dateStr.replace(",", "").split(" ");
            if (parts.length == 3) {
                month.setSelectedItem(parts[0]);
                day.setSelectedItem(parts[1]);
                year.setSelectedItem(Integer.parseInt(parts[2]));
            }
        } catch (Exception e) {
            System.err.println("Could not parse date: " + dateStr);
        }
    }

    private void loadCurrentData() {
        DatabaseManager db = new DatabaseManager();
        Map<String, String> data = db.getPatientById(patientId);

        if (data != null && !data.isEmpty()) {
            textFieldName.setText(data.get("name"));
            textFieldAge.setText(data.get("age"));
            textFieldAddress.setText(data.get("address"));
            textFieldEmail.setText(data.get("email"));
            textFieldContact.setText(data.get("contact"));
            comboSex.setSelectedItem(data.get("sex"));
            comboType.setSelectedItem(data.get("sType"));
            comboTime.setSelectedItem(data.get("sTime"));

            parseAndSetDate(data.get("dob"), comboBirthMonth, comboBirthDay, comboBirthYear);
            parseAndSetDate(data.get("sDate"), comboApptMonth, comboApptDay, comboApptYear);
        }
    }

    private void saveChanges() {
        DatabaseManager db = new DatabaseManager();

        try {
            String dob = comboBirthMonth.getSelectedItem() + " " +
                    comboBirthDay.getSelectedItem() + ", " +
                    comboBirthYear.getSelectedItem();

            String apptDate = comboApptMonth.getSelectedItem() + " " +
                    comboApptDay.getSelectedItem() + ", " +
                    comboApptYear.getSelectedItem();

            boolean success = db.updatePatientComplete(
                    patientId,
                    textFieldName.getText().trim(),
                    Integer.parseInt(textFieldAge.getText().trim()),
                    dob,
                    comboSex.getSelectedItem().toString(),
                    textFieldAddress.getText().trim(),
                    textFieldEmail.getText().trim(),
                    textFieldContact.getText().trim(),
                    apptDate,
                    comboTime.getSelectedItem().toString(),
                    comboType.getSelectedItem().toString()
            );

            if (success) {
                JOptionPane.showMessageDialog(null, "Patient Updated!");
            } else {
                JOptionPane.showMessageDialog(null, "Update Failed.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for age.");
        }
    }

    public EditPatient(int id, Object callerUI) {
        this.patientId = id;
        this.mainUI = callerUI;

        frame = new JFrame("Edit Patient #" + id);
        frame.setContentPane(panelEdit);

        setupYearComboBoxes();
        loadCurrentData();

        frame.pack();
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        saveButton.addActionListener(e -> {
            saveChanges();
            frame.dispose();
            refreshMainList(); // Using the new helper method
        });

        updateButton.addActionListener(e -> {
            saveChanges();
            frame.dispose();
            refreshMainList(); // Using the new helper method
        });

        cancelButton.addActionListener(e -> frame.dispose());
    }
}