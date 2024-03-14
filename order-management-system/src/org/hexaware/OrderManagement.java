package org.hexaware;

import org.hexaware.dao.OrderProcessor;
import org.hexaware.entity.model.Clothing;
import org.hexaware.entity.model.Electronics;
import org.hexaware.entity.model.Product;
import org.hexaware.entity.model.User;
import org.hexaware.exception.OrderNotFoundException;
import org.hexaware.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderManagement {
    private static final Scanner scanner = new Scanner(System.in);
    private static final OrderProcessor orderProcessor = new OrderProcessor();

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Order Management System Menu:");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Create Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. Get All Products");
            System.out.println("6. Get Order by User");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    createProduct();
                    break;
                case 3:
                    createOrder();
                    break;
                case 4:
                    cancelOrder();
                    break;
                case 5:
                    getAllProducts();
                    break;
                case 6:
                    getOrderByUser();
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 6.");
            }

        }
    }

    private static void createUser() {
        System.out.println("Enter user details:");
        System.out.print("User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Role (Admin/User): ");
        String role = scanner.nextLine();

        User user = new User(userId, username, password, role);
        orderProcessor.createUser(user);
        System.out.println("User created successfully.");
    }

    private static void createProduct() {
        System.out.println("Enter product details:");
        System.out.print("Product ID: ");
        int productId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Product Name: ");
        String productName = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Price: ");
        double price = scanner.nextDouble();
        System.out.print("Quantity in Stock: ");
        int quantityInStock = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Type (Electronics/Clothing): ");
        String type = scanner.nextLine();

        Product product;
        if ("Electronics".equalsIgnoreCase(type)) {
            System.out.print("Brand: ");
            String brand = scanner.nextLine();
            System.out.print("Warranty Period: ");
            int warrantyPeriod = scanner.nextInt();
            product = new Electronics(productId, productName, description, price, quantityInStock, type, brand, warrantyPeriod);
        } else if ("Clothing".equalsIgnoreCase(type)) {
            System.out.print("Size: ");
            String size = scanner.nextLine();
            System.out.print("Color: ");
            String color = scanner.nextLine();
            product = new Clothing(productId, productName, description, price, quantityInStock, type, size, color);
        } else {
            System.out.println("Invalid product type. Product not created.");
            return;
        }

        orderProcessor.createProduct(product);
        System.out.println("Product created successfully.");
    }

    private static void createOrder() {
        System.out.println("Enter order details:");
        System.out.print("User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        List<Product> products = orderProcessor.getAllProducts();
        System.out.println("Available Products:");
        for (Product product : products) {
            System.out.println(product.getProductId() + ". " + product.getProductName());
        }

        List<Product> selectedProducts = new ArrayList<>();
        boolean addMoreProducts = true;
        while (addMoreProducts) {
            System.out.print("Enter product ID to add to order (0 to finish): ");
            int productId = scanner.nextInt();
            scanner.nextLine();
            if (productId == 0) {
                addMoreProducts = false;
            } else {
                Product selectedProduct = products.stream()
                        .filter(product -> product.getProductId() == productId)
                        .findFirst()
                        .orElse(null);
                if (selectedProduct != null) {
                    selectedProducts.add(selectedProduct);
                    System.out.println(selectedProduct.getProductName() + " added to order.");
                } else {
                    System.out.println("Invalid product ID. Please enter a valid product ID.");
                }
            }
        }

        try {
            orderProcessor.createOrder(new User(userId, "", "", ""), selectedProducts);
            System.out.println("Order created successfully.");
        } catch (UserNotFoundException e) {
            System.out.println("User not found. Order creation failed.");
        }
    }

    private static void cancelOrder() {
        System.out.println("Enter order details to cancel:");
        System.out.print("User ID: ");
        int userId = scanner.nextInt();
        System.out.print("Order ID: ");
        int orderId = scanner.nextInt();
        try {
            orderProcessor.cancelOrder(userId, orderId);
            System.out.println("Order canceled successfully.");
        } catch (UserNotFoundException | OrderNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void getAllProducts() {
        List<Product> products = orderProcessor.getAllProducts();
        System.out.println("All Products:");
        for (Product product : products) {
            System.out.println(product);
        }
    }

    private static void getOrderByUser() {
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();
        User user = new User(userId, "", "", "");
        List<Product> products = orderProcessor.getOrderByUser(user);
        if (products.isEmpty()) {
            System.out.println("No orders found for user ID: " + userId);
        } else {
            System.out.println("Orders for User ID " + userId + ":");
            for (Product product : products) {
                System.out.println(product);
            }
        }
    }
}
