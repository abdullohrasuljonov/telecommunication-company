package uz.pdp.communicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.communicationsystem.entity.Tarif;

import java.util.UUID;

public interface TariffRepository extends JpaRepository<Tarif, UUID> {
    boolean existsByName(String name);
    Tarif findByName(String name);
}
