package dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FamilyRequestDTO {

    @NotNull(message = "No. of family members can't be empty")
    @Positive(message = "No. of family members can't be negative")
    private Integer number_of_members;

    @NotBlank(message = "Father's name cannot be empty")
    private String father_name;

    @NotBlank(message = "Mother's name cannot be empty")
    private String mother_name;

}
