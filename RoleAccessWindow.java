import java.awt.*;
import javax.swing.*;

class RoleAccessWindow extends JFrame {

    public RoleAccessWindow() {
        setTitle("Assign Role-Based Access");
        setSize(450, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel title = new JLabel("Assign Permissions to Roles", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // Form Section
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JComboBox<Role> roleBox = new JComboBox<>(Role.values());
        JCheckBox canBook = new JCheckBox("Can Book");
        JCheckBox canApprove = new JCheckBox("Can Approve");
        JCheckBox canManage = new JCheckBox("Can Manage Resources");
        JCheckBox isAdmin = new JCheckBox("Administrator");

        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleBox);
        formPanel.add(canBook);
        formPanel.add(canApprove);
        formPanel.add(canManage);
        formPanel.add(isAdmin);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JButton saveBtn = new JButton("Save");
        JButton closeBtn = new JButton("Close");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        saveBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Role access settings saved (demo only)."
        ));

        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}
