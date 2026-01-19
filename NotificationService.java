import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import javax.swing.*;

public class NotificationService {
    private static final String FILE = "notifications.txt";

    public NotificationService() {
        try {
            if (!Files.exists(Path.of(FILE))) {
                Files.createFile(Path.of(FILE));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyToFile(String to, String msg) {
        String line = LocalDateTime.now() + "|" + to + "|" + msg.replace("|", "/");
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(FILE), StandardOpenOption.APPEND)) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void popup(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }
}
