package uz.pdp.communicationsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.pdp.communicationsystem.component.NumberGenerator;
import uz.pdp.communicationsystem.entity.*;
import uz.pdp.communicationsystem.entity.enums.ActionType;
import uz.pdp.communicationsystem.entity.enums.ClientType;
import uz.pdp.communicationsystem.entity.enums.RoleType;
import uz.pdp.communicationsystem.payload.*;
import uz.pdp.communicationsystem.repository.ClientRepository;
import uz.pdp.communicationsystem.repository.RoleRepository;
import uz.pdp.communicationsystem.repository.SimCardRepository;
import uz.pdp.communicationsystem.repository.TariffRepository;

import java.sql.Timestamp;
import java.util.*;

@Service
public class ClientService {
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    SimCardRepository simCardRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    NumberGenerator numberGenerator;
    @Autowired
    TariffRepository tariffRepository;
    @Autowired
    DetailsService detailsService;


    public ApiResponse buySimCard(ClientDto clientDto) {
        boolean byPassportNumber = clientRepository.existsByPassportNumber(clientDto.getPassportNumber());
        if (byPassportNumber) {
            Optional<Client> optionalClient = clientRepository.findByPassportNumber(clientDto.getPassportNumber());
            Client client = optionalClient.get();


            List<BuyingSimCardDto> buyingSimCardDtos = clientDto.getBuyingSimCardDtos();

            //simkartalarni yigib olish uchun list ochildi
            List<SimCard> simCardList = new ArrayList<>();

            for (BuyingSimCardDto buyingSimCardDto : buyingSimCardDtos) {
                Optional<Tarif> optionalTariff = tariffRepository.findById(buyingSimCardDto.getTariffId());
                if (!optionalTariff.isPresent()) return new ApiResponse("Tariff not found", false);
                Tarif tariff = optionalTariff.get();

                Optional<SimCard> optionalSimCard = simCardRepository.findByCodeAndNumber(buyingSimCardDto.getCode(), buyingSimCardDto.getNumber());
                if (!optionalSimCard.isPresent()) return new ApiResponse("SimCard not found", false);
                SimCard simCard = optionalSimCard.get();


                //simcarta active bo'lmasligi kerak sotib olish uchin
                if (simCard.isActive()) return new ApiResponse("SimCard already bought", false);

                simCard.setActive(true);
                simCard.setTarif(tariff);
                simCard.setClient(client);
                simCard.setBalance(buyingSimCardDto.getSum());
                if (buyingSimCardDto.getSum() >= tariff.getPrice()) {
                    simCard.setBalance(simCard.getBalance() - tariff.getPrice());
                    simCard.setAmountMb(tariff.getMb());
                    simCard.setAmountMinute(tariff.getMin());
                    simCard.setAmountSms(tariff.getSms());
                    simCard.setTariffIsActive(true);
                } else {
                    simCard.setAmountMb(0);
                    simCard.setAmountMinute(0);
                    simCard.setAmountSms(0);
                    simCard.setTariffIsActive(false);
                }
                simCardList.add(simCard);
            }
            client.setSimCardList(simCardList);
            clientRepository.save(client);

            return new ApiResponse("SimCard rasmiylashtirildi", true);

        } else {

            //mijoz parametrlar
            Client client = new Client();

            client.setPassportNumber(clientDto.getPassportNumber());
            if (clientDto.getClientTypeOrdinal() == 1) {
                client.setClientType(ClientType.USER);
            } else if (clientDto.getClientTypeOrdinal() == 2) {
                client.setClientType(ClientType.COMPANY);
            } else {
                return new ApiResponse("Client type noto'g'ri berildi", false);
            }
            client.setFullName(clientDto.getFullName());
            Role byRoleName = roleRepository.findByRoleType(RoleType.ROLE_CLIENT);
            client.setRoles(Collections.singleton(byRoleName));

            //Simkardlarni yigib olish uchun method
            List<SimCard> simCardList = new ArrayList<>();

            for (BuyingSimCardDto buyingSimCardDto : clientDto.getBuyingSimCardDtos()) {
                Optional<Tarif> optionalTariff = tariffRepository.findById(buyingSimCardDto.getTariffId());
                if (!optionalTariff.isPresent()) return new ApiResponse("Tariff not found", false);
                Tarif tariff = optionalTariff.get();


                Optional<SimCard> optionalSimCard = simCardRepository.findByCodeAndNumber(buyingSimCardDto.getCode(), buyingSimCardDto.getNumber());
                if (!optionalSimCard.isPresent()) return new ApiResponse("SimCard not found", false);
                SimCard simCard = optionalSimCard.get();

                //sim karta parametrlarini kirgizish
                simCard.setActive(true);
                simCard.setTarif(tariff);
                simCard.setBalance(buyingSimCardDto.getSum());
                simCard.setClient(client);
                //agar simkartaga tariff uchun yetarli mablag' yuq bo'lsa bonuslar berilmaydi
                if (buyingSimCardDto.getSum() >= tariff.getPrice()) {
                    simCard.setBalance(simCard.getBalance() - tariff.getPrice());
                    simCard.setAmountMb(tariff.getMb());
                    simCard.setAmountMinute(tariff.getMin());
                    simCard.setAmountSms(tariff.getSms());
                    simCard.setTariffIsActive(true);

                } else {
                    simCard.setAmountMb(0);
                    simCard.setAmountMinute(0);
                    simCard.setAmountSms(0);
                    simCard.setTariffIsActive(false);
                }

                simCardList.add(simCard);
            }
            client.setSimCardList(simCardList);
            clientRepository.save(client);
            return new ApiResponse("SimCard successfully authorized", true);


        }
    }

    public ApiResponse requestCredit(DebtDto debtDto) {
        SimCard simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (simCard.isCredit() || simCard.getBalance() > 5000)
            return new ApiResponse("Sizga qarz berilmaydi", false);
        if (debtDto.getAmount() > 25000) return new ApiResponse("Siz faqat 25000 gacha qarz ola olasiz", false);

        simCard.setCredit(true);
        simCard.setBalance(simCard.getBalance() + debtDto.getAmount());


        DebtSimCard debtSimCard = new DebtSimCard();
        debtSimCard.setSimCard(simCard);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_YEAR, 3);
        Date expireTime = calendar.getTime();
        debtSimCard.setExpireDate(new Timestamp(expireTime.getTime()));
        debtSimCard.setAmount(debtDto.getAmount());

        simCard.setDebtSimCards(Collections.singletonList(debtSimCard));

        simCardRepository.save(simCard);
        return new ApiResponse("Sizga qarz berildi", true);


    }

    public ApiResponse callSmb(CallDto callDto) {

        Optional<SimCard> byCodeAndNumber = simCardRepository.findByCodeAndNumber(callDto.getCode(), callDto.getNumber());
        if (!byCodeAndNumber.isPresent()) return new ApiResponse("Siz tergan raqam mavjud emas", false);

        SimCard simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tarif tariff = simCard.getTarif();
        if (simCard.isActive() && !simCard.isCredit() && simCard.getBalance() > tariff.getMinCost()) {
            int minCost = tariff.getMinCost();
            float outcome = 0;

            for (int i = 0; i < callDto.getSeconds() / 60; i++) {
                if (simCard.getAmountMinute() > 1 && simCard.getBalance() > minCost) {
                    simCard.setAmountMinute(simCard.getAmountMinute() - 1);
                } else if (simCard.getAmountMinute() == 0 && simCard.getBalance() > 0) {
                    simCard.setBalance(simCard.getBalance() - minCost);
                    outcome += minCost;
                } else if (simCard.getBalance() <= 0 && simCard.getAmountMinute() == 0) {
                    simCard.setCredit(true);
                    return new ApiResponse("Qo'ng'iroq yakunlandi balansda mablag' qolmadi", false);
                }
            }
            detailsService.add(new DetailDto(ActionType.MIN, simCard, outcome));
            simCardRepository.save(simCard);
            return new ApiResponse("Qo'n'giroq yakunlandi", true);
        }
        return new ApiResponse("Sizda yetarli mablag' mavjud emas", false);


    }

    public ApiResponse sendSms(SmsDto smsDto) {

        Optional<SimCard> byCodeAndNumber = simCardRepository.findByCodeAndNumber(smsDto.getCode(), smsDto.getNumber());
        if (!byCodeAndNumber.isPresent()) return new ApiResponse("Siz kiritgan raqam mavjud emas", false);

        SimCard simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tarif tariff = simCard.getTarif();


        float outcome = 0;

        if (simCard.getAmountSms() > 1 && !simCard.isCredit() && simCard.isActive()) {
            simCard.setAmountMinute(simCard.getAmountMinute() - 1);
        } else if (simCard.getAmountSms() == 0 && simCard.getBalance() >= tariff.getSmsCost()) {
            simCard.setBalance(simCard.getBalance() - tariff.getSmsCost());
            outcome = outcome + tariff.getSmsCost();
        } else if (simCard.getBalance() <= 0 && simCard.getAmountSms() == 0) {
            return new ApiResponse("Sizda yetarli mablag' mavjud emas", false);
        }
        detailsService.add(new DetailDto(ActionType.SMS, simCard, outcome));
        simCardRepository.save(simCard);
        return new ApiResponse("Success", true);


    }

}
