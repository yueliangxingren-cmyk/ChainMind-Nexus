package com.chainmind.nexus.model;

/**
 * Tokenized Digital Debt Voucher (Directed Edge in the credit graph)
 * Records trade detail anchors backed by immutable cryptographic proof on-chain.
 */
public class DebtVoucher {
    private String voucherId;        
    private MerchantNode debtor;     // Buyer / Ower
    private MerchantNode creditor;   // Seller / Receiver
    private double currentAmount;    // Outstandng balance in HKD
    private String blockchainHash;   // Proof of Trade SHA-256 Hash
    private String status;           // ACTIVE, NETTED, CLEARED

    public DebtVoucher(String voucherId, MerchantNode debtor, MerchantNode creditor, double amount, String hash) {
        this.voucherId = voucherId;
        this.debtor = debtor;
        this.creditor = creditor;
        this.currentAmount = amount;
        this.blockchainHash = hash;
        this.status = "ACTIVE";
    }

    public String getVoucherId() { return voucherId; }
    public MerchantNode getDebtor() { return debtor; }
    public MerchantNode getCreditor() { return creditor; }
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public String getBlockchainHash() { return blockchainHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
