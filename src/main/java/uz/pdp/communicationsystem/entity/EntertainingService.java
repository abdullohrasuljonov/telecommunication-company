package uz.pdp.communicationsystem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uz.pdp.communicationsystem.entity.template.AbsEntity;

import javax.persistence.*;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class EntertainingService extends AbsEntity {
    private String name;

    private double price;

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private CategoryService categoryService;

    private Timestamp expiredDate;

    @ManyToOne
    private SimCard simCard;
    private Integer count;
}
