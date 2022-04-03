package uz.pdp.communicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.communicationsystem.entity.EntertainingService;
import uz.pdp.communicationsystem.entity.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<EntertainingService, UUID> {
    Optional<EntertainingService> findByName(String name);
    boolean existsByName(String name);
    List<EntertainingService> findAllByOrderByCountAsc();
}
