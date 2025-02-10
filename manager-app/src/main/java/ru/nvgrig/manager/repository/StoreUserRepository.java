package ru.nvgrig.manager.repository;

import org.springframework.data.repository.CrudRepository;
import ru.nvgrig.manager.entity.StoreUser;

import java.util.Optional;

public interface StoreUserRepository extends CrudRepository<StoreUser, Integer> {

    Optional<StoreUser> findByUsername(String username);
}
