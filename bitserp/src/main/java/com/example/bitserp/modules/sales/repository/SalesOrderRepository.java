package com.example.bitserp.modules.sales.repository;

import com.example.bitserp.modules.sales.entity.SalesOrder;
import com.example.bitserp.modules.sales.entity.SalesOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    List<SalesOrder> findByStatus(SalesOrderStatus status);
    List<SalesOrder> findByCustomerId(UUID customerId);
    List<SalesOrder> findByCreatedById(UUID userId);
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM SalesOrder o " +
            "WHERE o.status = com.example.bitserp.modules.sales.entity.SalesOrderStatus.DELIVERED")
    BigDecimal getTotalAmount();
    @Query(value = """
        SELECT COALESCE(SUM(total_amount), 0)
        FROM sales_orders
        WHERE status = 'DELIVERED'
        AND created_at >= NOW() - (:months * INTERVAL '1 month')
        """, nativeQuery = true)
    BigDecimal findRevenueLastMonths(int months);
    @Query("Select o From SalesOrder o " +
            "Where o.status = com.example.bitserp.modules.sales.entity.SalesOrderStatus.DISPATCHED " +
            "AND o.deliveryCords is NOT Null")
    List<SalesOrder> findDispatchedWithCoords();
}
