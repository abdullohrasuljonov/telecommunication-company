package uz.pdp.communicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.communicationsystem.entity.Details;
import uz.pdp.communicationsystem.entity.SimCard;
import uz.pdp.communicationsystem.entity.enums.ActionType;

import java.util.List;
import java.util.UUID;

public interface DetailsRepository extends JpaRepository<Details, UUID> {
    List<Details> findAllByActionTypeAndSimCard(ActionType actionType, SimCard simCard);
    List<Details> findAllBySimCard(SimCard simCard);
}
