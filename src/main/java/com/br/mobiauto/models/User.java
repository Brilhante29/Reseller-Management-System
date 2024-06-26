package com.br.mobiauto.models;

import com.br.mobiauto.models.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String name;
    private String password;

    private Role role;
}
