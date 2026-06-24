package dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DepartmentRequestDTO {

    @NotBlank
    private String department_name;

    @NotBlank
    private String department_code;
}
