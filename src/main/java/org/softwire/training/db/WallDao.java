package org.softwire.training.db;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.softwire.training.models.SocialEvent;
import org.softwire.training.models.User;

import java.util.List;

/**
 * The Wall DAO (Data Access Object) provides an interface for interacting with Social Events in the database.
 *
 * In particular, users of this class don't need to know any details about the database itself.
 */
public class WallDao {

    private final Jdbi jdbi;

    public WallDao(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<SocialEvent> readWall(User user) {
        try (Handle handle = jdbi.open()) {
            return handle.createQuery("SELECT social_events.id, social_events.authorId, social_events.content, users.userId, users.fullname, event_types.eventTypeId, event_types.description FROM social_events " +
                    "JOIN users ON users.userId = social_events.authorId " +
                    "LEFT JOIN event_types ON event_types.eventTypeId = social_events.eventType " +
                    "WHERE social_events.userId = :user " +
                    "ORDER BY social_events.id")
                    .bind("user", user.getUserId())
                    .mapToBean(SocialEvent.class)
                    .list();
        }
    }

    public List<User> getAllUsers() {
        try (Handle handle = jdbi.open()) {
            return handle.createQuery("SELECT * FROM users")
                    .mapToBean(User.class)
                    .list();
        }
    }

    public void writeOnWall(User user, SocialEvent socialEvent) {
        try (Handle handle = jdbi.open()) {
            handle.createCall("INSERT INTO social_events (userId, authorId, content) VALUES (:user, :author, :content)")
                    .bind("author", socialEvent.getAuthor().getUserId())
                    .bind("user", user.getUserId())
                    .bind("content", socialEvent.getContent())
                    .invoke();
        }
    }

    public void addSocialEventOfType(User user, SocialEvent socialEvent)
    {
        try (Handle handle = jdbi.open())
        {
            handle.createCall("INSERT INTO social_events (userId, authorId, eventType) VALUES (:user, :author, :eventType)")
            .bind("author", socialEvent.getAuthor().getUserId())
            .bind("user", user.getUserId())
            .bind("eventType", socialEvent.getEventType().getEventTypeId())
            .invoke();
        }
    }

    public void deletePost(int id)
    {
        try(Handle handle = jdbi.open())
        {
            handle.createCall("DELETE FROM social_events WHERE id = :id")
                    .bind("id", id)
                    .invoke();
        }
    }

    public SocialEvent getSocialEventById(int id)
    {
        try(Handle handle = jdbi.open())
        {
            return handle.select("SELECT * FROM social_events " +
                    "LEFT JOIN event_types ON event_types.eventTypeId = social_events.eventType " +
                    "WHERE id = :id")
                    .bind("id", id)
                    .mapToBean(SocialEvent.class)
                    .findOnly();
        }
    }
}
