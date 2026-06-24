package repository;

import model.Employee;
import model.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {

    boolean existsByEmployee(Employee employee);

    boolean findByEmployeeId(Employee employee);

    Family findByEmployeeId(UUID id);
}
