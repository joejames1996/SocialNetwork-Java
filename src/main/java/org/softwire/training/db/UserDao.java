package org.softwire.training.db;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.softwire.training.models.User;

public class UserDao
{
    private final Jdbi jdbi;

    public UserDao(Jdbi jdbi)
    {
        this.jdbi = jdbi;
    }

    public void createNewUser(User user)
    {
        try (Handle handle = jdbi.open())
        {
            handle.createCall("INSERT INTO users (username, password, fullname) VALUES (:username, :password, :fullname)")
                    .bindBean(user)
                    .invoke();
        }
    }

    public User getUserByUsername(String username)
    {
        try(Handle handle = jdbi.open())
        {
            return handle.select("SELECT * FROM users WHERE username = :username")
                    .bind("username", username)
                    .mapToBean(User.class)
                    .findOnly();
        }
    }

    public User getUserByUserId(int userId)
    {
        try(Handle handle = jdbi.open())
        {
            return handle.select("SELECT * FROM users WHERE userId = :userId")
                    .bind("userId", userId)
                    .mapToBean(User.class)
                    .findOnly();
        }
    }
}
