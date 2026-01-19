
import java.util.Objects;

public class User {
    private String userId;
    private String fullName;
    private String email;
    private String password;
    private Role role;

    public User(String userId, String fullName, String email, String password, Role role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    public void setFullName(String v) { fullName = v; }
    public void setEmail(String v) { email = v; }
    public void setPassword(String v) { password = v; }
    public void setRole(Role r) { role = r; }

    @Override public String toString(){ return userId + " - " + fullName + " (" + role + ")"; }
    @Override public boolean equals(Object o){ return (o instanceof User) && Objects.equals(userId, ((User)o).userId); }
    @Override public int hashCode(){ return Objects.hash(userId); }
}
