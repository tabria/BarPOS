package app.services.impl;

import app.dtos.OrderDto;
import app.entities.*;
import app.entities.enums.OrderStatus;
import app.repositories.*;
import app.services.api.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BarTableRepository barTableRepository;
    private final UserRepository userRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BarTableRepository barTableRepository, UserRepository userRepository, OrderProductRepository orderProductRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.barTableRepository = barTableRepository;
        this.userRepository = userRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderDto findOpenOrderByTable(Long tableId) {
        BarTable barTable = this.barTableRepository.findOne(tableId);
        Order order = this.orderRepository.findOpenOrderByBarTable(barTable);
        OrderDto orderDto = new OrderDto();
        orderDto.setDate(order.getDate());
        orderDto.setUser(order.getUser());
        orderDto.setBarTable(barTable);
        orderDto.setOrderId(order.getId());
        orderDto.setStatus(order.getStatus());
        List<OrderProduct> orderProductList = this.orderProductRepository.findProductsInOrder(order.getId());
        Map<Long, Integer> products = new HashMap<>();
        for (OrderProduct orderProduct : orderProductList) {
            products.put(orderProduct.getId().getProduct().getId(), orderProduct.getQuantity());
        }

        orderDto.setProducts(products);
        return orderDto;
    }

    @Override
    @Transactional
    public void createNewOrder(OrderDto orderDto) {
        Order order = new Order();

        BarTable barTable = orderDto.getBarTable();
        User user = orderDto.getUser();

        this.barTableRepository.changeTableStatus(false, orderDto.getBarTable().getId());

        order.setBarTable(barTable);
        order.setUser(user);
        order.setStatus("Open");
        order.setDate(new Date());

        this.orderRepository.save(order);


        Map<Long, Integer> products = orderDto.getProducts();
        for (Long productId : products.keySet()) {
            Product product = this.productRepository.findById(productId);
            OrderProductId orderProductId = new OrderProductId();
            orderProductId.setProduct(product);
            orderProductId.setOrder(order);

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setId(orderProductId);
            orderProduct.setQuantity(products.get(productId));
            this.orderProductRepository.save(orderProduct);
        }

    }

    @Override
    @Transactional
    public void closeOrder(Long orderId) {
        Order order = this.orderRepository.getOne(orderId);
        this.barTableRepository.changeTableStatus(true, order.getBarTable().getId());
        this.orderRepository.changeOrderStatus(OrderStatus.CLOSED, orderId);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = this.orderRepository.getOne(orderId);
        this.barTableRepository.changeTableStatus(true, order.getBarTable().getId());
        this.orderRepository.changeOrderStatus(OrderStatus.CANCELLED, orderId);
    }


}