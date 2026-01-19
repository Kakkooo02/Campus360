public class UsersExt {

    public static class Student extends User {
        public Student(String username, String password) {
            super(username, username, "", password, Role.STUDENT);
        }
    }

    public static class Faculty extends User {
        public Faculty(String username, String password) {
            super(username, username, "", password, Role.FACULTY);
        }
    }

    public static class Admin extends User {
        public Admin(String username, String password) {
            super(username, username, "", password, Role.ADMIN);
        }
    }

    public static class Technician extends User {
        public Technician(String username, String password) {
            super(username, username, "", password, Role.TECHNICIAN);
        }
    }
}
