package org.example.authmicroservice.Repository;

import org.example.authmicroservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public boolean existsUserByUsername(String username);

    public Optional<User> findUserByUsername(String username);

    public boolean existsUserByEmail(String email);
}
