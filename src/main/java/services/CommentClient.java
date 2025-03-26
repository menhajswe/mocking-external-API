package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Comment;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CommentClient {

    public List<Comment> getComments() throws URISyntaxException, IOException, InterruptedException {
        HttpClient commentClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://jsonplaceholder.typicode.com/comments"))
                .GET()
                .build();
        HttpResponse<String> response = commentClient.send(request, HttpResponse.BodyHandlers.ofString());
        commentClient.close();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(),  new TypeReference<List<Comment>>(){});
    }
}
