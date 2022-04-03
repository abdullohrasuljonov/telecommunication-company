package uz.pdp.communicationsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.communicationsystem.entity.Role;
import uz.pdp.communicationsystem.entity.Employee;
import uz.pdp.communicationsystem.entity.UssdCode;
import uz.pdp.communicationsystem.entity.enums.RoleType;
import uz.pdp.communicationsystem.payload.ApiResponse;
import uz.pdp.communicationsystem.repository.RoleRepository;
import uz.pdp.communicationsystem.repository.UssdCodeRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UssdCodeService {
    @Autowired
    UssdCodeRepository ussdCodeRepository;
    @Autowired
    RoleRepository roleRepository;

    public ApiResponse add(UssdCode ussdCode) {

        Employee hodim = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Role> userRoles = hodim.getRoles();
        Role manager = roleRepository.findByRoleType(RoleType.ROLE_MANAGER);
        Role staff = roleRepository.findByRoleType(RoleType.ROLE_STAFF);
        if ((hodim.getRoles().contains(staff)) ||
                (hodim.getRoles().contains(manager) && hodim.getPosition().equalsIgnoreCase("USSD_Manager")))
            return new ApiResponse("Siz yangi USSD sorovlarini qosholmaysiz", false);
        UssdCode ussdCode1 = new UssdCode();
        ussdCode1.setCode(ussdCode.getCode());
        ussdCode1.setDescription(ussdCode.getDescription());
        ussdCodeRepository.save(ussdCode1);
        return new ApiResponse("Yangi USSD sorov qoshildi", true);

    }
    public ApiResponse delete(Integer id){
        Employee hodim = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Role> userRoles = hodim.getRoles();
        Role manager = roleRepository.findByRoleType(RoleType.ROLE_MANAGER);
        Role staff = roleRepository.findByRoleType(RoleType.ROLE_STAFF);
        if ((hodim.getRoles().contains(staff)) ||
                (hodim.getRoles().contains(manager) && hodim.getPosition().equalsIgnoreCase("USSD_Manager")))
            return new ApiResponse("Siz  USSD sorovlarini ochirolmaysiz", false);
        ussdCodeRepository.deleteById(id);
        return new ApiResponse("USSD kodi ochirildi", true);
    }

    public ApiResponse edit(Integer id, UssdCode ussdCode){
        Employee hodim = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Role> userRoles = hodim.getRoles();
        Role manager = roleRepository.findByRoleType(RoleType.ROLE_MANAGER);
        Role staff = roleRepository.findByRoleType(RoleType.ROLE_STAFF);
        if ((hodim.getRoles().contains(staff)) ||
                (hodim.getRoles().contains(manager) && hodim.getPosition().equalsIgnoreCase("USSD_Manager")))
            return new ApiResponse("Siz  USSD sorovlarini tahrir qilolmaysiz", false);
        Optional<UssdCode> optionalUssdCode = ussdCodeRepository.findById(id);
        if (!optionalUssdCode.isPresent()) return new ApiResponse("Bunday IDli USSD kod mavjud emas", false);
        UssdCode ussdCode1 = optionalUssdCode.get();
        ussdCode1.setCode(ussdCode.getCode());
        ussdCode1.setDescription(ussdCode.getDescription());
        ussdCodeRepository.save(ussdCode1);
        return new ApiResponse("USSD kodi tahrirlandi", true);
    }

    public ApiResponse getAll(){
        List<UssdCode> all = ussdCodeRepository.findAll();
        return new ApiResponse("Barcha USSD kodilarii", true, all);
    }

    public ApiResponse getOne(Integer id){
        Optional<UssdCode> optionalUssdCode = ussdCodeRepository.findById(id);
        return optionalUssdCode.map(ussdCode -> new ApiResponse("Siz qidirgan USSD kodi", true, ussdCode)).orElseGet(() -> new ApiResponse("Bunday IDli USSD kod mavjud emas", false));
    }
}
