import models.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.PostClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class TestPostClient {

    static Post post;
    static PostClient client;

    @BeforeAll
    public static void init() {
        client = new PostClient();
    }

    @Test
    public void testPosts() {
        try {
            List<Post> posts = client.getPosts();
            System.out.println(posts);
            assert !posts.isEmpty();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
