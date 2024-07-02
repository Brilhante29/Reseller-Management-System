package com.br.mobiauto.modules.opportunities.repositories;

import com.br.mobiauto.modules.opportunities.models.Opportunity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityRepository extends MongoRepository<Opportunity, String> {
    List<Opportunity> findAllByUserIsNull();
    List<Opportunity> findAllByUserId(String userId);
}
