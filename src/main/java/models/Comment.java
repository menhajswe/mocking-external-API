package models;

public record Comment(int postId, int id, String name, String email, String body) {
}
