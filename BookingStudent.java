import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class BookingStudent extends JFrame {
    private JTextField idField, nameField, roomField, dateField, startField, endField;
    private JComboBox<String> resourceBox;
    private final List<Booking> bookings = new ArrayList<>();
    private final User student;

    // ✅ Constructor receives logged-in user
    public BookingStudent(User student) {
        this.student = student;

        setTitle("Student Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        loadExistingBookings();

        // 🔹 Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(0, 60, 110));

        JButton backBtn = new JButton("Back");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(0, 60, 110));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBackHome());
        topBar.add(backBtn);

        JButton myBookingsBtn = new JButton("My Bookings");
        myBookingsBtn.setForeground(Color.WHITE);
        myBookingsBtn.setBackground(new Color(0, 60, 110));
        myBookingsBtn.setFocusPainted(false);
        myBookingsBtn.setBorderPainted(false);
        myBookingsBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        myBookingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        myBookingsBtn.addActionListener(e -> showMyBookings());
        topBar.add(myBookingsBtn);
        add(topBar, BorderLayout.NORTH);

        // 🔹 Booking form
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Book a Resource"));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Student ID:"));
        idField = new JTextField(student.getUserId());
        idField.setEditable(false);
        formPanel.add(idField);

        formPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField(student.getFullName());
        nameField.setEditable(false);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Resource Type:"));
        resourceBox = new JComboBox<>(new String[]{"Classroom", "Sports Facility"});
        formPanel.add(resourceBox);

        formPanel.add(new JLabel("Room / Facility No:"));
        roomField = new JTextField();
        formPanel.add(roomField);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Start Time (HH:MM):"));
        startField = new JTextField();
        formPanel.add(startField);

        formPanel.add(new JLabel("End Time (HH:MM):"));
        endField = new JTextField();
        formPanel.add(endField);

        JButton bookBtn = new JButton("Book Now");
        JButton clearBtn = new JButton("Clear");

        bookBtn.setBackground(new Color(0, 60, 110));
        bookBtn.setForeground(Color.WHITE);
        clearBtn.setBackground(new Color(200, 200, 200));

        formPanel.add(bookBtn);
        formPanel.add(clearBtn);
        add(formPanel, BorderLayout.CENTER);

        bookBtn.addActionListener(e -> handleBooking());
        clearBtn.addActionListener(e -> clearForm());
    }

    private void handleBooking() {
        try {
            String id = student.getUserId();
            String name = student.getFullName();
            String role = "Student";
            String resource = (String) resourceBox.getSelectedItem();
            String room = roomField.getText().trim().toUpperCase();
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime start = LocalTime.parse(startField.getText().trim());
            LocalTime end = LocalTime.parse(endField.getText().trim());

            if (room.isEmpty() || room.length() > 6) {
                JOptionPane.showMessageDialog(this,
                        "Room/Facility name must be entered (max 6 characters).",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Booking b : bookings) {
                if (b.resource.equalsIgnoreCase(resource) && b.room.equalsIgnoreCase(room) && b.date.equals(date)) {
                    boolean overlap = !(end.isBefore(b.start) || start.isAfter(b.end));
                    if (overlap) {
                        JOptionPane.showMessageDialog(this,
                                "Time slot already booked for " + room + "!",
                                "Conflict", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // All student bookings need admin approval (classrooms and sports)
            String status = "Pending Approval";

            Booking newBooking = new Booking(id, name, role, resource, room, date, start, end, status);
            bookings.add(newBooking);
            saveBookingToFile(newBooking);

            JOptionPane.showMessageDialog(this,
                    "Booking " + status + " for " + room + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadExistingBookings() {
        String[] files = {"classrooms.txt", "sports.txt"};
        for (String file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || line.trim().isEmpty()) continue;
                    String[] parts = line.split(", ");
                    if (parts.length == 9) {
                        bookings.add(new Booking(parts[0], parts[1], parts[2],
                                parts[3], parts[4],
                                LocalDate.parse(parts[5]), LocalTime.parse(parts[6]), LocalTime.parse(parts[7]), parts[8]));
                    }
                }
            } catch (IOException ignored) {}
        }
    }

    private void saveBookingToFile(Booking b) {
        String filename = b.resource.equals("Sports Facility") ? "sports.txt" : "classrooms.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s%n",
                    b.id, b.name, b.role, b.resource, b.room, b.date, b.start, b.end, b.status));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ✅ Back button: return to the same logged-in student dashboard
    private void goBackHome() {
        dispose();
        new StudentDashboard(student);
    }

    private void showMyBookings() {
        List<Booking> myBookings = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.id.equals(student.getUserId())) {
                myBookings.add(b);
            }
        }

        if (myBookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no bookings yet.", "My Bookings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columns = {"Resource", "Room", "Date", "Start", "End", "Status"};
        String[][] data = new String[myBookings.size()][columns.length];
        for (int i = 0; i < myBookings.size(); i++) {
            Booking b = myBookings.get(i);
            data[i][0] = b.resource;
            data[i][1] = b.room;
            data[i][2] = b.date.toString();
            data[i][3] = b.start.toString();
            data[i][4] = b.end.toString();
            data[i][5] = b.status;
        }

        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(550, Math.min(300, myBookings.size() * 22 + 50)));

        JOptionPane.showMessageDialog(this, scrollPane, "My Bookings", JOptionPane.PLAIN_MESSAGE);
    }

    private void clearForm() {
        roomField.setText("");
        dateField.setText("");
        startField.setText("");
        endField.setText("");
    }

    static class Booking {
        String id, name, role, resource, room, status;
        LocalDate date;
        LocalTime start, end;

        Booking(String id, String name, String role, String resource, String room, LocalDate date, LocalTime start, LocalTime end, String status) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.resource = resource;
            this.room = room;
            this.date = date;
            this.start = start;
            this.end = end;
            this.status = status;
        }
    }
}
