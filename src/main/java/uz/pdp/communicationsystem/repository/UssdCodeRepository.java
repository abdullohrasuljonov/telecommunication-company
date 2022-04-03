package uz.pdp.communicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.communicationsystem.entity.UssdCode;

public interface UssdCodeRepository extends JpaRepository<UssdCode,Integer> {
    boolean existsByCode(String code);
}
