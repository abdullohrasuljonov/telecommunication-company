package uz.pdp.communicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.communicationsystem.entity.CategoryService;

public interface ServiceCategoryRepository extends JpaRepository<CategoryService,Integer> {
    boolean existsByName(String name);
}
