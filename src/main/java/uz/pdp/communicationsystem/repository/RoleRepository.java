package uz.pdp.communicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.communicationsystem.entity.Role;
import uz.pdp.communicationsystem.entity.enums.RoleType;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Role findByRoleType(RoleType roleType);
}
