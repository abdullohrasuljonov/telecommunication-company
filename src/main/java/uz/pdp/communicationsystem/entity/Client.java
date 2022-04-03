package uz.pdp.communicationsystem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uz.pdp.communicationsystem.entity.enums.ClientType;
import uz.pdp.communicationsystem.entity.template.AbsEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Client extends AbsEntity {

    private String passportNumber;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @ManyToMany
    private Set<Role> roles;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SimCard> simCardList;
}
