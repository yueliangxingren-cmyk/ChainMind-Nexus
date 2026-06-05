package com.chainmind.nexus.model;

import java.util.Objects;

/**
 * Merchant Node (Vertex in the credit graph)
 * Represents Hong Kong SMEs registered with unique Business Registration (BR) Numbers.
 */
public class MerchantNode {
    private String merchantId;   // e.g., BR12345678
    private String businessName; // e.g., Mong Kok Ah Kee Restaurant
    private String industryType; // e.g., Catering, Retail, Trade

    public MerchantNode(String merchantId, String businessName, String industryType) {
        this.merchantId = merchantId;
        this.businessName = businessName;
        this.industryType = industryType;
    }

    public String getMerchantId() { return merchantId; }
    public String getBusinessName() { return businessName; }
    public String getIndustryType() { return industryType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Strict safe type check without using 'instanceof' operator
        if (o == null || getClass() != o.getClass()) return false;
        MerchantNode that = (MerchantNode) o;
        return Objects.equals(merchantId, that.merchantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchantId);
    }
}
