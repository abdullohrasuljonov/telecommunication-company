package uz.pdp.communicationsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.pdp.communicationsystem.entity.Payment;
import uz.pdp.communicationsystem.entity.SimCard;
import uz.pdp.communicationsystem.entity.enums.PaymentType;
import uz.pdp.communicationsystem.payload.ApiResponse;
import uz.pdp.communicationsystem.payload.PaymentDto;
import uz.pdp.communicationsystem.repository.PaymentRepository;
import uz.pdp.communicationsystem.repository.RoleRepository;
import uz.pdp.communicationsystem.repository.SimCardRepository;

import java.util.List;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    SimCardRepository simCardRepository;
    @Autowired
    RoleRepository roleRepository;

    public ApiResponse add(PaymentDto dto){
        Payment payment = new Payment();
        payment.setPayerId(dto.getPayerId());
        payment.setPayerName(dto.getPayerName());
        payment.setAmount(dto.getAmount());
        SimCard simCard = simCardRepository.findBySimCardNumber(dto.getSimCardNumber()).get();
        simCard.setBalance(simCard.getBalance()+ dto.getAmount());
        payment.setSimCard(simCard);
        if (dto.getPayType().equalsIgnoreCase("naqd")) payment.setPaymentType(PaymentType.NAQD);
        if (dto.getPayType().equalsIgnoreCase("humo")) payment.setPaymentType(PaymentType.HUMO);
        if (dto.getPayType().equalsIgnoreCase("click")) payment.setPaymentType(PaymentType.CLICK);
        if (dto.getPayType().equalsIgnoreCase("payme")) payment.setPaymentType(PaymentType.PAYME);
        paymentRepository.save(payment);
        return new ApiResponse("Payment amalga oshirildi", true);

    }
    //hodimlar tomonidan barcha tolovlar tarihini korish
    public ApiResponse getAll(){
        List<Payment> paymentList = paymentRepository.findAll();
        return new ApiResponse("Barcha tolovlar tarihi", true, paymentList);
    }

    //hodimlar tomonidan faqat bitta mijoz sim kartasining tolovlar tarihini korish
    public ApiResponse getOneClientsPaymentHistory(String simCardNumber){
        List<Payment> allBySimCardNumber = paymentRepository.findAllBySimCardNumber(simCardNumber);
        SimCard simCard = simCardRepository.findBySimCardNumber(simCardNumber).get();
        return new ApiResponse(simCard.getCode()+simCard.getNumber()+
                " raqamli abonentning tolovlar tarihi", true, allBySimCardNumber);
    }
    //mijoz tomonidan ozining barcha tolovlar tarihini korish
    public ApiResponse getPaymentHistoryByClient(){
        SimCard simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Payment> allBySimCardNumber = paymentRepository.findAllBySimCardNumber(simCard.getSimCardNumber());
        return new ApiResponse(simCard.getCode()+simCard.getNumber()+" " +
                "raqimingizning tolovlar tarihi", true, allBySimCardNumber);
    }
}
