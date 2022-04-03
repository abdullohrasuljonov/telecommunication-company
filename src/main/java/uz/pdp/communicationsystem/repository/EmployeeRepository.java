package uz.pdp.communicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.communicationsystem.entity.Employee;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByUsername(String username);
    boolean existsByUserName(String userName);
}
