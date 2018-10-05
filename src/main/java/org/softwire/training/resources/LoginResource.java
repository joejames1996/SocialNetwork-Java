package org.softwire.training.resources;

import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softwire.training.core.utils.Hash;
import org.softwire.training.core.utils.JwtSecrets;
import org.softwire.training.db.UserDao;
import org.softwire.training.models.LoginResponse;
import org.softwire.training.models.User;
import org.softwire.training.models.UserPrincipal;
import org.softwire.training.views.LoginView;
import org.softwire.training.views.NewUserView;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LoginResponse doLogin(@Auth UserPrincipal user) throws JoseException
    {
        return new LoginResponse(buildToken(user).getCompactSerialization());
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @FormParam("username") @NotEmpty String username,
            @FormParam("password") @NotEmpty String password) throws JoseException {


        User user = userDao.getUserByUsername(username);

        if(Hash.compareHashedPassword(password, user.getPassword()))
        {
            // create JWT

            String jwt = buildToken(new UserPrincipal(user)).getCompactSerialization();
            Cookie cookie = new Cookie("access_token", jwt);

            return Response.seeOther(URI.create("/home"))
                    .cookie(new NewCookie(cookie)).build();
            //return Response.seeOther(URI.create("/home")).build();
        }
        else
        {
            return Response.seeOther(URI.create("/login")).build();
        }
    }

    private JsonWebSignature buildToken(UserPrincipal user)
    {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject(user.getUser().getUsername());
        claims.setIssuedAtToNow();
        claims.setGeneratedJwtId();
        claims.setExpirationTimeMinutesInTheFuture(5);

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(JwtSecrets.key));


        return jws;
    }
}
