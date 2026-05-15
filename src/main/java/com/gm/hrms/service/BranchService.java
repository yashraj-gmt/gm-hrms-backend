package com.gm.hrms.service;

import com.gm.hrms.dto.request.BranchReorderRequestDTO;
import com.gm.hrms.dto.request.BranchRequestDTO;
import com.gm.hrms.dto.request.BranchUpdateDTO;
import com.gm.hrms.dto.response.BranchResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BranchService {

    BranchResponseDTO             create(BranchRequestDTO dto);
    BranchResponseDTO             update(Long id, BranchUpdateDTO dto);
    BranchResponseDTO             getById(Long id);
    PageResponseDTO<BranchResponseDTO> getAll(Pageable pageable);
    List<BranchResponseDTO>       getTree();
    void                          reorder(BranchReorderRequestDTO dto);
    void                          delete(Long id);
}