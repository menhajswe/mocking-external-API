# **How to Mock an External API in a Maven Project?**

## **Description**
In this tutorial, we will implement and mock external API services using **JSONPlaceholder**. This is useful when working with applications that interact with third-party APIs for data retrieval.

For example, a flight search on Google Flights fetches data from multiple airline APIs to present options like:
- **American Airlines**: Departure **DCA** at **07:00 AM**, Arrival **SFO** at **6:00 PM**
- **Alaska Airlines**: Departure **IAD** at **8:00 AM**, Arrival **SFO** at **10:00 PM**

If you're performing **end-to-end testing**, making real API calls for every test is inefficient and unreliable. Instead, **mocking external APIs** ensures test stability, speed, and independence from third-party services.

This tutorial covers:  
✅ Implementing API clients for **Posts** and **Comments** using `HttpClient`.  
✅ Binding **Posts** with their corresponding **Comments**.  
✅ Using **Mockito** to mock API calls in unit tests.

---

## **Maven Dependencies**
Add these dependencies to your `pom.xml`:
```xml
<dependencies>
   <!-- JUnit 5 for testing -->
   <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-api</artifactId>
      <version>5.11.4</version>
      <scope>test</scope>
   </dependency>

   <!-- Jackson for JSON parsing -->
   <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.18.2</version>
   </dependency>

   <!-- Mockito for mocking -->
   <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <version>4.11.0</version>
      <scope>test</scope>
   </dependency>
</dependencies>
```

---

## **Implementing the API Clients**

### Modeling Java Objects
First, we need to inspect the API response, then create an java object that matches the response data. We are doing this,
so we can process the data in java data-structure. Let's see the request and responses from jsoanplaceholder API.
<br>
Given: `curl -X GET "https://jsonplaceholder.typicode.com/posts/1"`
</br>
Returned response:
  ```
  {
      "userId": 1,
      "id": 1,
      "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
      "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
  }
  ```
And visiting: `curl -X GET "https://jsonplaceholder.typicode.com/comments/1"` <br>
returns the following response:
```
   {
      "postId": 1,
      "id": 1,
      "name": "id labore ex et quam laborum",
      "email": "Eliseo@gardner.biz",
      "body": "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium"
   }
```
A good option for processing API data is java records, instead of classes. Let's create a java record that matches the API
response.

### **1. Post Model**
Similarly, the **Post** model:
```java
public record Post(int userId, int id, String title, String body) {
}
```

### **2. Comment Model**
Using a **Java record** for an immutable data structure:
```java
public record Comment(int postId, int id, String name, String email, String body) {
}
```
One way to handle request and response using java is HttpClient library. This will handle our HttpRequest and HttpResponse.

### **3. Post Client (Fetching Posts from API)**
```java
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
              .uri(new URI("https://jsonplaceholder.typicode.com/posts"))
              .GET()
              .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      client.close();
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(response.body(), new TypeReference<List<Post>>() {});
   }
}
```

### **4. Comment Client (Fetching Comments from API)**
```java
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
      return mapper.readValue(response.body(), new TypeReference<>() {});
   }
}
```

---
Often, you will have a class that will handle and process the client responses. For instance, if you fetch data from Alaska Airlines
in your application, then you would want to process that data according your own application or need. An API response 
could return unnecessary data, therefore, you many need to filter them. For instance, in this example, jsonpalceholder's post API
and comments' API returns two sets of json data. To create a relationship between posts and comments, I created teh following class,
which maps posts-id to comment's post-id. In terms fo SQL, it is post's primary key is linked to comment's foreign key.
```
post-table
   post-id primary key,
   post
```
<br>

``` 
comment-table
   comment-id primary key,
   comment,
   postId foreign key
   
```
Now, we will link the post-id to comment-postId

## **Binding Posts and Comments**
We now create a **PostAndCommentBinder** that maps **Posts** to their corresponding **Comments**:
```java
import models.Comment;
import models.Post;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAndCommentBinder {
   private final PostClient postClient;
   private final CommentClient commentClient;

   public PostAndCommentBinder(PostClient postClient, CommentClient commentClient) {
      this.postClient = postClient;
      this.commentClient = commentClient;
   }

   public Map<Post, List<Comment>> fetchData() throws URISyntaxException, IOException, InterruptedException {
      // Notice: The following two lines will fetch the data. These will make actual API requests.
      // We want to mock these requests during our testing.
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
```

---

## **Unit Testing with Mockito**
To **mock** the API clients and verify that the `PostAndCommentBinder` works correctly, we use **Mockito**.

```java
import models.Comment;
import models.Post;
import org.junit.jupiter.api.AfterEach;
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
   private PostAndCommentBinder binder;

   @Mock
   private PostClient postClient; // Mocking this service to avoid actual API requests. 
   @Mock
   private CommentClient commentClient; // The same thing, mocking it to prevent httpclient request/response.

   private AutoCloseable mocks; // An object that may hold resources (such as file or socket handles) until it is closed

   @BeforeEach
   void setUp() {
      mocks = MockitoAnnotations.openMocks(this);
      binder = new PostAndCommentBinder(postClient, commentClient); // Passing the mocked objects
   }

   @Test
   public void testPostAndComment() throws URISyntaxException, IOException, InterruptedException {
      Post post = new Post(1, 1, "Test Post", "This is a test post."); // Expected mocked-post
      Comment comment = new Comment(1, 1, "John Doe", "john@example.com", "This is a test comment."); // Expected mocked-comment
      Map<Post, List<Comment>> mockedData = Map.of(post, List.of(comment));
      // when binder class calls on postClient.getPost() to retrieve data, mockito will return the created mocked objects which are the expected response. 
      when(postClient.getPosts()).thenReturn(List.of(post));
      when(commentClient.getComments()).thenReturn(List.of(comment));
      Map<Post, List<Comment>> result = binder.fetchData(); // returns mocked data
      System.out.println("Test values: " + result);
      assert !result.isEmpty();
   }

   @AfterEach
   public void tearDown() throws Exception {
      mocks.close();
   }
}
```

---

## **Key Takeaways**
- **Implemented API clients** for fetching **Posts** and **Comments** from an external service.
- **Created a binder** to associate **Posts** with their **Comments**.
- **Used Mockito** to mock external API calls for faster and more reliable tests.

### **Why Mock External APIs?**
✔️ Avoid unnecessary network calls.  
✔️ Ensure consistent test results.  
✔️ Speed up test execution.

---

## **Additional Resources**
- **[Java HttpClient Guide](https://www.baeldung.com/java-9-http-client)**
- **[Jackson ObjectMapper Guide](https://www.baeldung.com/jackson-object-mapper-tutorial)**
- **[Mockito Tutorial](https://www.baeldung.com/mockito-series)**

---
### **How to Run This Project**
1. Clone the repository:
   ```sh
   git clone git@github.com:menhajswe/mocking-external-apis-.git
   ```  
2. Navigate to the project directory:
   ```sh
   cd mocking-external-apis-
   ```  
3. Run tests using Maven:
   ```sh
   mvn clean test
   ``` # mocking-external-API
# Feedback
Please feel free to leave comments or question.
