package mapper;

import dto.FamilyRequestDTO;
import dto.FamilyResponseDTO;
import model.Family;


public class FamilyMapper {
    public static FamilyResponseDTO toDTO(Family family) {
        FamilyResponseDTO FamilyDTO = new FamilyResponseDTO();
        FamilyDTO.setFamilyId(family.getFamily_id());
        FamilyDTO.setFather_name(family.getFather_name());
        FamilyDTO.setMother_name(family.getMother_name());
        if (family.getEmployee() != null) {
            FamilyDTO.setEmployee_name(family.getEmployee().getName());
        }

        return FamilyDTO;
    }
    //converting FamilyRequestDTO to an entity model
    public static Family toModel(FamilyRequestDTO familyRequestDTO){
        Family family = new Family();
        // Note: family_id is auto-generated, so we don't set it here
        family.setFather_name(familyRequestDTO.getFather_name());
        family.setMother_name(familyRequestDTO.getMother_name());
        family.setNumber_of_members(String.valueOf(familyRequestDTO.getNumber_of_members()));
        // Note: Employee is set by the Service layer, not here

        return family;
    }
}
