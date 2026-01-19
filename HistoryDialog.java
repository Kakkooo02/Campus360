import java.awt.*;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HistoryDialog extends JDialog {
    public HistoryDialog(Frame owner, String reqId) {
        super(owner, "History - " + reqId.substring(0, Math.min(8, reqId.length())), true);
        setSize(700, 400);
        setLocationRelativeTo(owner);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Status", "By", "Note", "Time"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try (BufferedReader br = Files.newBufferedReader(Path.of("request_history.txt"))) {
            br.lines().forEach(line -> {
                String[] p = line.split("\\|", -1);
                if (p.length >= 5 && p[0].equals(reqId)) {
                    model.addRow(new Object[]{p[1], p[2], p[3], p[4]});
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
