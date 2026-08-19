package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.dto.RevenueProjectionResponse;
import com.example.bitserp.modules.finance.entity.RevenueProjection;
import com.example.bitserp.modules.finance.repository.RevenueProjectionRepository;
import com.example.bitserp.modules.sales.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueProjectionService {

    private final RevenueProjectionRepository revenueProjectionRepository;
    private final SalesOrderRepository salesOrderRepository;

    public RevenueProjectionResponse projectNextMonth(int basisMonths) {
        BigDecimal totalRevenue = salesOrderRepository.getTotalAmount();
        BigDecimal avgMonthlyRevenue = totalRevenue
                .divide(BigDecimal.valueOf(basisMonths), 2, RoundingMode.HALF_UP);
        LocalDate nextMonth = LocalDate.now().plusMonths(1)
                .withDayOfMonth(1);
        RevenueProjection projection = revenueProjectionRepository
                .findByProjectionDateAndRegion(nextMonth, null)
                .orElse(new RevenueProjection());

        projection.setProjectionDate(nextMonth);
        projection.setProjectedAmount(avgMonthlyRevenue);
        projection.setMethod("MOVING_AVG");
        projection.setBasisMonths(basisMonths);

        return toResponse(revenueProjectionRepository.save(projection));
    }

    public List<RevenueProjectionResponse> getFutureProjections() {
        return revenueProjectionRepository
                .findByProjectionDateAfter(LocalDate.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RevenueProjectionResponse updateActual(Integer id, BigDecimal actualAmount) {
        RevenueProjection projection = revenueProjectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RevenueProjection Not Found"));

        projection.setActualAmount(actualAmount);
        return toResponse(revenueProjectionRepository.save(projection));
    }

    private RevenueProjectionResponse toResponse(RevenueProjection projection) {
        return new RevenueProjectionResponse(
                projection.getId(), projection.getProjectionDate(),
                projection.getProjectedAmount(), projection.getActualAmount(),
                projection.getMethod(), projection.getBasisMonths(),
                projection.getRegion()
        );
    }
}
