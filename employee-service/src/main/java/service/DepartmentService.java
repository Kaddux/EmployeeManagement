package service;


import Exceptions.DepartmentAlreadyExistsException;
import Exceptions.DepartmentNotFoundException;
import dto.DepartmentRequestDTO;
import dto.DepartmentResponseDTO;
import lombok.RequiredArgsConstructor;
import mapper.DepartmentMapper;
import model.Department;
import org.springframework.stereotype.Service;
import repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentRequestDTO){
        if(departmentRepository.existsByDepartment_code(departmentRequestDTO.getDepartment_code()))
            throw new DepartmentAlreadyExistsException("Department already exists");

        Department department = DepartmentMapper.toModel(departmentRequestDTO);

        Department saveDepartment = departmentRepository.save(department);

        return DepartmentMapper.toDTO(saveDepartment);
    }

    public DepartmentResponseDTO deleteDepartment(Integer department_id){
        Department department = departmentRepository.findById(department_id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found"));

        departmentRepository.deleteById(department_id);

        return DepartmentMapper.toDTO(department);
    }
}
