package mapper;

import dto.EmployeeRequestDTO;
import dto.EmployeeResponseDTO;
import model.Employee;
import Enum.Role;

import java.time.LocalDate;

public class EmployeeMapper {
    public static EmployeeResponseDTO toDTO(Employee employee){
        EmployeeResponseDTO EmployeeDTO = new EmployeeResponseDTO();
        EmployeeDTO.setId(employee.getId().toString());
        EmployeeDTO.setName(employee.getName());
        EmployeeDTO.setAddress(employee.getAddress());
        EmployeeDTO.setEmail(employee.getEmail());
        if (employee.getDepartment() != null) {
            EmployeeDTO.setDepartment_id(employee.getDepartment().getDepartment_id().toString());
            EmployeeDTO.setDepartment_name(employee.getDepartment().getDepartment_name());
            EmployeeDTO.setDepartment_code(employee.getDepartment().getDepartment_code());
        }
        EmployeeDTO.setRole(employee.getRole().toString());
        EmployeeDTO.setDateOfBirth(employee.getDate_of_birth().toString());

        return EmployeeDTO;
    }

    //converting EmployeeRequestDTO to an entity model
    public static Employee toModel(EmployeeRequestDTO employeeRequestDTO){
        Employee employee = new Employee();
        employee.setName(employeeRequestDTO.getName());
        employee.setAddress(employeeRequestDTO.getAddress());
        employee.setEmail(employeeRequestDTO.getEmail());
        employee.setRole(Role.valueOf(employeeRequestDTO.getRole()));
        // Department setting is handled in the service layer where DB access is available
        employee.setDate_of_birth(LocalDate.parse(employeeRequestDTO.getDateOfBirth()));

        return employee;
    }
}
