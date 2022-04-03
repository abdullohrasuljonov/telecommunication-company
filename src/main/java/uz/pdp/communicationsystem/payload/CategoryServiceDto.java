package uz.pdp.communicationsystem.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryServiceDto {
    private Integer id;
    private String name;
}
