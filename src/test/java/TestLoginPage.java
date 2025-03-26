import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pages.LoginPage;

public class TestLoginPage {
    private static LoginPage loginPage;

    @BeforeAll
    public static void init() {
        loginPage = new LoginPage("user123", "user123@gold.com");
    }

    @Test
    public void testUserNameAndEmail() {
        assert loginPage.getEmail().equals("user123@gold.com");
        assert loginPage.getUsername().equals("user123");
    }

    @Test
    public void testAutId() {
        assert loginPage.getId() > 0;
    }

    @Test
    public void testTotalCustomer() {
        assert loginPage.getId() == 1;
    }

    @Test
    public void testFail() {
        assert loginPage != null;
    }
}
