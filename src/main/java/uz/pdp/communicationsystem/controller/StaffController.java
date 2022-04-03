package uz.pdp.communicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.communicationsystem.entity.Employee;
import uz.pdp.communicationsystem.payload.ApiResponse;
import uz.pdp.communicationsystem.payload.StaffDto;
import uz.pdp.communicationsystem.service.StaffService;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    @Autowired
    StaffService staffService;

    // Staff qo'shish
    @PreAuthorize(value = "hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER')")
    @PostMapping
    public HttpEntity<?> addStaff(StaffDto staffDto){
        ApiResponse apiResponse = staffService.addStaff(staffDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    // Staff tahrirlash
    @PreAuthorize(value = "hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER','ROLE_STAFF')")
    @PutMapping("/{username}")
    public HttpEntity<?> editStaff(@PathVariable String username, StaffDto staffDto){
        ApiResponse apiResponse = staffService.editStaff(username, staffDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    // Token bo'yicha staff qaytaradi
    @PreAuthorize(value = "hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER','ROLE_STAFF')")
    @GetMapping("/one")
    public HttpEntity<?> getStaff(){
        ApiResponse apiResponse = staffService.getStaff();
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    // Barcha staff lar ro'yxati
    @PreAuthorize(value = "hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER')")
    @GetMapping
    public HttpEntity<?> getStaffList(){
        List<Employee> staffList = staffService.getStaffList();
        return ResponseEntity.ok(staffList);
    }


    // Username bo'yicha staff o'chiradi
    @PreAuthorize(value = "hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER')")
    @DeleteMapping("/{username}")
    public HttpEntity<?> deleteStaff(@PathVariable String username){
        ApiResponse apiResponse = staffService.deleteStaff(username);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }
}
