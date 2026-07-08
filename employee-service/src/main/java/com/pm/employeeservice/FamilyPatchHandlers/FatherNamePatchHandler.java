package com.pm.employeeservice.FamilyPatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Family;
import org.springframework.stereotype.Component;

@Component("father_name")
public class FatherNamePatchHandler implements PatchHandler {
    @Override
    public void apply(Family family, Object value) {
        family.setFather_name((String) value);
    }
}
