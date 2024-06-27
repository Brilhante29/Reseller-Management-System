package com.br.mobiauto.modules.dealerships.models;

import com.br.mobiauto.modules.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "dealerships")
public class Dealership {
    @Id
    private String id;

    @Indexed(unique = true)
    private String cnpj;

    private String corporateName;

    @DBRef
    private List<User> users;
}