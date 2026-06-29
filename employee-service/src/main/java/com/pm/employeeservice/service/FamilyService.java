package com.pm.employeeservice.service;

import com.pm.employeeservice.dto.FamilyRequestDTO;
import com.pm.employeeservice.dto.FamilyResponseDTO;
import lombok.RequiredArgsConstructor;
import com.pm.employeeservice.mapper.FamilyMapper;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.Family;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.FamilyRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final EmployeeRepository employeeRepository;

    public FamilyResponseDTO createFamily(UUID employee_id, FamilyRequestDTO familyRequestDTO){
        Employee employee = employeeRepository.findById(employee_id)
                .orElseThrow(() -> new RuntimeException("Employee not found for id " + employee_id));

        Family newfamily = FamilyMapper.toModel(familyRequestDTO);
        newfamily.setEmployee(employee);
        
        // Persist the family record to the database
        newfamily = familyRepository.save(newfamily);

        return FamilyMapper.toDTO(newfamily);
    }

    public List<FamilyResponseDTO> getFamily(){
        List<Family> family = familyRepository.findAll();
        return family.stream().map(FamilyMapper ::toDTO).toList();
    }

    public FamilyResponseDTO getUniqueFamily(UUID id){

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found for id " + id));
        Family family = familyRepository.findByEmployeeId(employee.getId());

        return FamilyMapper.toDTO(family);
    }
}
