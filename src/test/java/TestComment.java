import models.Comment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.CommentClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class TestComment {
    static CommentClient client;

    @BeforeAll
    public static void init() {
        client = new CommentClient();
    }

    @Test
    public void testComments() {
        try {
            List<Comment> comments = client.getComments();
            System.out.println(comments);
            assert !comments.isEmpty();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
