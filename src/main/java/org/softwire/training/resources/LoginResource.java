package org.softwire.training.resources;

import org.hibernate.validator.constraints.NotEmpty;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softwire.training.core.utils.Hash;
import org.softwire.training.core.utils.JwtSecrets;
import org.softwire.training.db.UserDao;
import org.softwire.training.models.User;
import org.softwire.training.models.UserPrincipal;
import org.softwire.training.views.LoginView;
import org.softwire.training.views.NewUserView;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(
            @FormParam("username") @NotEmpty String username,
            @FormParam("password") @NotEmpty String password) {


        User user = userDao.getUserByUsername(username);

        if(Hash.compareHashedPassword(password, user.getPassword()))
        {
            // create JWT

            return Response.seeOther(URI.create("/home")).build();
        }
        else
        {
            return Response.seeOther(URI.create("/login")).build();
        }
    }

    private JsonWebSignature buildToken(User user)
    {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject(user.getUsername());
        claims.setIssuedAtToNow();
        claims.setGeneratedJwtId();

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(JwtSecrets.key));
        return jws;
    }
}
