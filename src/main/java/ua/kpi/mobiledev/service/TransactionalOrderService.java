package ua.kpi.mobiledev.service;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusManager;
import ua.kpi.mobiledev.domain.priceCalculationManagement.PriceCalculationManager;
import ua.kpi.mobiledev.exception.ForbiddenOperationException;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static ua.kpi.mobiledev.domain.User.UserType.TAXI_DRIVER;
import static ua.kpi.mobiledev.exception.ErrorCode.DRIVER_CANNOT_PERFORM_OPERATION;
import static ua.kpi.mobiledev.exception.ErrorCode.ORDER_NOT_FOUND_WITH_ID;
import static ua.kpi.mobiledev.exception.ErrorCode.USER_IS_NOT_ORDER_OWNER;

@Transactional(readOnly = true)
public class TransactionalOrderService implements OrderService {

    private OrderRepository<Order, Long> orderRepository;
    private UserService userService;
    private OrderStatusManager orderStatusManager;
    private PriceCalculationManager priceCalculationManager;

    public TransactionalOrderService(OrderRepository<Order, Long> orderRepository, UserService userService,
                                     OrderStatusManager orderStatusManager, PriceCalculationManager priceCalculationManager) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.orderStatusManager = orderStatusManager;
        this.priceCalculationManager = priceCalculationManager;
    }

    @Override
    @Transactional
    public Order addOrder(Order order, Integer userId) {
        order.setCustomer(checkIfCustomer(findUser(userId)));
        return orderRepository.save(priceCalculationManager.calculateOrderPrice(order));
    }

    private User checkIfCustomer(User user) {
        if (user.getUserType() == TAXI_DRIVER) {
            throw new ForbiddenOperationException(DRIVER_CANNOT_PERFORM_OPERATION);
        }
        return user;
    }

    @Override
    public Double calculatePrice(Order notCompletedOrder) {
        return isNull(notCompletedOrder)
                ? 0.0
                : priceCalculationManager.calculateOrderPrice(notCompletedOrder).getPrice();
    }

    @Override
    @Transactional
    public Order changeOrderStatus(Long orderId, Integer userId, Order.OrderStatus orderStatus) {
        Order updatedOrder = orderStatusManager.changeOrderStatus(getOrder(orderId), findUser(userId), orderStatus);
        updatedOrder = orderRepository.save(updatedOrder);
        return updatedOrder;
    }

    @Override
    public List<Order> getOrderList(Order.OrderStatus orderStatus) {
        return isNull(orderStatus) ? mapToList(orderRepository.findAll()) : orderRepository.getAllByOrderStatus(orderStatus);
    }

    private List<Order> mapToList(Iterable<Order> all) {
        List<Order> orders = new ArrayList<>();
        all.forEach(orders::add);
        return orders;
    }

    @Override
    public Order getOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        if (isNull(order)) {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND_WITH_ID, orderId);
        }
        Hibernate.initialize(order.getTaxiDriver());
        return order;
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId, Integer userId) {
        checkIfCustomer(userService.getById(userId));
        Order order = getOrder(orderId);
        checkIfOrderOwner(order, userId);

        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Long orderId, Integer userId, OrderDto orderDto) {
        Order order = getOrder(orderId);

        checkIfOrderOwner(order, userId);

        order.setStartTime(orderDto.getStartTime());
        OrderPriceDto orderPriceDto = orderDto.getOrderPrice();
        order.setPrice(calculatePrice(null));

        return orderRepository.save(order);
    }

    private void checkIfOrderOwner(Order order, Integer userId) {
        Integer customerId = order.getCustomer().getId();
        if (!customerId.equals(userId)) {
            throw new ForbiddenOperationException(USER_IS_NOT_ORDER_OWNER, userId, order.getOrderId());
        }
    }

    private User findUser(Integer userId) {
        return userService.getById(userId);
    }

}
