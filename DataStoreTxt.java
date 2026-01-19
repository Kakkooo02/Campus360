import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// ======================================
// DATA STORE (TEXT FILE BASED)
// ======================================
public class DataStoreTxt {

    private static final String USERS = "users.txt";
    private static final String REQS = "requests.txt";
    private static final String HIST = "request_history.txt";

    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, MaintenanceRequest> requests = new LinkedHashMap<>();

    // ======================================
    // CONSTRUCTOR
    // ======================================
    public DataStoreTxt() {
        ensureFiles();
        loadUsers();
        loadRequests();
    }

    // ======================================
    // ENSURE FILES EXIST
    // ======================================
    private void ensureFiles() {
        try {
            if (!Files.exists(Path.of(USERS))) {
                Files.writeString(Path.of(USERS),
                        "A001;System Admin;admin@ku.ac.ae;admin123;ADMIN\n" +
                        "S001;Student Ali;ali@student.ku.ac.ae;1234;STUDENT\n" +
                        "F001;Dr. Ahmed;ahmed@ku.ac.ae;1234;FACULTY\n" +
                        "T001;Technician Omar;tech@ku.ac.ae;tech123;TECHNICIAN\n" +
                        "T002;Technician Fatima;fatima@ku.ac.ae;tech456;TECHNICIAN\n");
            }
            if (!Files.exists(Path.of(REQS))) Files.createFile(Path.of(REQS));
            if (!Files.exists(Path.of(HIST))) Files.createFile(Path.of(HIST));
        } catch (IOException e) {
            System.out.println("Error creating data files: " + e.getMessage());
        }
    }

    // ======================================
    // LOAD USERS
    // ======================================
    private void loadUsers() {
        try (BufferedReader br = Files.newBufferedReader(Path.of(USERS))) {
            br.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .forEach(line -> {
                    String[] p = line.split(";", -1);
                    if (p.length >= 5) {
                        try {
                            String id = p[0].trim();
                            String name = p[1].trim();
                            String email = p[2].trim();
                            String pass = p[3].trim();
                            Role role = Enum.valueOf(Role.class, p[4].trim().toUpperCase());
                            User u = new User(id, name, email, pass, role);
                            users.put(u.getUserId(), u);
                            System.out.println("Loaded user: " + name + " (" + role + ")");
                        } catch (Exception ex) {
                            System.out.println("Error loading user line: " + line);
                        }
                    }
                });
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    // ======================================
    // LOAD REQUESTS
    // ======================================
    private void loadRequests() {
        try (BufferedReader br = Files.newBufferedReader(Path.of(REQS))) {
            br.lines()
                .filter(line -> !line.isEmpty())
                .forEach(line -> {
                    try {
                        MaintenanceRequest r = MaintenanceRequest.fromLine(line);
                        requests.put(r.getId(), r);
                    } catch (Exception ignore) {}
                });
        } catch (IOException e) {
            System.out.println("Error loading requests: " + e.getMessage());
        }
    }

    // ======================================
    // GETTERS
    // ======================================
    public Collection<MaintenanceRequest> getAllRequests() {
        return requests.values();
    }

    public MaintenanceRequest getRequest(String id) {
        return requests.get(id);
    }

    // ======================================
    // SAVE / UPDATE REQUESTS
    // ======================================
    public void upsertRequest(MaintenanceRequest r) {
        requests.put(r.getId(), r);
        saveAll();
    }

    private void saveAll() {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(REQS))) {
            for (MaintenanceRequest r : requests.values()) {
                bw.write(r.toLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving requests: " + e.getMessage());
        }
    }

    // ======================================
    // TECHNICIAN LIST
    // ======================================
    public List<User> listTechnicians() {
        return users.values().stream()
                .filter(u -> u.getRole() == Role.TECHNICIAN)
                .collect(Collectors.toList());
    }

    // ======================================
    // APPEND HISTORY
    // ======================================
    public void appendHistory(String reqId, Status st, String by, String note) {
        String line = String.join("|",
                reqId,
                st.name(),
                by,
                note.replace("|", "/"),
                LocalDateTime.now().toString());
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(HIST), StandardOpenOption.APPEND)) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing history: " + e.getMessage());
        }
    }
}
