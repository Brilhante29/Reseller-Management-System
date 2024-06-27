package com.br.mobiauto.modules.dealerships.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealershipResponseDTO {
    private String id;
    private String cnpj;
    private String corporateName;
}
