package org.hexaware.dao;

import org.hexaware.entity.model.Clothing;
import org.hexaware.entity.model.Electronics;
import org.hexaware.entity.model.Product;
import org.hexaware.entity.model.User;
import org.hexaware.exception.OrderNotFoundException;
import org.hexaware.exception.UserNotFoundException;
import org.hexaware.util.DBConnUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessor implements IOrderManagementRepository {
    private static final String CREATE_USER_SQL = "INSERT INTO User (user_id, username, password, role) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ORDER_SQL = "INSERT INTO Orders (user_id, product_id) VALUES (?, ?)";
    private static final String CANCEL_ORDER_SQL = "DELETE FROM Orders WHERE user_id = ? AND order_id = ?";
    private static final String CREATE_ELECTRONICS_PRODUCT_SQL = "INSERT INTO Product (product_id, productName, description, price, quantityInStock, type, brand, warrantyPeriod) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_CLOTHING_PRODUCT_SQL = "INSERT INTO Product (product_id, productName, description, price, quantityInStock, type, size, color) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_ALL_PRODUCTS_SQL = "SELECT * FROM Product";
    private static final String GET_PRODUCT_BY_PRODUCT_ID_SQL = "SELECT * FROM Product WHERE product_id = ?";
    private static final String GET_ORDERS_BY_USER_SQL = "SELECT * FROM Orders WHERE user_id = ?";
    private static final String CHECK_USER_EXISTS = "SELECT COUNT(*) AS count FROM User WHERE user_id = ?";

    @Override
    public void createOrder(User user, List<Product> products) throws UserNotFoundException {
        if (!checkUserExists(user.getUserId())) {
            throw new UserNotFoundException("User not found");
        }
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_ORDER_SQL)) {

            for (Product product : products) {
                statement.setInt(1, user.getUserId());
                statement.setInt(2, product.getProductId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException {
        if (!checkUserExists(userId)) {
            throw new UserNotFoundException("User not found");
        }
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(CANCEL_ORDER_SQL)) {

            statement.setInt(1, userId);
            statement.setInt(2, orderId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new OrderNotFoundException("Order not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createProduct(Product product) {
        try (Connection connection = DBConnUtil.getConnection()) {
            String createProductSql = null;
            if (product instanceof Electronics) {
                createProductSql = CREATE_ELECTRONICS_PRODUCT_SQL;
            } else if (product instanceof Clothing) {
                createProductSql = CREATE_CLOTHING_PRODUCT_SQL;
            }

            try (PreparedStatement statement = connection.prepareStatement(createProductSql)) {
                statement.setInt(1, product.getProductId());
                statement.setString(2, product.getProductName());
                statement.setString(3, product.getDescription());
                statement.setDouble(4, product.getPrice());
                statement.setInt(5, product.getQuantityInStock());
                statement.setString(6, product.getType());

                if (product instanceof Electronics) {
                    Electronics electronics = (Electronics) product;
                    statement.setString(7, electronics.getBrand());
                    statement.setInt(8, electronics.getWarrantyPeriod());
                } else if (product instanceof Clothing) {
                    Clothing clothing = (Clothing) product;
                    statement.setString(7, clothing.getSize());
                    statement.setString(8, clothing.getColor());
                }

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUser(User user) {
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_USER_SQL)) {

            statement.setInt(1, user.getUserId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRole());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkUserExists(int userId) {
        boolean userExists = false;
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(CHECK_USER_EXISTS)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    userExists = count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL_PRODUCTS_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("productName");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int quantityInStock = resultSet.getInt("quantityInStock");
                String type = resultSet.getString("type");

                Product product = null;
                if ("Electronics".equalsIgnoreCase(type)) {
                    String brand = resultSet.getString("brand");
                    int warrantyPeriod = resultSet.getInt("warrantyPeriod");
                    product = new Electronics(productId, productName, description, price, quantityInStock, type, brand, warrantyPeriod);
                } else if ("Clothing".equalsIgnoreCase(type)) {
                    String size = resultSet.getString("size");
                    String color = resultSet.getString("color");
                    product = new Clothing(productId, productName, description, price, quantityInStock, type, size, color);
                }

                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    private Product getProductById(int productId) {
        Product product = null;
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_PRODUCT_BY_PRODUCT_ID_SQL)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String productName = resultSet.getString("productName");
                    String description = resultSet.getString("description");
                    double price = resultSet.getDouble("price");
                    int quantityInStock = resultSet.getInt("quantityInStock");
                    String type = resultSet.getString("type");

                    if ("Electronics".equalsIgnoreCase(type)) {
                        String brand = resultSet.getString("brand");
                        int warrantyPeriod = resultSet.getInt("warrantyPeriod");
                        product = new Electronics(productId, productName, description, price, quantityInStock, type, brand, warrantyPeriod);
                    } else if ("Clothing".equalsIgnoreCase(type)) {
                        String size = resultSet.getString("size");
                        String color = resultSet.getString("color");
                        product = new Clothing(productId, productName, description, price, quantityInStock, type, size, color);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public List<Product> getOrderByUser(User user) {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ORDERS_BY_USER_SQL)) {

            statement.setInt(1, user.getUserId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                products.add(getProductById(productId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
