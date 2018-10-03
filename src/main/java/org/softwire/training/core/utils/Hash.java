package org.softwire.training.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kosprov.jargon2.api.Jargon2.*;

public class Hash
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Hash.class);

    private static Hasher hasher = jargon2Hasher()
            .type(Type.ARGON2id)
            .memoryCost(65536)
            .timeCost(3)
            .parallelism(4)
            .saltLength(16)
            .hashLength(64);

    public static String hashPassword(String password)
    {
        byte[] passwordToByteArray = password.getBytes();

        String hashedPassword = hasher.password(passwordToByteArray).encodedHash();
        LOGGER.debug(hashedPassword);
        Verifier verifier = jargon2Verifier();
        boolean matches = verifier.hash(hashedPassword).password(passwordToByteArray).verifyEncoded();
        System.out.println(matches);
        String matchString = matches ? "true" : "false";
        LOGGER.debug(matchString);
        return hashedPassword;
    }

    public static boolean compareHashedPassword(String password, String hashedPassword)
    {
        byte[] passwordToByteArray = password.getBytes();
        Verifier verifier = jargon2Verifier();
        boolean matches = verifier.hash(hashedPassword).password(passwordToByteArray).verifyEncoded();
        return matches;
    }
}
