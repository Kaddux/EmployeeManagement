package com.pm.employeeservice.controller;


import com.pm.employeeservice.dto.FamilyRequestDTO;
import com.pm.employeeservice.dto.FamilyResponseDTO;
import lombok.RequiredArgsConstructor;
import com.pm.employeeservice.model.Family;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.pm.employeeservice.repository.FamilyRepository;
import com.pm.employeeservice.service.FamilyService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/family")
@RequiredArgsConstructor
public class FamilyController {
    private final FamilyService familyService;


    @PostMapping("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FamilyResponseDTO> createFamily(
            @PathVariable UUID employeeId, 
            @Valid @RequestBody FamilyRequestDTO familyRequestDTO){
        FamilyResponseDTO familyResponseDTO = familyService.createFamily(employeeId, familyRequestDTO);

        return ResponseEntity.ok().body(familyResponseDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FamilyResponseDTO>> getFamily(){
        List<FamilyResponseDTO> family = familyService.getFamily();

        return ResponseEntity.ok().body(family);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FamilyResponseDTO> getUniqueFamily(@PathVariable UUID id){
        FamilyResponseDTO familyResponse = familyService.getUniqueFamily(id);

        return ResponseEntity.ok().body(familyResponse);
    }
}
