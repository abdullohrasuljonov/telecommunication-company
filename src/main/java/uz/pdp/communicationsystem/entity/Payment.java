package uz.pdp.communicationsystem.entity;

import lombok.Data;
import uz.pdp.communicationsystem.entity.enums.PaymentType;
import uz.pdp.communicationsystem.entity.template.AbsEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

@Entity
@Data
public class Payment extends AbsEntity{

    @OneToOne
    private SimCard simCard;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private double amount;

    private String payerName;

    private String payerId;
    //qo'lda chek nomeri
}
