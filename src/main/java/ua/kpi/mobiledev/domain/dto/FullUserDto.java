package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.MobileNumber;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullUserDto {

    public static FullUserDto toUserDto(User user) {
        FullUserDto fullUserDto = new FullUserDto();
        fullUserDto.setUserId(user.getId());
        fullUserDto.setName(user.getName());
        fullUserDto.setEmail(user.getEmail());
        fullUserDto.setUserType(user.getUserType());
        fullUserDto.setMobileNumbers(user.getMobileNumbers().stream().map(MobileNumberDto::from).collect(Collectors.toList()));
        if (isTaxiDriver(user.getUserType())) {
            fullUserDto.setCar(CarDto.fromCar(((TaxiDriver) user).getCar()));
        }
        return fullUserDto;
    }

    public static User toUser(FullUserDto fullUserDto) {
        Set<MobileNumber> mobileNumbers = fullUserDto.getMobileNumbers().stream().map(MobileNumberDto::from).collect(Collectors.toSet());
        return isTaxiDriver(fullUserDto.getUserType()) ?
                new TaxiDriver(fullUserDto.userId, fullUserDto.name, fullUserDto.email, mobileNumbers, CarDto.toCar(fullUserDto.getCar())) :
                new User(fullUserDto.userId, fullUserDto.name, fullUserDto.email, fullUserDto.userType, mobileNumbers);
    }

    private static boolean isTaxiDriver(User.UserType userType) {
        return userType == User.UserType.TAXI_DRIVER;
    }

    @NotNull(message = "userId.required")
    @Min(value = 1, message = "userId.negativeOrZero")
    private Integer userId;

    @NotNull(message = "name.required")
    @Size(min = 1, max = 45, message = "name.invalidSize")
    private String name;

    @NotNull(message = "username.required")
    @Size(min = 1, max = 45, message = "username.invalidSize")
    private String email;

    @NotNull(message = "userType.required")
    private User.UserType userType;

    @NotNull(message = "mobileNumbers.required")
    @Valid
    private List<MobileNumberDto> mobileNumbers;

    @Valid
    private CarDto car;
}