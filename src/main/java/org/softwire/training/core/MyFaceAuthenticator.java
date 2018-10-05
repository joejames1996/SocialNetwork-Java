package org.softwire.training.core;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softwire.training.core.utils.Hash;
import org.softwire.training.db.UserDao;
import org.softwire.training.models.User;
import org.softwire.training.models.UserPrincipal;

import java.util.Optional;

//public class MyFaceAuthenticator implements Authenticator<BasicCredentials, UserPrincipal> {
public class MyFaceAuthenticator implements Authenticator<JwtContext, UserPrincipal> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyFaceAuthenticator.class);

    private final UserDao userDao;

    public MyFaceAuthenticator(UserDao userDao)
    {
        this.userDao = userDao;
    }

//    Old HTTP authenticator
//    @Override
//    public Optional<UserPrincipal> authenticate(BasicCredentials credentials) {
//        User user = userDao.getUserByUsername(credentials.getUsername());
//        LOGGER.debug(user.getPassword());
//        //if(user.getPassword().equals(credentials.getPassword()))
//        if(Hash.compareHashedPassword(credentials.getPassword(), user.getPassword()))
//        {
//            UserPrincipal userPrincipal = new UserPrincipal(user);
//            LOGGER.debug("Successfully authenticated user: {}", userPrincipal);
//            return Optional.of(userPrincipal);
//        }
//        else
//        {
//            LOGGER.debug("Failed to authenticate user, incorrect password.  Username: {}", credentials.getUsername());
//            return Optional.empty();
//        }
//    }

    @Override
    public Optional<UserPrincipal> authenticate(JwtContext context)
    {
        try
        {
            JwtClaims claims = context.getJwtClaims();
            String username = claims.getSubject();
            UserPrincipal user = new UserPrincipal(userDao.getUserByUsername(username));
            LOGGER.debug("User " + user + " logged in");
            return Optional.of(user);
        }
        catch(Exception e)
        {
            LOGGER.debug("Could not log in user");
            return Optional.empty();
        }
    }

}
