import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    private final User user;
    private final MaintenanceService svc;
    private final Runnable onLogout; // optional callback (unused here, kept for future)
    private final JFrame parent;     // <— who opened this window (dashboard)

    public MainFrame(User user, MaintenanceService svc) {
        this(user, svc, null);
    }

    // NEW: parent-aware constructor so Back returns correctly
    public MainFrame(User user, MaintenanceService svc, JFrame parent) {
        super("Smart Campus Maintenance System - " + user.getFullName());
        this.user = user;
        this.svc = svc;
        this.onLogout = null;
        this.parent = parent;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // ensure BorderLayout for NORTH + CENTER

        // ===== Top bar (Back for everyone + Title + Logout for everyone) =====
        JPanel topBar = new JPanel(new BorderLayout(8, 8));
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Back (always shown)
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose();                          // close MainFrame
            if (parent != null) {
                parent.setVisible(true);        // return to the dashboard that opened this
            } else {
                // Fallback: if no parent provided, open the right dashboard based on role
                switch (user.getRole()) {
                    case STUDENT -> new StudentDashboard(user);
                    case FACULTY -> new FacultyDashboard(user);
                    case TECHNICIAN -> new TechnicianDashboard(user);
                    case ADMIN -> new AdminDashboard(user);
                }
            }
        });
        topBar.add(backBtn, BorderLayout.WEST);

        // Title
        JLabel title = new JLabel("Smart Campus Maintenance System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topBar.add(title, BorderLayout.CENTER);

        // Logout (always shown)
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to log out?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPage().setVisible(true);
            }
        });
        topBar.add(logoutBtn, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ===== Tabs =====
        JTabbedPane tabs = new JTabbedPane();

        // Requesters: Students and Faculty
        if (user.getRole() == Role.STUDENT || user.getRole() == Role.FACULTY)
            tabs.add("My Requests", requesterPanel());

        // Admin view: Head assigns work
        if (user.getRole() == Role.ADMIN)
            tabs.add("Head - Assign Work", adminPanel());

        // Technician view: sees own assigned work
        if (user.getRole() == Role.TECHNICIAN)
            tabs.add("My Work", technicianPanel());

        // All users can view all requests
        tabs.add("All Requests", allPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // ====== existing panels (unchanged from your version) ======

    private JPanel requesterPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField title = new JTextField();
        JTextField location = new JTextField();
        JTextArea desc = new JTextArea(3, 20);
        JButton create = new JButton("Create Maintenance Request");

        form.add(new JLabel("Title:"));
        form.add(title);
        form.add(new JLabel("Location:"));
        form.add(location);
        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(desc));
        form.add(new JLabel());
        form.add(create);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Status", "Assigned To"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        JButton reopen = new JButton("Reopen Selected");
        JButton refresh = new JButton("Refresh");
        JButton hist = new JButton("View History");

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(hist);
        south.add(reopen);
        south.add(refresh);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);

        Runnable reload = () -> {
            model.setRowCount(0);
            // You likely want listByCreator(user.getUserId()) if you store IDs; currently uses full name
            for (MaintenanceRequest r : svc.listByCreator(user.getFullName())) {
                model.addRow(new Object[]{r.getId(), r.getTitle(), r.getStatus(), r.getAssignedTo()});
            }
        };
        reload.run();

        create.addActionListener(e -> {
            if (title.getText().isBlank() || location.getText().isBlank()) {
                JOptionPane.showMessageDialog(p, "Title and Location are required.");
                return;
            }
            svc.create(title.getText().trim(), desc.getText().trim(), location.getText().trim(), user);
            title.setText("");
            location.setText("");
            desc.setText("");
            reload.run();
        });

        reopen.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(p, "Select a request first.");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            String note = JOptionPane.showInputDialog(p, "Reason to reopen:");
            svc.reopen(id, user, note);
            reload.run();
        });

        refresh.addActionListener(e -> reload.run());

        hist.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(p, "Select a request first.");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            new HistoryDialog(this, id).setVisible(true);
        });

        return p;
    }

    private JPanel adminPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        JComboBox<String> techs = new JComboBox<>();
        svc.listTechnicians().forEach(t -> techs.addItem(t.getFullName()));

        JButton assign = new JButton("Assign to Technician");
        JButton refresh = new JButton("Refresh");

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(new JLabel("Technician:"));
        south.add(techs);
        south.add(assign);
        south.add(refresh);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);

        Runnable reload = () -> {
            model.setRowCount(0);
            for (MaintenanceRequest r : svc.listUnassigned()) {
                model.addRow(new Object[]{r.getId(), r.getTitle(), r.getStatus()});
            }
        };
        reload.run();

        assign.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(p, "Select a request first.");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            String tech = (String) techs.getSelectedItem();
            svc.assign(id, tech, user);
            reload.run();
        });

        refresh.addActionListener(e -> reload.run());
        return p;
    }

    private JPanel technicianPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Status", "Location"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        JComboBox<Status> next = new JComboBox<>(
                new Status[]{Status.IN_PROGRESS, Status.ON_HOLD, Status.COMPLETED, Status.CANCELLED});
        JTextField note = new JTextField();
        JButton update = new JButton("Update Status");
        JButton refresh = new JButton("Refresh");
        JButton hist = new JButton("View History");

        JPanel south = new JPanel(new GridLayout(2, 1, 6, 6));
        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("Next status:"));
        line1.add(next);
        line1.add(new JLabel("Note:"));
        note.setColumns(25);
        line1.add(note);
        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        line2.add(hist);
        line2.add(update);
        line2.add(refresh);
        south.add(line1);
        south.add(line2);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);

        Runnable reload = () -> {
            model.setRowCount(0);
            for (MaintenanceRequest r : svc.listAll()) {
                if (user.getFullName().equals(r.getAssignedTo())) {
                    model.addRow(new Object[]{r.getId(), r.getTitle(), r.getStatus(), r.getLocation()});
                }
            }
        };
        reload.run();

        update.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(p, "Select a request first.");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            Status s = (Status) next.getSelectedItem();
            svc.updateStatus(id, s, user, note.getText());
            note.setText("");
            reload.run();
        });

        refresh.addActionListener(e -> reload.run());

        hist.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(p, "Select a request first.");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            new HistoryDialog(this, id).setVisible(true);
        });

        return p;
    }

    private JPanel allPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Status", "Created By", "Assigned To", "Location", "Reopens"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        JButton refresh = new JButton("Refresh");
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(refresh, BorderLayout.SOUTH);

        Runnable reload = () -> {
            model.setRowCount(0);
            for (MaintenanceRequest r : svc.listAll()) {
                model.addRow(new Object[]{r.getId(), r.getTitle(), r.getStatus(),
                        r.getCreatedBy(), r.getAssignedTo(), r.getLocation(), r.getReopenCount()});
            }
        };
        reload.run();

        refresh.addActionListener(e -> reload.run());
        return p;
    }
}

