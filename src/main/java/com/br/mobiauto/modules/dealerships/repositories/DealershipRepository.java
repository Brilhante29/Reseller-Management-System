package com.br.mobiauto.modules.dealerships.repositories;


import com.br.mobiauto.modules.dealerships.models.Dealership;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealershipRepository extends MongoRepository<Dealership, String> {
    Optional<Dealership> findByCnpj(String cnpj);
}