import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class CalendarView extends JFrame {
    private final JPanel calendarPanel;
    private final JLabel monthLabel;
    private LocalDate currentMonth;
    private final Map<LocalDate, List<String>> eventMap;

    public CalendarView(User user) {
        setTitle("Event Calendar - Campus360");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        eventMap = readEvents();
        currentMonth = LocalDate.now().withDayOfMonth(1);

        // Header with month navigation
        JPanel header = new JPanel(new BorderLayout(10, 10));
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        updateMonthLabel();

        JButton prevBtn = new JButton("<");
        JButton nextBtn = new JButton(">");
        styleButton(prevBtn);
        styleButton(nextBtn);

        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateMonthLabel();
            refreshCalendar();
        });

        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateMonthLabel();
            refreshCalendar();
        });

        JPanel navPanel = new JPanel();
        navPanel.add(prevBtn);
        navPanel.add(nextBtn);

        header.add(navPanel, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn);
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(calendarPanel, BorderLayout.CENTER);

        refreshCalendar();
        setVisible(true);
    }

    private void updateMonthLabel() {
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 60, 110));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void refreshCalendar() {
        calendarPanel.removeAll();

        // Weekday headers
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            lbl.setOpaque(true);
            lbl.setBackground(new Color(0, 60, 110));
            lbl.setForeground(Color.WHITE);
            calendarPanel.add(lbl);
        }

        LocalDate firstDay = currentMonth;
        int startDay = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();

        // Empty boxes before the first day
        for (int i = 0; i < startDay; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // Date boxes
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate date = firstDay.withDayOfMonth(d);
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel dateLbl = new JLabel(String.valueOf(d), SwingConstants.RIGHT);
            dateLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            dayPanel.add(dateLbl, BorderLayout.NORTH);

            JTextArea eventArea = new JTextArea();
            eventArea.setEditable(false);
            eventArea.setFont(new Font("SansSerif", Font.PLAIN, 11));

            if (eventMap.containsKey(date)) {
                StringBuilder sb = new StringBuilder();
                for (String ev : eventMap.get(date)) {
                    sb.append("• ").append(ev).append("\n");
                }
                eventArea.setText(sb.toString());
                eventArea.setBackground(new Color(230, 247, 255));
            } else {
                eventArea.setBackground(Color.WHITE);
            }

            dayPanel.add(eventArea, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private Map<LocalDate, List<String>> readEvents() {
        Map<LocalDate, List<String>> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("events.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(", ");
                if (p.length >= 7 && p[6].equalsIgnoreCase("Approved")) {
                    try {
                        LocalDate date = LocalDate.parse(p[3].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        String title = p[2].trim() + " (" + p[4].trim() + ")";
                        map.computeIfAbsent(date, k -> new ArrayList<>()).add(title);
                    } catch (Exception ignored) {}
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading events file.");
        }
        return map;
    }
}
