package uz.pdp.communicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.communicationsystem.entity.Client;
import uz.pdp.communicationsystem.entity.Employee;
import uz.pdp.communicationsystem.entity.SimCard;
import uz.pdp.communicationsystem.payload.ApiResponse;
import uz.pdp.communicationsystem.payload.LoginDto;
import uz.pdp.communicationsystem.repository.ClientRepository;
import uz.pdp.communicationsystem.repository.EmployeeRepository;
import uz.pdp.communicationsystem.repository.RoleRepository;
import uz.pdp.communicationsystem.repository.SimCardRepository;
import uz.pdp.communicationsystem.security.JwtProvider;

import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    SimCardRepository simCardRepository;


    public ApiResponse loginForStaff(LoginDto dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (dto.getUsername(), dto.getPassword()));
            Employee employee = (Employee) authentication.getPrincipal();
            String token = jwtProvider.generateToken(dto.getUsername(), employee.getRoles());
            return new ApiResponse("Token", true, token);
        } catch (BadCredentialsException badCredentialsException) {
            return new ApiResponse("Parol yoki username xato", false);
        }

    }

    public ApiResponse loginForClient(LoginDto dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (dto.getUsername(), dto.getPassword()));
            Client client = (Client) authentication.getPrincipal();
            String token = jwtProvider.generateToken(dto.getUsername(), client.getRoles());
            return new ApiResponse("Token", true, token);
        } catch (BadCredentialsException badCredentialsException) {
            return new ApiResponse("Parol yoki username xato", false);
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> optionalUser = employeeRepository.findByUsername(username);
//        Optional<Client> optionalClient = clientRepository.findByPhoneNumber(username);
        Optional<SimCard> optionalSimCard = simCardRepository.findByCodeAndNumber(username.substring(0, 2), username.substring(3));
        if (optionalUser.isPresent()) return optionalUser.get();
        if (optionalSimCard.isPresent()) return optionalSimCard.get();
        throw new UsernameNotFoundException(username + "topilmadi");
    }

    public UserDetails loadClientByUsernameFromSimCard(String simCardNumber) {
        SimCard simCard = simCardRepository.findBySimCardNumber(simCardNumber).orElseThrow(() -> new UsernameNotFoundException(simCardNumber));
        return simCard;
    }

}
