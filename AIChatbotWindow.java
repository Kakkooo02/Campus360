import java.awt.*;
import javax.swing.*;

class AIChatbotWindow extends JFrame {

    public AIChatbotWindow() {
        setTitle("AI Chatbot");
        setSize(480, 330);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTextArea textArea = new JTextArea(
                "AI Chatbot placeholder.\n\n" +
                "In the future this window can show:\n" +
                "- Chat with AI for booking help\n" +
                "- FAQs and smart suggestions\n" +
                "- Maintenance and usage tips.\n\n" +
                "For now it is only a demo view."
        );
        textArea.setEditable(false);

        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);

        setVisible(true);
    }
}
