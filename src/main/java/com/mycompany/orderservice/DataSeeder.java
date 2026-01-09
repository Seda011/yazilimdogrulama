package com.mycompany.orderservice;

import com.mycompany.orderservice.model.Customer;
import com.mycompany.orderservice.model.Product;
import com.mycompany.orderservice.repository.CustomerRepository;
import com.mycompany.orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (customerRepository.count() == 0) {
            customerRepository.save(new Customer(null, "John Doe", "john@example.com"));
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product(null, "Laptop", new BigDecimal("1200.00"), 10));
            productRepository.save(new Product(null, "Mouse", new BigDecimal("25.00"), 50));
            productRepository.save(new Product(null, "Keyboard", new BigDecimal("75.00"), 20));
        }
    }
}
