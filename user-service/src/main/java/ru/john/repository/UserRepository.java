package ru.john.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.john.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
