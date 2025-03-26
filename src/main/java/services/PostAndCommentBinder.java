package services;

import models.Comment;
import models.Post;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAndCommentBinder {
    private final CommentClient commentClient;
    private final PostClient postClient;

    public PostAndCommentBinder(PostClient postClient, CommentClient commentClient) {
        this.postClient = postClient;
        this.commentClient = commentClient;
    }

    public Map<Post, List<Comment>> fetchData() throws URISyntaxException, IOException, InterruptedException {
        List<Post> posts = postClient.getPosts();
        List<Comment> comments = commentClient.getComments();

        Map<Post, List<Comment>> postCommentMap = new HashMap<>();
        for (Post post : posts) {
            List<Comment> filteredComments = comments.stream()
                    .filter(c -> c.postId() == post.id())
                    .toList();
            postCommentMap.put(post, filteredComments);
        }
        return postCommentMap;
    }
}
