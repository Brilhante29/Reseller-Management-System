package com.br.mobiauto.modules.dealerships.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DealershipRequestDTO {

    @NotBlank(message = "CNPJ is required")
    @Pattern(regexp = "\\d{14}", message = "CNPJ must be 14 digits")
    private String cnpj;

    @NotBlank(message = "Corporate name is required")
    private String corporateName;
}