package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Post;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PostClient {
    public List<Post> getPosts() throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                // to reduce the number of returned posts, add "/1" at then of url
                // ex: https://jsonplaceholder.typicode.com/posts/1
                .uri(new URI("https://jsonplaceholder.typicode.com/posts"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        ObjectMapper objectMapper = new ObjectMapper();
        // You could also use an extra variable such as:
        // List<Post> posts = objectMapper.readValue(response.body(), new TypeReference<>() {});
        // return posts;
        return objectMapper.readValue(response.body(), new TypeReference<List<Post>>() {});
    }
}
