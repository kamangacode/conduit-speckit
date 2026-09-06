package com.conduit.infrastructure.persistence;

import com.conduit.application.user.UserRepository;
import com.conduit.domain.user.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final JdbcTemplate jdbc;

    public UserRepositoryAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public User save(User user) {
        UserRecord record = UserRecord.fromDomain(user);
        boolean exists = !jdbc.query("select id from users where id = ?", (rs, row) -> rs.getObject(1, UUID.class), user.id()).isEmpty();
        if (exists) {
            jdbc.update("update users set email=?, username=?, password_hash=?, bio=?, image=? where id=?",
                    record.email(), record.username(), record.passwordHash(), record.bio(), record.image(), record.id());
        } else {
            jdbc.update("insert into users(id,email,username,password_hash,bio,image) values (?,?,?,?,?,?)",
                    record.id(), record.email(), record.username(), record.passwordHash(), record.bio(), record.image());
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return query("select * from users where id = ?", id).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return query("select * from users where email = ?", email).stream().findFirst();
    }

    @Override
    public boolean existsByEmail(String email, UUID excludedId) {
        return exists("email", email, excludedId);
    }

    @Override
    public boolean existsByUsername(String username, UUID excludedId) {
        return exists("username", username, excludedId);
    }

    private boolean exists(String field, String value, UUID excludedId) {
        String sql = "select id from users where " + field + " = ? and (? is null or id <> ?)";
        return !jdbc.query(sql, (rs, row) -> rs.getObject(1, UUID.class), value, excludedId, excludedId).isEmpty();
    }

    @SuppressWarnings("null")
    private List<User> query(String sql, Object... args) {
        return jdbc.query(sql, (rs, row) -> new UserRecord(
                rs.getObject("id", UUID.class),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("bio"),
                rs.getString("image")).toDomain(), args);
    }
}
