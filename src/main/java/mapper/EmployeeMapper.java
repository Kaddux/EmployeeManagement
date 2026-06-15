package mapper;

import dto.EmployeeRequestDTO;
import dto.EmployeeResponseDTO;
import model.Employee;

import java.time.LocalDate;

public class EmployeeMapper {
    public static EmployeeResponseDTO toDTO(Employee employee){
        EmployeeResponseDTO EmployeeDTO = new EmployeeResponseDTO();
        EmployeeDTO.setId(employee.getId().toString());
        EmployeeDTO.setName(employee.getName());
        EmployeeDTO.setAddress(employee.getAddress());
        EmployeeDTO.setEmail(employee.getEmail());
        EmployeeDTO.setDateOfBirth(employee.getDate_of_birth().toString());

        return EmployeeDTO;
    }

    //converting EmployeeRequestDTO to an entity model
    public static Employee toModel(EmployeeRequestDTO employeeRequestDTO){
        Employee employee = new Employee();
        employee.setName(employeeRequestDTO.getName());
        employee.setAddress(employeeRequestDTO.getAddress());
        employee.setEmail(employeeRequestDTO.getEmail());
        employee.setDate_of_birth(LocalDate.parse(employeeRequestDTO.getDateOfBirth()));

        return employee;

    }
}
