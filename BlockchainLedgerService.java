package com.chainmind.nexus.service;

import com.chainmind.nexus.model.DebtVoucher;
import com.chainmind.nexus.model.MerchantNode;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Simulates Blockchain Smart Contract execution for trade tokenization.
 */
public class BlockchainLedgerService {

    public DebtVoucher createOnChainVoucher(MerchantNode debtor, MerchantNode creditor, double amount) {
        String voucherId = "VOUCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String rawData = voucherId + debtor.getMerchantId() + creditor.getMerchantId() + amount + System.currentTimeMillis();
        String blockHash = generateSha256(rawData);
        
        return new DebtVoucher(voucherId, debtor, creditor, amount, blockHash);
    }

    private String generateSha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            return "0x" + Integer.toHexString(base.hashCode());
        }
    }
}
