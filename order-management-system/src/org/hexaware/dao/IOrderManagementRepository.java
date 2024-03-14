package org.hexaware.dao;

import org.hexaware.entity.model.Product;
import org.hexaware.entity.model.User;
import org.hexaware.exception.OrderNotFoundException;
import org.hexaware.exception.UserNotFoundException;

import java.util.List;

public interface IOrderManagementRepository {
    void createOrder(User user, List<Product> products) throws UserNotFoundException;
    void cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException;
    void createProduct(Product product);
    void createUser(User user);
    List<Product> getAllProducts();
    List<Product> getOrderByUser(User user);
}
