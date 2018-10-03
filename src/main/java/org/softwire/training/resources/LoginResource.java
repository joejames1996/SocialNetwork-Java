package org.softwire.training.resources;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softwire.training.core.utils.Hash;
import org.softwire.training.db.UserDao;
import org.softwire.training.models.User;
import org.softwire.training.views.LoginView;
import org.softwire.training.views.NewUserView;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Resource for creating new users
 */
@Path("/login")
public class LoginResource
{

    private static final Logger LOGGER = LoggerFactory.getLogger(WallResource.class);

    private final UserDao userDao;

    public LoginResource(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public LoginView get() {
        return new LoginView();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @FormParam("username") @NotEmpty String username,
            @FormParam("password") @NotEmpty String password) {

        //String salt = Salt.createSalt();
        String hashedPassword = Hash.hashPassword(password);

        User user = userDao.getUserByUsername(username);

        userDao.createNewUser(user);

        return Response.seeOther(URI.create("/home")).build();
    }
}
