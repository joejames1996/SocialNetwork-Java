package org.softwire.training.core;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softwire.training.db.UserDao;
import org.softwire.training.models.User;
import org.softwire.training.models.UserPrincipal;

import java.util.Optional;

public class MyFaceAuthenticator implements Authenticator<BasicCredentials, UserPrincipal> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyFaceAuthenticator.class);

    private final UserDao userDao;

    public MyFaceAuthenticator(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Optional<UserPrincipal> authenticate(BasicCredentials credentials) {
        User user = userDao.getUser(credentials.getUsername());
        if(user.getPassword().equals(credentials.getPassword()))
        {
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.debug("Successfully authenticated user: {}", userPrincipal);
            return Optional.of(userPrincipal);
        }
        else
        {
            LOGGER.debug("Failed to authenticate user, incorrect password.  Username: {}", credentials.getUsername());
            return Optional.empty();
        }
    }
}
