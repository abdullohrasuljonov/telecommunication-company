package uz.pdp.communicationsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.pdp.communicationsystem.entity.Filial;
import uz.pdp.communicationsystem.entity.Role;
import uz.pdp.communicationsystem.entity.Employee;
import uz.pdp.communicationsystem.payload.ApiResponse;
import uz.pdp.communicationsystem.payload.StaffDto;
import uz.pdp.communicationsystem.repository.FilialRepository;
import uz.pdp.communicationsystem.repository.RoleRepository;
import uz.pdp.communicationsystem.repository.EmployeeRepository;
import uz.pdp.communicationsystem.security.JwtProvider;

import java.util.*;

@Service
public class StaffService {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    FilialRepository filialRepository;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    PasswordEncoder passwordEncoder;

    public ApiResponse addStaff(StaffDto staffDto){
        Optional<Role> optionalRole = roleRepository.findById(staffDto.getRoleId());
        if (!optionalRole.isPresent()){
            return new ApiResponse("Such Role doesnt exist",false);
        }
        Optional<Filial> optionalFilial = filialRepository.findById(staffDto.getFilialId());
        if (!optionalFilial.isPresent()){
            return new ApiResponse("Such Filial doesnt exist",false);
        }
        Employee staff=new Employee();
        staff.setFullName(staffDto.getFullName());
        staff.setUserName(staffDto.getUserName());
        Set<Role> roles=new HashSet<>();
        roles.add(optionalRole.get());
        staff.setRoles(roles);
        staff.setFilial(optionalFilial.get());
        staff.setPosition(staffDto.getPosition());
        staff.setPassword(passwordEncoder.encode(staffDto.getPassword()));
        employeeRepository.save(staff);
        return new ApiResponse("Staff added!",true);
    }

    public List<Employee> getStaffList(){
        List<Employee> staffList = employeeRepository.findAll();
        return staffList;
    }

    public ApiResponse getStaff(){
        Employee staff = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Employee> optionalStaff = employeeRepository.findById(staff.getId());
        if (!optionalStaff.isPresent()){
            return new ApiResponse("Such staff doesnt exist",false);
        }
        return new ApiResponse("=>",true,staff);
    }

    public ApiResponse editStaff(String username,StaffDto staffDto){
        Optional<Employee> optionalStaff = employeeRepository.findByUsername(username);
        if (!optionalStaff.isPresent()){
            return new ApiResponse("Such Staff doesnt exist",false);
        }
        Optional<Filial> optionalFilial = filialRepository.findById(staffDto.getFilialId());
        if (!optionalFilial.isPresent()){
            return new ApiResponse("Such Filial doesnt exist",false);
        }
        Employee staff = optionalStaff.get();
        staff.setFullName(staffDto.getFullName());
        boolean existsByUserName = employeeRepository.existsByUserName(staff.getUsername());
        if (existsByUserName){
            return new ApiResponse("Such staff already exist",false);
        }
        staff.setUserName(staffDto.getUserName());
        staff.setFilial(optionalFilial.get());
        staff.setPosition(staffDto.getPosition());
        staff.setPassword(passwordEncoder.encode(staffDto.getPassword()));
        employeeRepository.save(staff);
        return new ApiResponse("Staff edited!",true);
    }


    public ApiResponse deleteStaff(String username){
        Optional<Employee> optionalStaff = employeeRepository.findByUsername(username);
        if (!optionalStaff.isPresent()){
            return new ApiResponse("Such staff doesnt exist",false);
        }
        employeeRepository.delete(optionalStaff.get());
        return new ApiResponse("Staff deleted",true);
    }
}
