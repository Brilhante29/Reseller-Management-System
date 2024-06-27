package com.br.mobiauto.modules.dealerships.services;

import com.br.mobiauto.modules.dealerships.dtos.DealershipRequestDTO;
import com.br.mobiauto.modules.dealerships.dtos.DealershipResponseDTO;
import java.util.List;

public interface IDealershipService {
    public List<DealershipResponseDTO> getAllDealerships();
    public DealershipResponseDTO getDealershipById(String id);
    public DealershipResponseDTO createDealership(DealershipRequestDTO dealershipRequestDTO);
    public DealershipResponseDTO updateDealership(String id, DealershipRequestDTO dealershipRequestDTO);
    public void deleteDealership(String id);
}
