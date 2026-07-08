package com.pm.employeeservice.FamilyPatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Family;
import org.springframework.stereotype.Component;

@Component("number_of_members")
public class NumberOfMembersPatchHandler implements PatchHandler {
    @Override
    public void apply(Family family, Object value) {
        family.setNumber_of_members(String.valueOf(value));
    }
}
