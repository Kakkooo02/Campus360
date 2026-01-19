import java.awt.*;
import javax.swing.*;

class AdminDashboard extends JFrame {

    private final User adminUser;

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        return button;
    }

    public AdminDashboard(User user) {
        this.adminUser = user;

        setTitle("Admin Dashboard - Campus360");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ----- Top bar: Welcome + Logout -----
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcome = new JLabel("Welcome Admin: " + user.getFullName(), SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        topBar.add(welcome, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPage().setVisible(true);
            }
        });
        logoutBtn.setBackground(new Color(0, 60, 110));
        logoutBtn.setForeground(Color.WHITE);
        topBar.add(logoutBtn, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ----- Main buttons area -----
        JPanel main = new JPanel(new GridLayout(6, 1, 10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton accountBtn = createButton("My Account");
        accountBtn.addActionListener(e -> new AccountSettingsWindow(user));
        accountBtn.setBackground(new Color(0, 60, 110));
        accountBtn.setForeground(Color.WHITE);
        main.add(accountBtn);

        JButton manageUsersBtn = createButton("Manage Users");
        manageUsersBtn.addActionListener(e -> new ManageUsersWindow(user));
        manageUsersBtn.setBackground(new Color(0, 60, 110));
        manageUsersBtn.setForeground(Color.WHITE);
        main.add(manageUsersBtn);

        JButton maintBtn = createButton("Manage Maintenance");
        maintBtn.addActionListener(e -> {
            dispose();
            MaintenanceService svc = new MaintenanceService();
            new MainFrame(adminUser, svc).setVisible(true);
        });
        maintBtn.setBackground(new Color(0, 60, 110));
        maintBtn.setForeground(Color.WHITE);
        main.add(maintBtn);

        JButton approveBookingsBtn = createButton("Approve Bookings");
        approveBookingsBtn.addActionListener(e -> {
            dispose();
            new BookingAdmin(adminUser).setVisible(true);
        });
        approveBookingsBtn.setBackground(new Color(0, 60, 110));
        approveBookingsBtn.setForeground(Color.WHITE);
        main.add(approveBookingsBtn);

        JButton manageEventsBtn = createButton("Manage Events");
        manageEventsBtn.addActionListener(e -> new AdminEventApproval(adminUser));
        manageEventsBtn.setBackground(new Color(0, 60, 110));
        manageEventsBtn.setForeground(Color.WHITE);
        main.add(manageEventsBtn);

        JButton viewCalendarBtn = createButton("View Calendar");
        viewCalendarBtn.addActionListener(e -> new CalendarView(adminUser));
        viewCalendarBtn.setBackground(new Color(0, 60, 110));
        viewCalendarBtn.setForeground(Color.WHITE);
        main.add(viewCalendarBtn);

        add(main, BorderLayout.CENTER);

        setVisible(true);
    }
}
