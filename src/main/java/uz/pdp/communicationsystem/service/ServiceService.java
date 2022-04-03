package uz.pdp.communicationsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import uz.pdp.communicationsystem.entity.EntertainingService;
import uz.pdp.communicationsystem.entity.CategoryService;
import uz.pdp.communicationsystem.entity.SimCard;
import uz.pdp.communicationsystem.entity.enums.ActionType;
import uz.pdp.communicationsystem.payload.ApiResponse;
import uz.pdp.communicationsystem.payload.DetailDto;
import uz.pdp.communicationsystem.payload.ServiceDto;
import uz.pdp.communicationsystem.payload.SimcardEntertainingDto;
import uz.pdp.communicationsystem.repository.ServiceCategoryRepository;
import uz.pdp.communicationsystem.repository.ServiceRepository;
import uz.pdp.communicationsystem.repository.SimCardRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ServiceService {
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    ServiceCategoryRepository serviceCategoryRepository;
    @Autowired
    DetailsService detailsService;
    @Autowired
    SimCardService simCardservice;
    @Autowired
    SimCardRepository simCardRepository;
    public List<EntertainingService> getServiceList(){
        return serviceRepository.findAll();
    }
    public ApiResponse getServiceById(UUID id){
        Optional<EntertainingService> serviceOptional = serviceRepository.findById(id);
        if (!serviceOptional.isPresent())
            return new ApiResponse("bunday id dagi xizmat topilmadi",false,null);
        EntertainingService entertainingService = serviceOptional.get();
        return new ApiResponse("So'rov muvaffaqiyatli yakunladni",false,entertainingService);
    }
    public ApiResponse addEntertainingService(ServiceDto serviceDto){
        boolean exists = serviceRepository.existsByName(serviceDto.getName());
        if (exists)
            return new ApiResponse("bunday nomli xizmat mavjud",false,null);
        EntertainingService entertainingService = new EntertainingService();
        entertainingService.setName(serviceDto.getName());
        entertainingService.setExpiredDate(serviceDto.getExpiredDate());
        entertainingService.setPrice(serviceDto.getPrice());
        Optional<CategoryService> categoryOptional = serviceCategoryRepository.findById(serviceDto.getServiceCategoryID());
        if (!categoryOptional.isPresent()){
            CategoryService serviceCategory = new CategoryService();
            serviceCategory.setName(serviceDto.getCategoryName());
            serviceCategoryRepository.save(serviceCategory);
        }
        CategoryService serviceCategory = categoryOptional.get();
        entertainingService.setCategoryService(serviceCategory);
        serviceRepository.save(entertainingService);
        return new ApiResponse("qo'shildi",true,entertainingService);
    }
    //    @Secured({ "ROLE_VIEWER", "ROLE_EDITOR" })
    public ApiResponse editEntertainingService(UUID id,ServiceDto serviceDto){
        Optional<EntertainingService> serviceOptional = serviceRepository.findById(id);
        if (!serviceOptional.isPresent())
            return new ApiResponse("Bunday id dagi xizmat topilmadi",false,null);
        EntertainingService entertainingService = serviceOptional.get();
        boolean exist = serviceRepository.existsByName(serviceDto.getName());
        if (exist)
            return new ApiResponse("bunday xizmat turi mavjud",false,null);
        entertainingService.setName(serviceDto.getName());
        entertainingService.setPrice(serviceDto.getPrice());
        entertainingService.setExpiredDate(serviceDto.getExpiredDate());
        Optional<CategoryService> categoryOptional = serviceCategoryRepository.findById(serviceDto.getServiceCategoryID());
        if (!categoryOptional.isPresent()){
            CategoryService serviceCategory = new CategoryService();
            serviceCategory.setName(serviceDto.getCategoryName());
            serviceCategoryRepository.save(serviceCategory);
        }
        CategoryService serviceCategory = categoryOptional.get();
        entertainingService.setCategoryService(serviceCategory);
        serviceRepository.save(entertainingService);
        return new ApiResponse("Xizmat muvaffaqiyatli o'zgartirildi",false,entertainingService);
    }
    public ApiResponse addEntertainingServiceForClient(@RequestBody SimcardEntertainingDto simcardEntertainingDto){
        SimCard principal = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<EntertainingService> serviceOptional = serviceRepository.findByName(simcardEntertainingDto.getEntertainingName());
        if (!serviceOptional.isPresent())
            return new ApiResponse("Bunday xizmat mavjud emas",false,null);
        EntertainingService entertainingService = serviceOptional.get();
        if (entertainingService.getPrice()>principal.getBalance()){
            return new ApiResponse("mablag yetarli emas",false,null);
        }
        Set<EntertainingService> entertainingServices = principal.getEntertainingServices();
        entertainingServices.add(entertainingService);
        principal.setEntertainingServices(entertainingServices);
        simCardRepository.save(principal);
        detailsService.add(new DetailDto(ActionType.SERVICE,principal, (float) entertainingService.getPrice()));
        entertainingService.setCount(entertainingService.getCount()+1);
        serviceRepository.save(entertainingService);
        return new ApiResponse("Ulandi",true,null);
    }
    public ApiResponse deleteEntertainingForClient(@RequestBody SimcardEntertainingDto simcardEntertainingDto){
        SimCard principal = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<EntertainingService> serviceOptional = serviceRepository.findByName(simcardEntertainingDto.getEntertainingName());
        if (!serviceOptional.isPresent())
            return new ApiResponse("Bunday nomli xizmat turi mavjud emas",false,null);
        EntertainingService entertainingService = serviceOptional.get();
        principal.getEntertainingServices().remove(entertainingService);
        entertainingService.setCount(entertainingService.getCount()-1);
        simCardRepository.save(principal);
        serviceRepository.save(entertainingService);
        return new ApiResponse("deleted",true,null);

    }
    public ApiResponse getFamousServiceList(){
        List<EntertainingService> byCountOrderByCount = serviceRepository.findAllByOrderByCountAsc();
        return new ApiResponse("success",true,byCountOrderByCount);
    }
}
