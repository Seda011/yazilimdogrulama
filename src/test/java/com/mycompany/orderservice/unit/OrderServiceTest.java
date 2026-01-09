package com.mycompany.orderservice.unit;

import com.mycompany.orderservice.model.Customer;
import com.mycompany.orderservice.model.Order;
import com.mycompany.orderservice.model.OrderStatus;
import com.mycompany.orderservice.model.Product;
import com.mycompany.orderservice.repository.CustomerRepository;
import com.mycompany.orderservice.repository.OrderRepository;
import com.mycompany.orderservice.repository.ProductRepository;
import com.mycompany.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "Test User", "test@example.com");
        product1 = new Product(1L, "P1", BigDecimal.TEN, 10);
        product2 = new Product(2L, "P2", BigDecimal.ONE, 5);
    }

    @Test
    void shouldPlaceOrderSuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(123L);
            return o;
        });

        Order order = orderService.placeOrder(1L, Arrays.asList(1L, 2L));

        assertNotNull(order);
        assertEquals(123L, order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(2, order.getItems().size());
        verify(productRepository, times(2)).save(any(Product.class)); // Stock update
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(1L, List.of(99L)));
    }

    @Test
    void shouldThrowExceptionWhenOutOfStock() {
        product1.setStock(0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(1L, List.of(1L)));
    }
}
