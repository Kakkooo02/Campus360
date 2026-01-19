import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BookingAdmin extends JFrame {

    private final JTable bookingTable;
    private final DefaultTableModel tableModel;
    private final List<Booking> pendingBookings = new ArrayList<>();
    private final User adminUser;

    public BookingAdmin(User adminUser) {
        this.adminUser = adminUser;

        setTitle("Admin Approval");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

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

        JLabel title = new JLabel("Pending Approval Requests");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        topBar.add(Box.createHorizontalStrut(10));
        topBar.add(title);
        add(topBar, BorderLayout.NORTH);

        // 🔹 Table setup
        String[] columns = {"User ID", "User Name", "User Type", "Resource", "Room", "Date", "Start", "End", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // 🔹 Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");

        approveBtn.setBackground(new Color(0, 60, 110));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFocusPainted(false);
        approveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        rejectBtn.setBackground(new Color(180, 0, 0));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFocusPainted(false);
        rejectBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        approveBtn.addActionListener(e -> handleAction("Approved"));
        rejectBtn.addActionListener(e -> handleAction("Rejected"));

        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // 🔹 Load pending requests
        loadPendingBookings();
    }

    private void loadPendingBookings() {
        String[] files = {"labs.txt", "sports.txt", "classrooms.txt"};
        for (String file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split(", ");
                    if (parts.length >= 9) { // now supports ID at index 0
                        String id = parts[0];
                        String name = parts[1];
                        String userType = parts[2];
                        String resource = parts[3];
                        String room = parts[4];
                        LocalDate date = LocalDate.parse(parts[5]);
                        LocalTime start = LocalTime.parse(parts[6]);
                        LocalTime end = LocalTime.parse(parts[7]);
                        String status = parts[8];
                        if (status.equalsIgnoreCase("Pending Approval")) {
                            Booking b = new Booking(id, name, userType, resource, room, date, start, end, status);
                            pendingBookings.add(b);
                            tableModel.addRow(new Object[]{id, name, userType, resource, room, date, start, end, status});
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("No pending file found: " + file);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error reading file: " + file, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAction(String newStatus) {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to " + newStatus.toLowerCase() + ".", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking selectedBooking = pendingBookings.get(selectedRow);
        selectedBooking.status = newStatus;

        updateBookingFile(selectedBooking);

        pendingBookings.remove(selectedRow);
        tableModel.removeRow(selectedRow);

        JOptionPane.showMessageDialog(this, "Booking " + newStatus + " successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateBookingFile(Booking updatedBooking) {
        String filename = updatedBooking.resource.equalsIgnoreCase("Lab")
                ? "labs.txt"
                : updatedBooking.resource.equalsIgnoreCase("Sports Facility")
                ? "sports.txt"
                : "classrooms.txt";
        File tempFile = new File("temp.txt");
        File originalFile = new File(filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(", ");
                if (parts.length >= 9) {
                    String id = parts[0];
                    String name = parts[1];
                    String room = parts[4];
                    String date = parts[5];

                    if (name.equals(updatedBooking.name) &&
                        room.equals(updatedBooking.room) &&
                        date.equals(updatedBooking.date.toString())) {
                        line = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s",
                                updatedBooking.id, updatedBooking.name, updatedBooking.userType,
                                updatedBooking.resource, updatedBooking.room, updatedBooking.date,
                                updatedBooking.start, updatedBooking.end, updatedBooking.status);
                    }
                }
                writer.write(line + System.lineSeparator());
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating booking file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }
    }

    private void goBackHome() {
        dispose();
        new AdminDashboard(adminUser);
    }

    static class Booking {
        String id, name, userType, resource, room, status;
        LocalDate date;
        LocalTime start, end;

        Booking(String id, String name, String userType, String resource, String room,
                LocalDate date, LocalTime start, LocalTime end, String status) {
            this.id = id;
            this.name = name;
            this.userType = userType;
            this.resource = resource;
            this.room = room;
            this.date = date;
            this.start = start;
            this.end = end;
            this.status = status;
        }
    }
}
