package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

public interface NotificationService {
    void sendUpdateOrderNotification(Order order, User whoSend);
}