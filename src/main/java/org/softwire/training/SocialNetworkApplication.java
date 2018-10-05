package org.softwire.training;

import com.github.arteam.jdbi3.JdbiFactory;
import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.jersey.errors.ErrorEntityWriter;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewBundle;
import org.jdbi.v3.core.Jdbi;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softwire.training.core.BasicAuthenticator;
import org.softwire.training.core.MyFaceAuthenticator;
import org.softwire.training.core.utils.JwtSecrets;
import org.softwire.training.db.UserDao;
import org.softwire.training.db.WallDao;
import org.softwire.training.models.User;
import org.softwire.training.models.UserPrincipal;
import org.softwire.training.resources.*;
import org.softwire.training.views.ErrorView;

import javax.ws.rs.core.MediaType;

/**
 * Main MyFace application
 *
 * Run with no arguments to use the default config file, or 'server myconfig.yml' to use a custom configuration.
 */
public class SocialNetworkApplication extends Application<SocialNetworkConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocialNetworkApplication.class);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            args = new String[]{"server", "config.yml"};
        }
        new SocialNetworkApplication().run(args);
    }

    @Override
    public String getName() {
        return "socialnetwork";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(final Bootstrap<SocialNetworkConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(final SocialNetworkConfiguration configuration,
                    final Environment environment) {
        LOGGER.info("Starting MyFace Social Network");

        // Database setup
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        final WallDao wallDao = new WallDao(jdbi);
        final UserDao userDao = new UserDao(jdbi);

        // Register Resources
        environment.jersey().register(new HomePageResource(wallDao));
        environment.jersey().register(new WallResource(wallDao, userDao));
        environment.jersey().register(new LandingPageResource());
        environment.jersey().register(new NewUserResource(userDao));
        environment.jersey().register(new LoginResource(userDao));
        environment.jersey().register(new ErrorEntityWriter<ErrorMessage, View>(MediaType.TEXT_HTML_TYPE, View.class)
        {
            @Override
            protected View getRepresentation(ErrorMessage errorMessage)
            {
                return new ErrorView(errorMessage);
            }
        });

        // HTTP Basic Auth setup
//        environment.jersey().register(new AuthDynamicFeature(
//                new BasicCredentialAuthFilter.Builder<UserPrincipal>()
//                        .setAuthenticator(new MyFaceAuthenticator(userDao))
//                        .setRealm("Super Secret Social Network")
//                        .buildAuthFilter()));

        // JWT Auth Setup
        final JwtConsumer consumer = new JwtConsumerBuilder()
                .setAllowedClockSkewInSeconds(30)
                .setRequireExpirationTime()
                .setRequireSubject()
                .setVerificationKey(new HmacKey(JwtSecrets.key))
                .setRelaxVerificationKeyValidation()
                .build();

        environment.jersey().register(new AuthDynamicFeature(
                new JwtAuthFilter.Builder<UserPrincipal>()
                .setJwtConsumer(consumer)
                .setRealm("Super Secret Social Network")
                .setPrefix("Bearer")
                .setAuthenticator(new MyFaceAuthenticator(userDao))
                .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(UserPrincipal.class));
    }
}
