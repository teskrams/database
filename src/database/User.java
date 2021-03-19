package database;

public class User {

    private String username;
    private String password;
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Email:"+username+" password:"+password;
    }

    public String getName() {
        return username;
    }
}
