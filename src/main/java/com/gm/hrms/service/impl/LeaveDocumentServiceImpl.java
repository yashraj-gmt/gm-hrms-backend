package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.LeaveDocumentResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.LeaveStatus;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeaveDocumentMapper;
import com.gm.hrms.repository.LeaveDocumentRepository;
import com.gm.hrms.repository.LeaveRequestRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.service.LeaveDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveDocumentServiceImpl implements LeaveDocumentService {

    private final LeaveDocumentRepository repository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PersonalInformationRepository personalRepository;

    // ================= UPLOAD =================
    @Override
    public void upload(Long leaveRequestId, Long personalId, List<MultipartFile> files) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        PersonalInformation user = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        for (MultipartFile file : files) {

            String path = saveFile(file);

            LeaveDocument doc = LeaveDocument.builder()
                    .leaveRequest(leaveRequest)
                    .uploadedBy(user)
                    .filePath(path)
                    .fileName(file.getOriginalFilename())
                    .uploadedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();

            repository.save(doc);
        }

        //  STATUS UPDATE
        if (leaveRequest.getStatus() == LeaveStatus.WAITING_FOR_DOCUMENT) {
            leaveRequest.setStatus(LeaveStatus.PENDING);
            leaveRequestRepository.save(leaveRequest);
        }
    }

    // ================= GET =================
    @Override
    public List<LeaveDocumentResponseDTO> getByLeave(Long leaveRequestId) {

        return repository.findByLeaveRequestIdAndIsActiveTrue(leaveRequestId)
                .stream()
                .map(LeaveDocumentMapper::toResponse)
                .toList();
    }

    // ================= DELETE =================
    @Override
    public void delete(Long documentId) {

        LeaveDocument doc = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        doc.setIsActive(false);

        repository.save(doc);
    }

    // ================= FILE STORAGE =================
    private String saveFile(MultipartFile file) {
        //  Replace with S3 / Cloud / local storage
        return "uploads/" + file.getOriginalFilename();
    }
}