import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MaintenanceService {
    private final DataStoreTxt store;
    private final NotificationService noti;

    public MaintenanceService(DataStoreTxt store, NotificationService noti) {
        this.store = store;
        this.noti = noti;
    }

    // Constructor overload for quick use without NotificationService
    public MaintenanceService() {
        this(new DataStoreTxt(), new NotificationService());
    }

    public MaintenanceRequest create(String title, String desc, String loc, User creator) {
        MaintenanceRequest r = new MaintenanceRequest(title, desc, loc, creator.getFullName());
        store.upsertRequest(r);
        store.appendHistory(r.getId(), r.getStatus(), creator.getFullName(), "Created");
        noti.notifyToFile(creator.getFullName(), "Request created: " + r.getTitle());
        noti.popup("Created request!\nID: " + r.getId().substring(0, 8));
        return r;
    }

    public List<MaintenanceRequest> listAll() {
        return store.getAllRequests().stream().collect(Collectors.toList());
    }

    public List<MaintenanceRequest> listByCreator(String name) {
        return store.getAllRequests().stream()
                .filter(r -> Objects.equals(r.getCreatedBy(), name))
                .collect(Collectors.toList());
    }

    public List<MaintenanceRequest> listUnassigned() {
        return store.getAllRequests().stream()
                .filter(r -> r.getAssignedTo() == null || r.getAssignedTo().isBlank())
                .collect(Collectors.toList());
    }

    public List<User> listTechnicians() {
        return store.listTechnicians();
    }

    public void assign(String id, String tech, User by) {
        MaintenanceRequest r = store.getRequest(id);
        if (r == null) return;
        r.setAssignedTo(tech);
        store.upsertRequest(r);
        store.appendHistory(id, r.getStatus(), by.getFullName(), "Assigned to " + tech);
        noti.notifyToFile(r.getCreatedBy(), "Your request assigned to " + tech);
        noti.notifyToFile(tech, "New assignment: " + r.getTitle());
    }

    public void updateStatus(String id, Status st, User by, String note) {
        MaintenanceRequest r = store.getRequest(id);
        if (r == null) return;
        r.setStatus(st);
        store.upsertRequest(r);
        store.appendHistory(id, st, by.getFullName(), note);
        noti.notifyToFile(r.getCreatedBy(), "Status updated to " + st + " for " + r.getTitle());
    }

    public void reopen(String id, User by, String note) {
        MaintenanceRequest r = store.getRequest(id);
        if (r == null) return;
        r.incReopen();
        r.setStatus(Status.REOPENED);
        store.upsertRequest(r);
        store.appendHistory(id, Status.REOPENED, by.getFullName(), note);
        if (r.getAssignedTo() != null && !r.getAssignedTo().isBlank()) {
            noti.notifyToFile(r.getAssignedTo(), "Request reopened: " + r.getTitle());
        }
        noti.notifyToFile(r.getCreatedBy(), "You reopened " + r.getTitle());
    }
}
