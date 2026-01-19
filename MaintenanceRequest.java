import java.time.LocalDateTime;
import java.util.UUID;

public class MaintenanceRequest {
    private final String id;
    private String title;
    private String description;
    private String location;
    private final String createdBy;
    private String assignedTo;
    private Status status;
    private int reopenCount;
    private LocalDateTime updatedAt;

    // Constructor for new requests
    public MaintenanceRequest(String title, String desc, String location, String createdBy) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = desc;
        this.location = location;
        this.createdBy = createdBy;
        this.assignedTo = "";
        this.status = Status.PENDING;
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor for loading from file
    public MaintenanceRequest(String id, String title, String desc, String location,
                              String createdBy, String assignedTo, Status status, int reopenCount,
                              LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = desc;
        this.location = location;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.status = status;
        this.reopenCount = reopenCount;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getCreatedBy() { return createdBy; }
    public String getAssignedTo() { return assignedTo; }
    public Status getStatus() { return status; }
    public int getReopenCount() { return reopenCount; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setAssignedTo(String tech) {
        this.assignedTo = tech == null ? "" : tech;
    }

    public void setStatus(Status s) {
        this.status = s;
        this.updatedAt = LocalDateTime.now();
    }

    public void incReopen() {
        this.reopenCount++;
        this.updatedAt = LocalDateTime.now();
    }

    // Convert to text line
    public String toLine() {
        return String.join("|",
                id,
                esc(title),
                esc(description),
                esc(location),
                createdBy,
                assignedTo == null ? "" : assignedTo,
                status.name(),
                Integer.toString(reopenCount),
                updatedAt.toString()
        );
    }

    // Load from text line
    public static MaintenanceRequest fromLine(String line) {
        String[] p = line.split("\\|", -1);
        return new MaintenanceRequest(
                p[0],
                unesc(p[1]),
                unesc(p[2]),
                unesc(p[3]),
                p[4],
                p[5],
                Status.valueOf(p[6]),
                Integer.parseInt(p[7]),
                LocalDateTime.parse(p[8])
        );
    }

    private static String esc(String s) {
        return s.replace("\n", " ").replace("|", "/");
    }

    private static String unesc(String s) {
        return s;
    }
}
