package uz.pdp.communicationsystem.entity;

import uz.pdp.communicationsystem.entity.template.AbsEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uz.pdp.communicationsystem.entity.enums.ActionType;


import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Details extends AbsEntity {

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @ManyToOne//bitta simkartaga ko'pgina detaillar to'g'ri keladi
    private SimCard simCard;

    //ayni vaqtda qancha miqdorda ishlatilganligi
    private Float amount;
}
