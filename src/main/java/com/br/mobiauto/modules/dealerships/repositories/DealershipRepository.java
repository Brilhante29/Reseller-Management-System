package com.br.mobiauto.modules.dealerships.repositories;


import com.br.mobiauto.modules.dealerships.models.Dealership;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DealershipRepository extends MongoRepository<Dealership, String> {
    Optional<Dealership> findByCnpj(String cnpj);
}