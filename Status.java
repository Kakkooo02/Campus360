// ======================================
// STATUS ENUM FOR MAINTENANCE REQUESTS
// ======================================
public enum Status {
    PENDING,        // Newly created
    APPROVED,       // Approved by admin
    REJECTED,       // Rejected by admin
    IN_PROGRESS,    // Work started
    ON_HOLD,        // Work temporarily paused
    COMPLETED,      // Work done
    CANCELLED,      // Cancelled by user or admin
    REOPENED        // Reopened after completion
}
