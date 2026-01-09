package com.mycompany.orderservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.orderservice.model.Customer;
import com.mycompany.orderservice.model.Product;
import com.mycompany.orderservice.repository.CustomerRepository;
import com.mycompany.orderservice.repository.OrderRepository;
import com.mycompany.orderservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long customerId;
    private Long productId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = customerRepository.save(new Customer(null, "IT User", "it@example.com"));
        customerId = customer.getId();

        Product product = productRepository.save(new Product(null, "IT Product", BigDecimal.TEN, 100));
        productId = product.getId();
    }

    @Test
    void shouldPlaceOrderAndRetrieveIt() throws Exception {
        List<Long> productIds = Arrays.asList(productId);

        mockMvc.perform(post("/api/orders")
                .param("customerId", customerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Verify retrieval
        mockMvc.perform(get("/api/orders/customer/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
