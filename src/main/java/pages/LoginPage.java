package pages;

public class LoginPage {
    private String username;
    private String email;
    private int id;

    private static int autoId = 0;

    public LoginPage(String uname, String mail) {
        username = uname;
        email = mail;
        id = (++autoId);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }
}
