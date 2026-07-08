package com.pm.employeeservice.service;

import com.pm.employeeservice.Exceptions.FamilyNotFoundException;
import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.dto.FamilyRequestDTO;
import com.pm.employeeservice.dto.FamilyResponseDTO;
import lombok.RequiredArgsConstructor;
import com.pm.employeeservice.mapper.FamilyMapper;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.Family;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.FamilyRepository;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
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
    final Map<String, PatchHandler> handlers;
    public FamilyResponseDTO patchFamily(UUID id, FamilyRequestDTO updates){
        Family family = familyRepository.findById(id)
                .orElseThrow(() -> new FamilyNotFoundException("Family not Found"));
        ReflectionUtils.doWithFields(FamilyRequestDTO.class,field -> {
            field.setAccessible(true);
            try {
                Object value = field.get(updates);
                if (value != null) {
                    PatchHandler handler = handlers.get(field.getName());
                    if (handler != null) {
                        handler.apply(family, value);
                    } else {
                        log.warn("No patch handler found for field: {}", field.getName());
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("Error accessing field: {}", field.getName(), e);
            }
        });
        Family updatedFamily = familyRepository.save(family);
        return FamilyMapper.toDTO(updatedFamily);
    }
}
