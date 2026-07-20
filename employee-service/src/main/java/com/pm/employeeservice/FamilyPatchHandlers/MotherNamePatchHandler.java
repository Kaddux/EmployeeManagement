package com.pm.employeeservice.FamilyPatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Family;
import org.springframework.stereotype.Component;

@Component("mother_name")
public class MotherNamePatchHandler implements PatchHandler<Family> {
    @Override
    public void apply(Family family, Object value) {
        family.setMother_name((String) value);
    }
}
