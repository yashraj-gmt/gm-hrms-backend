package com.gm.hrms.service;

import com.gm.hrms.dto.response.LeaveDocumentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LeaveDocumentService {

    void upload(Long leaveRequestId, Long personalId, List<MultipartFile> files);

    List<LeaveDocumentResponseDTO> getByLeave(Long leaveRequestId);

    void delete(Long documentId);
}