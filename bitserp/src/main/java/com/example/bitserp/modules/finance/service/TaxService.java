package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.entity.TaxSettings;
import com.example.bitserp.modules.finance.repository.CurrencyRateRepository;
import com.example.bitserp.modules.finance.repository.TaxSettingsRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TaxService {

    private final TaxSettingsRepository taxSettingsRepository;
    private final CurrencyRateRepository currencyRateRepository;
    private static final String COMPANY_STATE = "Maharashtra";

    public TaxCalculation calculatePOTax(
            BigDecimal subTotal,
            BigDecimal gstRate,
            String tdsCategory,
            String vendorState,
            boolean isInterstate
    ) {
        TaxCalculation calc = new TaxCalculation();
        calc.setSubTotal(subTotal);
        if(gstRate != null && gstRate.compareTo(BigDecimal.ZERO) > 0) {
            if(isInterstate) {
                BigDecimal igst = percent(subTotal, gstRate);
                calc.setIgstAmount(igst);
            } else {
                BigDecimal half = gstRate.divide(
                        BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP
                );
                BigDecimal cgst = percent(subTotal, half);
                calc.setCgstAmount(cgst);
                calc.setSgstAmount(cgst);
            }
        }

        BigDecimal tdsRate = getTDSRate(tdsCategory);
        if(tdsRate.compareTo(BigDecimal.ZERO) > 0) {
            calc.setTdsAmount(percent(subTotal, tdsRate));
        }

        BigDecimal taxTotal = calc.getCgstAmount()
                .add(calc.getSgstAmount())
                .add(calc.getIgstAmount());
        calc.setTaxTotal(taxTotal);

        calc.setGrandTotal(
                subTotal.add(taxTotal).subtract(calc.getTdsAmount())
        );

        return calc;
    }

    public TaxCalculation calculateSOTax(
            BigDecimal subTotal,
            BigDecimal gstRate,
            String customerState,
            boolean isInterstate,
            boolean isExport
    ) {
        TaxCalculation calc = new TaxCalculation();
        calc.setSubTotal(subTotal);

        if(isExport || gstRate == null || gstRate.compareTo(BigDecimal.ZERO) == 0) {
            calc.setGrandTotal(subTotal);
            return calc;
        }

        if(isInterstate) {
            calc.setIgstAmount(percent(subTotal, gstRate));
        } else {
            BigDecimal half = gstRate.divide(
                    BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP
            );
            calc.setCgstAmount(half);
            calc.setSgstAmount(half);
        }

        BigDecimal taxTotal = calc.getCgstAmount()
                .add(calc.getSgstAmount())
                .add(calc.getIgstAmount());
        calc.setTaxTotal(taxTotal);
        calc.setGrandTotal(subTotal.add(taxTotal));

        return calc;
    }

    public boolean isInterstate(String otherState) {
        return otherState != null
                && !otherState.equalsIgnoreCase(COMPANY_STATE);
    }

    private BigDecimal percent(BigDecimal base, BigDecimal rate) {
        return base.multiply(rate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal getTDSRate(String category) {
        if(category == null) return BigDecimal.ZERO;

        return taxSettingsRepository
                .findByTypeAndCategory("TDS", category)
                .map(TaxSettings::getRate)
                .orElse(BigDecimal.ZERO);
    }

    @Data
    public static class TaxCalculation {
        private BigDecimal subTotal = BigDecimal.ZERO;
        private BigDecimal cgstAmount = BigDecimal.ZERO;
        private BigDecimal sgstAmount = BigDecimal.ZERO;
        private BigDecimal igstAmount = BigDecimal.ZERO;
        private BigDecimal tdsAmount = BigDecimal.ZERO;
        private BigDecimal taxTotal =  BigDecimal.ZERO;
        private BigDecimal grandTotal = BigDecimal.ZERO;
    }
}
