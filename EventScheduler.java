import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventScheduler extends JFrame {
    private JTextField titleField, dateField, timeField, locationField;
    private final User user;

    public EventScheduler(User user) {
        this.user = user;

        setTitle("Schedule Event - Campus360");
        setSize(400, 350);
        setLayout(new GridLayout(6, 2, 8, 8));
        setLocationRelativeTo(null);

        add(new JLabel("Event Title:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        add(dateField);

        add(new JLabel("Time (HH:MM):"));
        timeField = new JTextField();
        add(timeField);

        add(new JLabel("Location:"));
        locationField = new JTextField();
        add(locationField);

        JButton submitBtn = new JButton("Submit for Approval");
        add(submitBtn);
        JButton closeBtn = new JButton("Close");
        add(closeBtn);

        submitBtn.addActionListener(e -> saveEvent());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void saveEvent() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("events.txt", true))) {
            bw.write(String.format("%s, %s, %s, %s, %s, %s, Pending Approval%n",
                    user.getFullName(), user.getRole(), titleField.getText().trim(),
                    dateField.getText().trim(), timeField.getText().trim(), locationField.getText().trim()));
            JOptionPane.showMessageDialog(this, "Event submitted for admin approval.");
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving event.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
