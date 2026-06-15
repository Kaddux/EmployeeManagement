package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponseDTO {

    private String id;
    private String name;
    private String email;
    private String dateOfBirth;
    private String address;

}
