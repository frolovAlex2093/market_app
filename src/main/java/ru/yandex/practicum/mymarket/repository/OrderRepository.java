package ru.yandex.practicum.mymarket.repository;

import ru.yandex.practicum.mymarket.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}