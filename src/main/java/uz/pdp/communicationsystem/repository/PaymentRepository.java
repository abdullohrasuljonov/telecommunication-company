package uz.pdp.communicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.communicationsystem.entity.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllBySimCardNumber(String simCard_number);
}
