package org.softwire.training.models;

import com.google.common.base.MoreObjects;

public class EventType
{
    private int eventTypeId;
    private String description;

    public EventType()
    {

    }

    public EventType(int eventTypeId, String description)
    {
        this.eventTypeId = eventTypeId;
        this.description = description;
    }

    public EventType(int eventTypeId)
    {
        this.eventTypeId = eventTypeId;
    }

    public int getEventTypeId()
    {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId)
    {
        this.eventTypeId = eventTypeId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("eventTypeId", eventTypeId)
                .add("description", description)
                .toString();
    }
}
