package com.br.mobiauto.modules.users.repositories;

import com.br.mobiauto.modules.users.models.User;
import com.br.mobiauto.modules.users.models.enums.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findAllByDealershipId(String dealershipId);
    List<User> findAllByRole(Role role);
}
