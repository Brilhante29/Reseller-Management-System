package com.br.mobiauto.modules.opportunities.dtos;

import com.br.mobiauto.modules.opportunities.models.enums.OpportunityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpportunityResponseDTO {

    private String id;
    private OpportunityStatus status;
    private String name;
    private String email;
    private String phone;
    private String brand;
    private String model;
    private String version;
    private int yearModel;
    private String conclusionReason;
}
