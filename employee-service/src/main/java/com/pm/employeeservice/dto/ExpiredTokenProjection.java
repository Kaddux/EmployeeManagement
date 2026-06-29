package com.pm.employeeservice.dto;

import java.math.BigInteger;

public record ExpiredTokenProjection (
    BigInteger token_id,
    String token,
    String email,
    String employeeName
){};

