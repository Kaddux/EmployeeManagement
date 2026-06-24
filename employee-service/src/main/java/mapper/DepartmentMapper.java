package mapper;

import dto.DepartmentRequestDTO;
import dto.DepartmentResponseDTO;
import model.Department;

public class DepartmentMapper {

    public static DepartmentResponseDTO toDTO(Department department){
        DepartmentResponseDTO departmentResponseDTO = new DepartmentResponseDTO();
        departmentResponseDTO.setDepartment_id(department.getDepartment_id());
        departmentResponseDTO.setDepartment_code(department.getDepartment_code());
        departmentResponseDTO.setDepartment_name(department.getDepartment_name());

        return departmentResponseDTO;
    }

    public static Department toModel(DepartmentRequestDTO departmentRequestDTO){

        Department department = new Department();

        department.setDepartment_name(departmentRequestDTO.getDepartment_name());
        department.setDepartment_code(departmentRequestDTO.getDepartment_code());

        return department;

    }
}
