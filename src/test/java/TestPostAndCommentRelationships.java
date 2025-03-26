import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.CommentClient;
import services.PostAndCommentBinder;
import services.PostClient;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestPostAndCommentRelationships {
    private PostAndCommentBinder binder;

    @BeforeEach
    public void init() {
        PostClient post = new PostClient();
        CommentClient comment = new CommentClient();
        binder = new PostAndCommentBinder(post, comment);
    }

    @Test
    public void testValues() {
        try {
            var postResult = binder.fetchData();
            System.out.println(postResult); // you can read the returned data from the API
            assert !postResult.isEmpty();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
