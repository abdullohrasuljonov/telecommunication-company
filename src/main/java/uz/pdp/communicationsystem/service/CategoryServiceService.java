package uz.pdp.communicationsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import uz.pdp.communicationsystem.entity.CategoryService;
import uz.pdp.communicationsystem.payload.ApiResponse;
import uz.pdp.communicationsystem.payload.CategoryServiceDto;
import uz.pdp.communicationsystem.repository.ServiceCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceService {
    @Autowired
    ServiceCategoryRepository serviceCategoryRepository;
    public List<CategoryService> getCategoriesList(){
        return serviceCategoryRepository.findAll();
    }
    public ApiResponse getServiceCategoryById(Integer id){
        Optional<CategoryService> optionalServiceCategory = serviceCategoryRepository.findById(id);
        if (!optionalServiceCategory.isPresent()){
            return new ApiResponse("category id not founded",false,null);
        }
        CategoryService category = optionalServiceCategory.get();
        return new ApiResponse("category founded",true,category);
    }
    public ApiResponse addCategory(CategoryServiceDto categoryServiceDto){
        boolean exists = serviceCategoryRepository.existsByName(categoryServiceDto.getName());
        if (exists)
            return new ApiResponse("Bunday Category mavjud",false,null);
        CategoryService serviceCategory = new CategoryService();
        serviceCategory.setName(categoryServiceDto.getName());
        serviceCategoryRepository.save(serviceCategory);
        return new ApiResponse("Category qo'shildi",false,serviceCategory);

    }
    public ApiResponse editCategory(CategoryServiceDto categoryServiceDto,Integer id){
        Optional<CategoryService> categoryOptional = serviceCategoryRepository.findById(id);
        if (!categoryOptional.isPresent())
            return new ApiResponse("category id not founded",false,null);
        CategoryService serviceCategory = categoryOptional.get();
        boolean exists = serviceCategoryRepository.existsByName(categoryServiceDto.getName());
        if (exists)
            return new ApiResponse("bunday category mavjud",false,null);
        serviceCategory.setName(categoryServiceDto.getName());
        serviceCategoryRepository.save(serviceCategory);
        return new ApiResponse("Category o'zgartirildi",true,serviceCategory);
    }

}
