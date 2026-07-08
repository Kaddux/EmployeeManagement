package com.pm.employeeservice.service;

import com.pm.employeeservice.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.pm.employeeservice.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return   employeeRepository.findByEmail(email)
                .map(EmployeeUserDetailsService::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}