package uz.pdp.communicationsystem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uz.pdp.communicationsystem.entity.enums.PacketType;
import uz.pdp.communicationsystem.entity.template.AbsEntity;


import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Packet extends AbsEntity {
    @Enumerated(EnumType.STRING)
    private PacketType packetType;

    @Column(nullable = false,unique = true)
    private String name;

    private int amount;

    private int cost;

    private int duration; // 5 kunlik
    private boolean isTariff;

    @OneToMany
    private List<Tarif> availableTarifs; //shu tariflarga dostup
}
