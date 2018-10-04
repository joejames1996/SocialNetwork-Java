package org.softwire.training.resources;

import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softwire.training.db.UserDao;
import org.softwire.training.db.WallDao;
import org.softwire.training.models.EventType;
import org.softwire.training.models.UserPrincipal;
import org.softwire.training.models.SocialEvent;
import org.softwire.training.models.User;
import org.softwire.training.views.WallView;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource for viewing and writing to walls
 */
@Path("/wall")
public class WallResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(WallResource.class);

    private final WallDao wallDao;
    private final UserDao userDao;

    public WallResource(WallDao wallDao, UserDao userDao) {
        this.wallDao = wallDao;
        this.userDao = userDao;
    }

    @GET
    @Path("{subjectName}")
    @Produces(MediaType.TEXT_HTML)
    public WallView get(
            @Auth UserPrincipal userPrincipal,
            @PathParam("subjectName")  @NotEmpty String subjectName) {
        //User subject = new User(subjectName);
        User subject = userDao.getUserByUsername(subjectName);

        LOGGER.info("Get wall. User: {} Subject: {}", userPrincipal, subject);

        List<SocialEvent> socialEvents = wallDao.readWall(subject);
        List<SocialEvent> socialEventsWithDelete = new ArrayList<>();

        for(SocialEvent socialEvent : socialEvents)
        {
            if(hasPermissionToDelete(userPrincipal, subject, socialEvent))
            {
                socialEvent.setCanBeDeleted(true);
            }
            else
            {
                socialEvent.setCanBeDeleted(false);
            }
            LOGGER.debug(socialEvent.toString());
            socialEventsWithDelete.add(socialEvent);
        }



        return new WallView(socialEventsWithDelete, subject, userPrincipal.getUser());
    }

    private boolean hasPermissionToDelete(@Auth UserPrincipal userPrincipal, User subject, SocialEvent socialEvent)
    {
        return socialEvent.getAuthorId() == userPrincipal.getUser().getUserId() || subject.getUserId() == userPrincipal.getUser().getUserId();
    }

    @POST
    @Path("{subjectName}/write")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @Auth UserPrincipal userPrincipal,
            @PathParam("subjectName") @NotEmpty String subjectName,
            @FormParam("message") @NotEmpty String message) {
        //User subject = new User(subjectName);

        User subject = userDao.getUserByUsername(subjectName);

        LOGGER.info("Post to Wall. User: {} Subject: {} Message: {}",
                userPrincipal, subject, message);

        SocialEvent socialEvent = new SocialEvent(userPrincipal.getUser(), message);
        wallDao.writeOnWall(subject, socialEvent);
        return Response.seeOther(URI.create("/wall/" + subjectName)).build();
    }

    @GET
    @Path("{subjectName}/delete/{postId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getAfterDeletedPost(
            @Auth UserPrincipal userPrincipal,
            @PathParam("subjectName") @NotEmpty String subjectName,
            @PathParam("postId") @NotNull int postId)
    {
        User subject = userDao.getUserByUsername(subjectName);
        SocialEvent socialEvent = wallDao.getSocialEventById(postId);

        if(hasPermissionToDelete(userPrincipal, subject, socialEvent))
        {
            wallDao.deletePost(socialEvent.getId());
        }

        return Response.seeOther(URI.create("/wall/" + subjectName)).build();
    }

    @GET
    @Path("{subjectName}/addevent/{eventId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getAfterNewEventType(
            @Auth UserPrincipal userPrincipal,
            @PathParam("subjectName") @NotEmpty String subjectName,
            @PathParam("eventId") @NotNull int eventId)
    {
        User subject = userDao.getUserByUsername(subjectName);

        SocialEvent socialEvent = new SocialEvent(userPrincipal.getUser(), new EventType(eventId));
        wallDao.addSocialEventOfType(subject, socialEvent);
        return Response.seeOther(URI.create("/wall/" + subjectName)).build();
    }
}
