package ecommercemanagement;

import ecommercemanagement.service.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    static final String URL =
            "jdbc:mysql://localhost:3306/ecommerce_management";

    static final String USER = "root";
    static final String PASSWORD = "root";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con =
                         DriverManager.getConnection(URL, USER, PASSWORD);
                 Scanner sc = new Scanner(System.in)) {

                createTables(con);

                ProductService productService = new ProductService();
                CustomerService customerService = new CustomerService();
                CartService cartService = new CartService();
                OrderService orderService = new OrderService();
                OrderTrackingService trackingService = new OrderTrackingService();

                Map<Integer, Integer> cart = new HashMap<>();

                while (true) {

                    System.out.println("\n===== E-COMMERCE MANAGEMENT =====");
                    System.out.println("1. Add Product");
                    System.out.println("2. View Product Catalog");
                    System.out.println("3. Customer Registration");
                    System.out.println("4. Add Product to Cart");
                    System.out.println("5. View Shopping Cart");
                    System.out.println("6. Place Order");
                    System.out.println("7. Track Order");
                    System.out.println("8. Exit");

                    System.out.print("Enter your choice: ");

                    int choice;

                    try {
                        choice = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid menu number.");
                        continue;
                    }

                    try {
                        switch (choice) {

                            case 1:
                                productService.addProduct(con, sc);
                                break;

                            case 2:
                                productService.viewProducts(con);
                                break;

                            case 3:
                                customerService.registerCustomer(con, sc);
                                break;

                            case 4:
                                cartService.addToCart(con, sc, cart);
                                break;

                            case 5:
                                cartService.viewCart(con, cart);
                                break;

                            case 6:
                                orderService.placeOrder(con, sc, cart);
                                break;

                            case 7:
                                trackingService.trackOrder(con, sc);
                                break;

                            case 8:
                                System.out.println("Thank you for using our application.");
                                return;

                            default:
                                System.out.println("Invalid choice.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");

                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input: " + e.getMessage());

                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }
    private static void createTables(Connection con) throws SQLException {

        String productsSql = """
                CREATE TABLE IF NOT EXISTS products (
                    product_id INT PRIMARY KEY AUTO_INCREMENT,
                    product_name VARCHAR(100) NOT NULL,
                    description VARCHAR(255),
                    price BIGINT NOT NULL,
                    stock INT NOT NULL DEFAULT 0,
                    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                    CHECK (price > 0),
                    CHECK (stock >= 0),
                    CHECK (status IN ('AVAILABLE', 'UNAVAILABLE'))
                )
                """;

        String customerSQL = """
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id INT PRIMARY KEY AUTO_INCREMENT,
                    customer_name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    phone VARCHAR(10) NOT NULL UNIQUE,
                    address VARCHAR(255) NOT NULL,
                
                    CHECK (CHAR_LENGTH(phone) = 10)
                )
                """;

        String ordersSql = """
                CREATE TABLE IF NOT EXISTS orders (
                    order_id INT PRIMARY KEY AUTO_INCREMENT,
                    customer_id INT NOT NULL,
                    total_amount DECIMAL(10,2) NOT NULL,
                    order_status VARCHAR(20) NOT NULL DEFAULT 'PLACED',
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                
                    FOREIGN KEY (customer_id)
                        REFERENCES customers(customer_id),
                
                    CHECK (total_amount > 0),
                
                    CHECK (order_status IN (
                        'PLACED',
                        'PROCESSING',
                        'SHIPPED',
                        'DELIVERED',
                        'CANCELLED'
                    ))
                )
                """;

        String order_items = """
                CREATE TABLE IF NOT EXISTS order_items (
                    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
                    order_id INT NOT NULL,
                    product_id INT NOT NULL,
                    quantity INT NOT NULL,
                    price BIGINT NOT NULL,
                
                    FOREIGN KEY (order_id)
                        REFERENCES orders(order_id),
                
                    FOREIGN KEY (product_id)
                        REFERENCES products(product_id),
                
                    CHECK (quantity > 0),
                    CHECK (price > 0)
                )
                """;

        createTable(con, productsSql, "Products");
        createTable(con, customerSQL, "Customers");
        createTable(con, ordersSql, "Orders");
        createTable(con, order_items, "Order Items");

    }

    private static void createTable(
            Connection con, String sql, String tableName) throws SQLException {

        try (Statement statement = con.createStatement()) {
            statement.execute(sql);
            System.out.println(tableName + " table is ready.");
        }
    }
}