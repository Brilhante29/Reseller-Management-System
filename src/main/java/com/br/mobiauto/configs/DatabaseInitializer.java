package com.br.mobiauto.configs;

import com.br.mobiauto.modules.dealerships.models.Dealership;
import com.br.mobiauto.modules.dealerships.repositories.DealershipRepository;
import com.br.mobiauto.modules.opportunities.models.Opportunity;
import com.br.mobiauto.modules.opportunities.models.enums.OpportunityStatus;
import com.br.mobiauto.modules.opportunities.repositories.OpportunityRepository;
import com.br.mobiauto.modules.users.models.User;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Configuration
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DealershipRepository dealershipRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Limpar coleções
        mongoTemplate.getDb().drop();

        // Criar e salvar Dealerships
        Dealership dealership1 = Dealership.builder()
                .id("dealership1")
                .cnpj("12345678901234")
                .corporateName("Dealership One")
                .build();

        Dealership dealership2 = Dealership.builder()
                .id("dealership2")
                .cnpj("23456789012345")
                .corporateName("Dealership Two")
                .build();

        dealershipRepository.saveAll(Arrays.asList(dealership1, dealership2));

        // Criar e salvar Users
        User user1 = User.builder()
                .id("user1")
                .email("user1@example.com")
                .name("User One")
                .password("password1")
                .role(Role.ADMIN)
                .dealership(dealership1)
                .build();

        User user2 = User.builder()
                .id("user2")
                .email("user2@example.com")
                .name("User Two")
                .password("password2")
                .role(Role.ADMIN)
                .dealership(dealership2)
                .build();

        userRepository.saveAll(Arrays.asList(user1, user2));

        // Atualizar Dealerships com Users
        dealership1.setUsers(Collections.singletonList(user1));
        dealership2.setUsers(Collections.singletonList(user2));

        dealershipRepository.saveAll(Arrays.asList(dealership1, dealership2));

        // Criar e salvar Opportunities
        Opportunity opportunity1 = Opportunity.builder()
                .id("opportunity1")
                .name("Opportunity One")
                .email("opportunity1@example.com")
                .phone("1234567890")
                .brand("Brand One")
                .model("Model One")
                .version("Version One")
                .yearModel(2021)
                .status(OpportunityStatus.IN_PROGRESS)
                .assignedDate(new Date())
                .user(user1)
                .build();

        Opportunity opportunity2 = Opportunity.builder()
                .id("opportunity2")
                .name("Opportunity Two")
                .email("opportunity2@example.com")
                .phone("0987654321")
                .brand("Brand Two")
                .model("Model Two")
                .version("Version Two")
                .yearModel(2022)
                .status(OpportunityStatus.COMPLETED)
                .conclusionReason("Sold")
                .conclusionDate(new Date())
                .user(user2)
                .build();

        opportunityRepository.saveAll(Arrays.asList(opportunity1, opportunity2));
    }
}
