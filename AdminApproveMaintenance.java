import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminApproveMaintenance extends JFrame {
    private final MaintenanceService svc;
    private final User adminUser;
    private final DefaultTableModel model;
    private JComboBox<String> technicianBox;

    public AdminApproveMaintenance(User adminUser) {
        this.adminUser = adminUser;
        this.svc = new MaintenanceService();

        setTitle("Approve & Assign Maintenance Requests");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel header = new JLabel("Pending Maintenance Requests", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID", "Title", "Status", "Created By", "Assigned To"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        loadRequests();

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Technician dropdown
        JPanel techPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        technicianBox = new JComboBox<>();
        loadTechnicians();
        techPanel.add(new JLabel("Technician:"));
        techPanel.add(technicianBox);
        add(techPanel, BorderLayout.NORTH);

        // Buttons
        JButton assignBtn = new JButton("Assign Technician");
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.add(assignBtn);
        bottom.add(approveBtn);
        bottom.add(rejectBtn);
        bottom.add(refreshBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // --- Actions ---
        assignBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a request first.");
                return;
            }

            String id = (String) model.getValueAt(row, 0);
            String tech = (String) technicianBox.getSelectedItem();
            if (tech == null || tech.startsWith("No")) {
                JOptionPane.showMessageDialog(this, "No technician available to assign.");
                return;
            }

            // Extract technician ID from combo box text
            String techId = tech.substring(tech.indexOf('(') + 1, tech.indexOf(')'));
            svc.assign(id, techId, adminUser);

            JOptionPane.showMessageDialog(this, "Technician assigned successfully!");
            loadRequests();
        });

        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a request first.");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            svc.updateStatus(id, Status.APPROVED, adminUser, "Approved by admin");
            JOptionPane.showMessageDialog(this, "Request approved.");
            loadRequests();
        });

        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a request first.");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            svc.updateStatus(id, Status.REJECTED, adminUser, "Rejected by admin");
            JOptionPane.showMessageDialog(this, "Request rejected.");
            loadRequests();
        });

        refreshBtn.addActionListener(e -> {
            loadRequests();
            loadTechnicians();
        });

        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(adminUser).setVisible(true);
        });

        setVisible(true);
    }

    private void loadRequests() {
        model.setRowCount(0);
        List<MaintenanceRequest> list = svc.listAll();
        for (MaintenanceRequest r : list) {
            if (r.getStatus() == Status.PENDING || r.getStatus() == Status.REOPENED) {
                model.addRow(new Object[]{
                        r.getId(),
                        r.getTitle(),
                        r.getStatus(),
                        r.getCreatedBy(),
                        r.getAssignedTo()
                });
            }
        }
    }

    private void loadTechnicians() {
        technicianBox.removeAllItems();
        List<User> techs = svc.listTechnicians();

        if (techs == null || techs.isEmpty()) {
            technicianBox.addItem("No technicians available");
            technicianBox.setEnabled(false);
            return;
        }

        technicianBox.setEnabled(true);
        for (User t : techs) {
            String displayName = (t.getFullName() != null && !t.getFullName().isEmpty())
                    ? t.getFullName()
                    : t.getUserId(); // fallback if full name missing
            technicianBox.addItem(displayName + " (" + t.getUserId() + ")");
        }
    }
}
