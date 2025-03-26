import models.Comment;
import models.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.CommentClient;
import services.PostAndCommentBinder;
import services.PostClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class TestPostAndCommentBinder {
    private static PostAndCommentBinder binder;

    @Mock
    private PostClient postClient;
    @Mock
    private CommentClient commentClient;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this); // Initialize mocks
        binder = new PostAndCommentBinder(postClient, commentClient);
    }

    @Test
    public void testPostAndComment() throws URISyntaxException, IOException, InterruptedException {
        Post post = new Post(1, 1, "Test", "test");
        Comment comment = new Comment(1, 1, "test", "test", "test");
        Map<Post, List<Comment>> mockedData = Map.of(post, List.of(comment));
        when(postClient.getPosts()).thenReturn(List.of(post));
        when(commentClient.getComments()).thenReturn(List.of(comment));
        Map< Post, List<Comment>> map = binder.fetchData();
        System.out.println("Test values: " + map);
        System.out.println(map.size());
        assert !map.isEmpty();
    }

    @AfterEach
    public void tearDown() {
        try {
            mocks.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
