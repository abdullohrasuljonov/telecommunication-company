package uz.pdp.communicationsystem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uz.pdp.communicationsystem.entity.template.AbsEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Service extends AbsEntity {
    private String name;

    private double price;

    @ManyToOne
    private CategoryService categoryService;

    private Timestamp expiredDate;
}
