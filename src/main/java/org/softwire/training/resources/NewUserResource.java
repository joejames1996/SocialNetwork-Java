package org.softwire.training.resources;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softwire.training.db.UserDao;
import org.softwire.training.models.User;
import org.softwire.training.views.NewUserView;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Resource for creating new users
 */
@Path("/signup")
public class NewUserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(WallResource.class);

    private final UserDao userDao;

    public NewUserResource(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public NewUserView get() {
        return new NewUserView();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @FormParam("username") @NotEmpty String username,
            @FormParam("password") @NotEmpty String password,
            @FormParam("fullname") @NotEmpty String fullname) {

        User user = new User(username, password, fullname);

        userDao.createNewUser(user);

        return Response.seeOther(URI.create("/home")).build();
    }
}
