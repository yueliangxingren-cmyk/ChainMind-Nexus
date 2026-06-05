package com.chainmind.nexus;

import com.chainmind.nexus.engine.NettingEngine;
import com.chainmind.nexus.model.DebtVoucher;
import com.chainmind.nexus.model.MerchantNode;
import com.chainmind.nexus.service.BlockchainLedgerService;

public class AppMainSimulator {
    public static void main(String[] args) {
        BlockchainLedgerService blockchainService = new BlockchainLedgerService();
        NettingEngine engine = new NettingEngine();

        // Setup real scenario merchants in Hong Kong
        MerchantNode restaurant = new MerchantNode("BR-9981", "Mong Kok Ah Kee Restaurant", "Catering");
        MerchantNode supplier = new MerchantNode("BR-4432", "GBA Universal Food Wholesaler", "Trade");
        MerchantNode logistics = new MerchantNode("BR-1102", "HK Shunda Cross-Border Logistics", "Service");

        System.out.println("====== Step 1: Tokenizing Invoices & Anchoring onto Blockchain ======");
        
        DebtVoucher v1 = blockchainService.createOnChainVoucher(restaurant, supplier, 80000.0);
        System.out.println("Voucher On-chain Success! Hash: " + v1.getBlockchainHash());

        DebtVoucher v2 = blockchainService.createOnChainVoucher(supplier, logistics, 50000.0);
        DebtVoucher v3 = blockchainService.createOnChainVoucher(logistics, restaurant, 60000.0);

        engine.addDebtVoucher(v1);
        engine.addDebtVoucher(v2);
        engine.addDebtVoucher(v3);

        System.out.println("\n====== Step 2: Running ChainMind Tarjan Netting Settlement Engine ======");
        engine.runNettingCloser();
        
        System.out.println("\n====== Step 3: Netting Completed. Balance Syncing back to Core DB ======");
        System.out.printf("Ah Kee Restaurant Remaining Payable: %.2f HKD (Original: 80,000)\n", v1.getCurrentAmount());
        System.out.printf("GBA Food Wholesaler Remaining Payable: %.2f HKD (Original: 50,000)\n", v2.getCurrentAmount());
        System.out.printf("Shunda Logistics Remaining Payable: %.2f HKD (Original: 60,000)\n", v3.getCurrentAmount());
    }
}
