package com.br.mobiauto.modules.opportunities.models;

import com.br.mobiauto.modules.opportunities.models.enums.OpportunityStatus;
import com.br.mobiauto.modules.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "opportunities")
public class Opportunity {

    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private String brand;
    private String model;
    private String version;
    private int yearModel;
    private OpportunityStatus status;
    private String conclusionReason;
    private Date assignedDate;
    private Date conclusionDate;

    @DBRef
    private User user;
}
