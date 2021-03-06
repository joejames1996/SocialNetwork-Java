package org.softwire.training.models;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jdbi.v3.core.mapper.Nested;

import java.awt.*;

/**
 * A Social Event represents a single item on a wall - i.e. a post
 */
public class SocialEvent {

    /**
     * The author of the Social Event
     */
    private User author;

    /**
     * The text content of the Event
     */
    private String content;
    private boolean canBeDeleted;
    private int id;
    private int authorId;
    private EventType eventType;

    public SocialEvent() {}

    public SocialEvent(User author, String content) {
        this.author = author;
        this.content = content;
    }

    public SocialEvent(User author, EventType eventType)
    {
        this.author = author;
        this.eventType = eventType;
    }

    @Nested
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Below methods were automatically generated using IntelliJ and Guava.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialEvent that = (SocialEvent) o;
        return Objects.equal(author, that.author) &&
                Objects.equal(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(author, content);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("author", author)
                .add("content", content)
                .add("EventType", eventType)
                .toString();
    }

    public boolean isCanBeDeleted()
    {
        return canBeDeleted;
    }

    public void setCanBeDeleted(boolean canBeDeleted)
    {
        this.canBeDeleted = canBeDeleted;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getAuthorId()
    {
        return authorId;
    }

    public void setAuthorId(int authorId)
    {
        this.authorId = authorId;
    }

    @Nested
    public EventType getEventType()
    {
        return eventType;
    }

    public void setEventType(EventType eventType)
    {
        this.eventType = eventType;
    }
}
