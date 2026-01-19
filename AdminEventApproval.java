import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminEventApproval extends JFrame {

    private final JTable eventTable;
    private final DefaultTableModel tableModel;
    private final List<Event> pendingEvents = new ArrayList<>();
    private final User adminUser;

    public AdminEventApproval(User adminUser) {
        this.adminUser = adminUser;

        setTitle("Event Approval - Campus360");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Pending Events for Approval", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBackground(new Color(0, 60, 110));
        title.setForeground(new Color(0, 60, 110));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Organizer", "Role", "Title", "Date", "Time", "Location", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        eventTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(eventTable);
        add(scroll, BorderLayout.CENTER);

        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");
        JButton backBtn = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(approveBtn);
        buttons.add(rejectBtn);
        buttons.add(refreshBtn);
        buttons.add(backBtn);
        add(buttons, BorderLayout.SOUTH);

        approveBtn.addActionListener(e -> handleAction("Approved"));
        rejectBtn.addActionListener(e -> handleAction("Rejected"));
        refreshBtn.addActionListener(e -> loadPendingEvents());
        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(adminUser).setVisible(true);
        });

        loadPendingEvents();
        setVisible(true);
    }

    private void loadPendingEvents() {
        pendingEvents.clear();
        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("events.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(", ");
                if (p.length >= 7 && p[6].equalsIgnoreCase("Pending Approval")) {
                    Event ev = new Event(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
                    pendingEvents.add(ev);
                    tableModel.addRow(p);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading events.txt: " + e.getMessage());
        }
    }

    private void handleAction(String newStatus) {
        int row = eventTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to update.");
            return;
        }
        Event selected = pendingEvents.get(row);
        selected.status = newStatus;
        updateEventFile(selected);
        loadPendingEvents();
        JOptionPane.showMessageDialog(this, "Event " + newStatus.toLowerCase() + " successfully!");
    }

    private void updateEventFile(Event updated) {
        File original = new File("events.txt");
        File temp = new File("temp.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(original));
             BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(", ");
                if (p.length >= 7 && p[0].equals(updated.organizer) && p[2].equals(updated.title)) {
                    line = String.format("%s, %s, %s, %s, %s, %s, %s",
                            updated.organizer, updated.role, updated.title, updated.date,
                            updated.time, updated.location, updated.status);
                }
                bw.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating event file.");
        }

        if (original.delete()) temp.renameTo(original);
    }

    static class Event {
        String organizer, role, title, date, time, location, status;
        Event(String o, String r, String t, String d, String ti, String l, String s) {
            organizer = o; role = r; title = t; date = d; time = ti; location = l; status = s;
        }
    }
}
