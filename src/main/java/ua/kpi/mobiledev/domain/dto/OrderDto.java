package ua.kpi.mobiledev.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeDeserializer;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeSerializer;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    public static OrderDto from(Order order) {
        if (Objects.isNull(order)) {
            return new OrderDto();
        }
        OrderDto orderDto = new OrderDto();
        orderDto.customerId = order.getCustomer().getId();
        orderDto.startTime = order.getStartTime();
        orderDto.startPoint = order.getStartPoint();
        orderDto.endPoint = order.getEndPoint();
        orderDto.price = order.getPrice();
        return orderDto;
    }

    @NotNull
    @Min(0)
    private Integer customerId;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Size(min = 1)
    private String startPoint;

    @NotNull
    @Size(min = 1)
    private String endPoint;

    @NotNull
    @Valid
    private OrderPriceDto orderPrice;

    private Double price;
}
