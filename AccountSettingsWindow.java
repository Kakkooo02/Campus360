import java.awt.*;
import javax.swing.*;

class AccountSettingsWindow extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passField;

    public AccountSettingsWindow(User user) {
        setTitle("My Account");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ----- Form Panel -----
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        nameField = new JTextField(user.getFullName());
        nameField.setEditable(false);
        emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);
        passField = new JPasswordField(user.getPassword());

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passField);

        add(formPanel, BorderLayout.CENTER);

        // ----- Buttons -----
        JButton saveBtn = new JButton("Save");
        JButton closeBtn = new JButton("Close");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // ----- Actions -----
        saveBtn.addActionListener(e -> {
            user.setPassword(new String(passField.getPassword()).trim());
            UserManager.saveUsersToFile();
            JOptionPane.showMessageDialog(this, "Account updated successfully.");
        });

        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}
