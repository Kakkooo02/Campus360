import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

// ======================================
// ROLE ENUM
// ======================================
enum Role {
    STUDENT,
    FACULTY,
    ADMIN,
    TECHNICIAN
}

// ======================================
// USER CLASS
// ======================================
class User {
    private String userId;
    private String fullName;
    private String email;
    private String password;
    private Role role;

    public User(String userId, String fullName, String email, String password, Role role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    public void setFullName(String v) { fullName = v; }
    public void setEmail(String v) { email = v; }
    public void setPassword(String v) { password = v; }
    public void setRole(Role r) { role = r; }
}

// ======================================
// USER MANAGER (FILE-BASED)
// ======================================
class UserManager {
    private static final String FILE_NAME = "users.txt";
    private static final ArrayList<User> users = new ArrayList<>();

    public static ArrayList<User> getUsers() { return users; }

    public static void loadUsersFromFile() {
        File file = new File(FILE_NAME);
        users.clear();

        if (!file.exists()) {
            users.add(new User("A001", "System Admin", "admin@ku.ac.ae", "admin123", Role.ADMIN));
            users.add(new User("S001", "Student Ali", "ali@student.ku.ac.ae", "1234", Role.STUDENT));
            users.add(new User("F001", "Dr. Ahmed", "ahmed@ku.ac.ae", "1234", Role.FACULTY));
            users.add(new User("T001", "Technician Omar", "tech@ku.ac.ae", "tech123", Role.TECHNICIAN));
            saveUsersToFile();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length < 5) continue;

                String id = parts[0].trim();
                String name = parts[1].trim();
                String email = parts[2].trim();
                String pass = parts[3].trim();
                String roleStr = parts[4].trim().toUpperCase();

                try {
                    Role role = Role.valueOf(roleStr);
                    users.add(new User(id, name, email, pass, role));
                } catch (IllegalArgumentException ignored) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveUsersToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (User u : users) {
                pw.println(u.getUserId() + ";" +
                        u.getFullName() + ";" +
                        u.getEmail() + ";" +
                        u.getPassword() + ";" +
                        u.getRole().name());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User u) {
        users.add(u);
        saveUsersToFile();
    }

    public static void removeUser(int index) {
        if (index >= 0 && index < users.size()) {
            users.remove(index);
            saveUsersToFile();
        }
    }

    public static User authenticate(String email, String password) {
        String eTrim = email.trim();
        String pTrim = password.trim();

        for (User u : users) {
            if (u.getEmail().trim().equalsIgnoreCase(eTrim)
                    && u.getPassword().trim().equals(pTrim)) {
                return u;
            }
        }
        return null;
    }
}

// ======================================
// STUDENT DASHBOARD (with Logout)
// ======================================
class StudentDashboard extends JFrame {
    private final User loggedInStudent;

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 16));
        return b;
    }

    public StudentDashboard(User user) {
        this.loggedInStudent = user;
        setTitle("Student Dashboard - Campus360");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top bar: Welcome + Logout
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcome = new JLabel("Student: " + user.getFullName(), SwingConstants.CENTER);
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

        // Main buttons area
        JPanel main = new JPanel(new GridLayout(6, 1, 10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton accountBtn = createButton("My Account");
        accountBtn.addActionListener(e -> new AccountSettingsWindow(user));
        accountBtn.setBackground(new Color(0, 60, 110));
        accountBtn.setForeground(Color.WHITE);
        main.add(accountBtn);

        JButton bookFacilityBtn = createButton("Book Facility");
        bookFacilityBtn.addActionListener(e -> {
            dispose();
            new BookingStudent(user).setVisible(true);
        });
        bookFacilityBtn.setBackground(new Color(0, 60, 110));
        bookFacilityBtn.setForeground(Color.WHITE);
        main.add(bookFacilityBtn);

        JButton requestMaintBtn = createButton("Request Maintenance");
        requestMaintBtn.addActionListener(e -> {
            dispose();
            MaintenanceService svc = new MaintenanceService();
            new MainFrame(loggedInStudent, svc).setVisible(true);
        });
        requestMaintBtn.setBackground(new Color(0, 60, 110));
        requestMaintBtn.setForeground(Color.WHITE);
        main.add(requestMaintBtn);

        JButton aiAssistantBtn = createButton("AI Assistant");
        aiAssistantBtn.addActionListener(e -> openAssistant(loggedInStudent));
        aiAssistantBtn.setBackground(new Color(0, 60, 110));
        aiAssistantBtn.setForeground(Color.WHITE);
        main.add(aiAssistantBtn);


        JButton scheduleEventBtn = createButton("Schedule Event");
        scheduleEventBtn.addActionListener(e -> new EventScheduler(user));
        scheduleEventBtn.setBackground(new Color(0, 60, 110));
        scheduleEventBtn.setForeground(Color.WHITE);
        main.add(scheduleEventBtn);

        JButton viewCalendarBtn = createButton("View Calendar");
        viewCalendarBtn.addActionListener(e -> new CalendarView(user));
        viewCalendarBtn.setBackground(new Color(0, 60, 110));
        viewCalendarBtn.setForeground(Color.WHITE);
        main.add(viewCalendarBtn);

        add(main, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openAssistant(User user) {
        try {
            String projectPath = "C:\\Users\\hp\\OneDrive\\Desktop\\campus360 - Copy (3) - Copy";
            String script = "app.py";
            writeUserContext(projectPath, user);
            String env = "set \"AI_USER_ID=" + user.getUserId() + "\" && " +
                    "set \"AI_USER_NAME=" + user.getFullName() + "\" && " +
                    "set \"AI_USER_EMAIL=" + user.getEmail() + "\" && " +
                    "set \"AI_USER_ROLE=" + formatRole(user.getRole()) + "\" && ";
            String command = "cmd.exe /c start cmd.exe /k \"cd /d " + projectPath + " && " +
                    env + "streamlit run " + script + " --server.port 8502 || pause\"";
            Runtime.getRuntime().exec(command);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not open AI Assistant. Please verify Streamlit installation and path to app.py.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeUserContext(String projectPath, User user) {
        try (FileWriter fw = new FileWriter(projectPath + File.separator + "user_context.json")) {
            fw.write("{\n" +
                    "  \"id\": \"" + escape(user.getUserId()) + "\",\n" +
                    "  \"name\": \"" + escape(user.getFullName()) + "\",\n" +
                    "  \"email\": \"" + escape(user.getEmail()) + "\",\n" +
                    "  \"role\": \"" + escape(formatRole(user.getRole())) + "\"\n" +
                    "}");
        } catch (IOException ignored) {}
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String formatRole(Role role) {
        String name = role.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}

// ======================================
// FACULTY DASHBOARD (with Logout)
// ======================================
class FacultyDashboard extends JFrame {
    private final User facultyUser;

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 16));
        return b;
    }

    public FacultyDashboard(User user) {
        this.facultyUser = user;
        setTitle("Faculty Dashboard - Campus360");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top bar: Welcome + Logout
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcome = new JLabel("Faculty: " + user.getFullName(), SwingConstants.CENTER);
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

        // Main buttons area
        JPanel main = new JPanel(new GridLayout(5, 1, 10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton accountBtn = createButton("My Account");
        accountBtn.addActionListener(e -> new AccountSettingsWindow(user));
        accountBtn.setBackground(new Color(0, 60, 110));
        accountBtn.setForeground(Color.WHITE);
        main.add(accountBtn);

        JButton bookFacilityBtn = createButton("Book Facility");
        bookFacilityBtn.addActionListener(e -> {
            dispose();
            new BookingFaculty(facultyUser).setVisible(true);
        });
        bookFacilityBtn.setBackground(new Color(0, 60, 110));
        bookFacilityBtn.setForeground(Color.WHITE);
        main.add(bookFacilityBtn);

        JButton requestMaintBtn = createButton("Request Maintenance");
        requestMaintBtn.addActionListener(e -> {
            dispose();
            MaintenanceService svc = new MaintenanceService();
            new MainFrame(facultyUser, svc).setVisible(true);
        });
        requestMaintBtn.setBackground(new Color(0, 60, 110));
        requestMaintBtn.setForeground(Color.WHITE);
        main.add(requestMaintBtn);

        JButton aiAssistantBtn = createButton("AI Assistant");
        aiAssistantBtn.addActionListener(e -> openAssistant(facultyUser));
        aiAssistantBtn.setBackground(new Color(0, 60, 110));
        aiAssistantBtn.setForeground(Color.WHITE);
        main.add(aiAssistantBtn);

        JButton scheduleEventBtn = createButton("Schedule Event");
        scheduleEventBtn.addActionListener(e -> new EventScheduler(user));
        scheduleEventBtn.setBackground(new Color(0, 60, 110));
        scheduleEventBtn.setForeground(Color.WHITE);
        main.add(scheduleEventBtn);

        JButton viewCalendarBtn = createButton("View Calendar");
        viewCalendarBtn.addActionListener(e -> new CalendarView(user));
        viewCalendarBtn.setBackground(new Color(0, 60, 110));
        viewCalendarBtn.setForeground(Color.WHITE);
        main.add(viewCalendarBtn);

        add(main, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openAssistant(User user) {
        try {
            String projectPath = "C:\\Users\\hp\\OneDrive\\Desktop\\campus360 - Copy (3) - Copy";
            String path = projectPath + "\\\\app.py";
            writeUserContext(projectPath, user);
            String env = "set \"AI_USER_ID=" + user.getUserId() + "\" && " +
                    "set \"AI_USER_NAME=" + user.getFullName() + "\" && " +
                    "set \"AI_USER_EMAIL=" + user.getEmail() + "\" && " +
                    "set \"AI_USER_ROLE=" + formatRole(user.getRole()) + "\" && ";
            String command = "cmd /c start cmd.exe /k \"cd /d " + projectPath + " && " + env +
                    "streamlit run \"" + path + "\" --server.port 8502 || pause\"";
            Runtime.getRuntime().exec(command);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to open AI Assistant. Please check the file path or Streamlit installation.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatRole(Role role) {
        String name = role.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}

// ======================================
// TECHNICIAN DASHBOARD (with Logout)
// ======================================
class TechnicianDashboard extends JFrame {
    private final User technicianUser;

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 16));
        return b;
    }

    public TechnicianDashboard(User user) {
        this.technicianUser = user;
        setTitle("Technician Dashboard - Campus360");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top bar: Welcome + Logout
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcome = new JLabel("Technician: " + user.getFullName(), SwingConstants.CENTER);
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
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Main buttons area
        JPanel main = new JPanel(new GridLayout(1, 1, 10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton viewAssignedBtn = createButton("View Assigned Maintenance");
        viewAssignedBtn.addActionListener(e -> {
            dispose();
            MaintenanceService svc = new MaintenanceService();
            new MainFrame(technicianUser, svc).setVisible(true);
        });
        viewAssignedBtn.setBackground(new Color(0, 60, 110));
        viewAssignedBtn.setForeground(Color.WHITE);
        main.add(viewAssignedBtn);

        add(main, BorderLayout.CENTER);

        setVisible(true);
    }
}

// ======================================
// ADMIN DASHBOARD (with Logout)
// ======================================
class AdminDashboard extends JFrame {
    private final User adminUser;

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 16));
        return b;
    }

    public AdminDashboard(User user) {
        this.adminUser = user;
        setTitle("Admin Dashboard - Campus360");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top bar: Welcome + Logout
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

        // Main buttons area
        JPanel main = new JPanel(new GridLayout(7, 1, 10, 10));
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

        JButton roleAccessBtn = createButton("Assign Role-Based Access");
        roleAccessBtn.addActionListener(e -> new RoleAccessWindow());
        roleAccessBtn.setBackground(new Color(0, 60, 110));
        roleAccessBtn.setForeground(Color.WHITE);
        main.add(roleAccessBtn);

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

// ======================================
// LOGIN PAGE (MAIN CLASS)
// ======================================
public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passField;

    public LoginPage() {
        setTitle("Campus360 Login");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add logo above the title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon icon = new ImageIcon("logo.png"); // Ensure logo.png is in the same folder
            Image scaled = icon.getImage().getScaledInstance(350, 150, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
            headerPanel.add(logoLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            System.out.println("Logo image not found.");
        }

        add(headerPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        emailField = new JTextField();
        passField = new JPasswordField();

        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passField);
        add(form, BorderLayout.CENTER);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setBackground(new Color(0, 60, 110));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(e -> doLogin());

        JPanel bottom = new JPanel();
        bottom.add(loginBtn);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.");
            return;
        }

        User user = UserManager.authenticate(email, pass);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Welcome " + user.getFullName() + "!");

        switch (user.getRole()) {
            case STUDENT -> new StudentDashboard(user);
            case FACULTY -> new FacultyDashboard(user);
            case TECHNICIAN -> new TechnicianDashboard(user);
            case ADMIN -> new AdminDashboard(user);
        }

        dispose();
    }

    public static void main(String[] args) {
        UserManager.loadUsersFromFile();
        new LoginPage();
    }
}
