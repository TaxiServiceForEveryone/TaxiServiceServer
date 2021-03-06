package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.web.dto.AddReqSimpleDto;

import java.util.List;

@Component("additionalRequirementsConverter")
public class AdditionalRequirementsConverter implements CustomConverter<List<AddReqSimpleDto>, Order> {

    @Override
    public void convert(List<AddReqSimpleDto> addRequirements, Order order) {
        for (AddReqSimpleDto req : addRequirements) {
            int reqValue = req.getReqValueId();
            switch (req.getReqId()) {
                case 1: {
                    order.setCarType(Car.CarType.values()[reqValue]);
                    break;
                }
                case 2: {
                    order.setPaymentMethod(Order.PaymentMethod.values()[reqValue]);
                    break;
                }
                case 3: {
                    order.setWithPet(reqValue == 1);
                    break;
                }
                case 4: {
                    order.setWithLuggage(reqValue == 1);
                    break;
                }
                case 5: {
                    order.setExtraPrice((double) reqValue);
                    break;
                }
                case 6: {
                    order.setDriveMyCar(reqValue == 1);
                    break;
                }
                case 7: {
                    order.setPassengerCount(reqValue);
                }
            }
        }
    }

    @Override
    public void reverseConvert(Order order, List<AddReqSimpleDto> addRequirements) {
        addRequirements.add(new AddReqSimpleDto(1, order.getCarType().ordinal()));
        addRequirements.add(new AddReqSimpleDto(2, order.getPaymentMethod().ordinal()));
        addRequirements.add(new AddReqSimpleDto(3, order.getWithPet() ? 1 : 0));
        addRequirements.add(new AddReqSimpleDto(4, order.getWithLuggage() ? 1 : 0));
        addRequirements.add(new AddReqSimpleDto(5, order.getExtraPrice().intValue()));
        addRequirements.add(new AddReqSimpleDto(6, order.getDriveMyCar() ? 1 : 0));
        addRequirements.add(new AddReqSimpleDto(7, order.getPassengerCount()));
    }
}
