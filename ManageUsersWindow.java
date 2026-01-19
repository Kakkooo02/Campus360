import java.awt.*;
import javax.swing.*;

class ManageUsersWindow extends JFrame {

    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField passField;
    private JComboBox<Role> roleBox;

    public ManageUsersWindow(User adminUser) {
        setTitle("Manage Users");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ----- Left Side: User List -----
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        refreshUserList();

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(320, 0));
        add(scrollPane, BorderLayout.WEST);

        // ----- Right Side: Form -----
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        passField = new JTextField();
        roleBox = new JComboBox<>(Role.values());

        rightPanel.add(createLabeledRow("User ID:", idField));
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(createLabeledRow("Full Name:", nameField));
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(createLabeledRow("Email:", emailField));
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(createLabeledRow("Password:", passField));
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(createLabeledRow("Role:", roleBox));
        rightPanel.add(Box.createVerticalStrut(15));

        // Buttons
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton closeBtn = new JButton("Close");

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row1.add(addBtn);
        row1.add(updateBtn);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row2.add(deleteBtn);
        row2.add(closeBtn);

        rightPanel.add(row1);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(row2);

        add(rightPanel, BorderLayout.CENTER);

        // ----- Event Handlers -----
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = userList.getSelectedIndex();
                if (idx >= 0) {
                    User u = UserManager.getUsers().get(idx);
                    idField.setText(u.getUserId());
                    nameField.setText(u.getFullName());
                    emailField.setText(u.getEmail());
                    passField.setText(u.getPassword());
                    roleBox.setSelectedItem(u.getRole());
                }
            }
        });

        addBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passField.getText().trim();
            Role role = (Role) roleBox.getSelectedItem();

            if (id.isEmpty() || name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            for (User u : UserManager.getUsers()) {
                if (u.getUserId().equalsIgnoreCase(id)) {
                    JOptionPane.showMessageDialog(this, "User ID already exists.");
                    return;
                }
            }

            User newUser = new User(id, name, email, pass, role);
            UserManager.addUser(newUser);
            refreshUserList();
            JOptionPane.showMessageDialog(this, "User added successfully.");
        });

        updateBtn.addActionListener(e -> {
            int idx = userList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Select a user from the list first.");
                return;
            }

            User u = UserManager.getUsers().get(idx);
            u.setFullName(nameField.getText().trim());
            u.setEmail(emailField.getText().trim());
            u.setPassword(passField.getText().trim());
            u.setRole((Role) roleBox.getSelectedItem());

            UserManager.saveUsersToFile();
            refreshUserList();
            JOptionPane.showMessageDialog(this, "User updated.");
        });

        deleteBtn.addActionListener(e -> {
            int idx = userList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Select a user from the list first.");
                return;
            }

            User u = UserManager.getUsers().get(idx);
            if (u.getRole() == Role.ADMIN && u.getEmail().equalsIgnoreCase("admin@ku.ac.ae")) {
                JOptionPane.showMessageDialog(this, "Cannot delete main system admin.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected user?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserManager.removeUser(idx);
                refreshUserList();
                JOptionPane.showMessageDialog(this, "User deleted.");
            }
        });

        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private JPanel createLabeledRow(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 24));
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void refreshUserList() {
        listModel.clear();
        for (User u : UserManager.getUsers()) {
            listModel.addElement(u.getUserId() + " - " + u.getFullName() + " (" + u.getRole() + ")");
        }
    }
}
