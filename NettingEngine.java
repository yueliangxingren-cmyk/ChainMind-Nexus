package com.chainmind.nexus.engine;

import com.chainmind.nexus.model.DebtVoucher;
import com.chainmind.nexus.model.MerchantNode;
import java.util.*;

/**
 * Multilateral Netting Settlement Engine powered by Tarjan's Strongly Connected Components Algorithm.
 * Resolves circular debt deadlock linearly with O(|V| + |E|) time complexity.
 */
public class NettingEngine {
    private Map<MerchantNode, List<DebtVoucher>> graph = new HashMap<>();
    
    private Map<MerchantNode, Integer> dfn = new HashMap<>();
    private Map<MerchantNode, Integer> low = new HashMap<>();
    private Set<MerchantNode> inStack = new HashSet<>();
    private Stack<MerchantNode> stack = new Stack<>();
    private int timeStamp = 0;

    public void registerMerchant(MerchantNode merchant) {
        graph.putIfAbsent(merchant, new ArrayList<>());
    }

    public void addDebtVoucher(DebtVoucher voucher) {
        registerMerchant(voucher.getDebtor());
        registerMerchant(voucher.getCreditor());
        graph.get(voucher.getDebtor()).add(voucher);
    }

    public void runNettingCloser() {
        dfn.clear(); low.clear(); inStack.clear(); stack.clear();
        timeStamp = 0;

        for (MerchantNode node : graph.keySet()) {
            if (!dfn.containsKey(node)) {
                tarjanDFS(node);
            }
        }
    }

    private void tarjanDFS(MerchantNode u) {
        dfn.put(u, timeStamp);
        low.put(u, timeStamp);
        timeStamp++;
        stack.push(u);
        inStack.add(u);

        List<DebtVoucher> edges = graph.get(u);
        if (edges != null) {
            for (DebtVoucher voucher : edges) {
                if (voucher.getCurrentAmount() <= 0) continue;
                MerchantNode v = voucher.getCreditor();
                
                if (!dfn.containsKey(v)) {
                    tarjanDFS(v);
                    low.put(u, Math.min(low.get(u), low.get(v)));
                } else if (inStack.contains(v)) {
                    low.put(u, Math.min(low.get(u), dfn.get(v)));
                }
            }
        }

        if (low.get(u).equals(dfn.get(u))) {
            List<MerchantNode> scc = new ArrayList<>();
            MerchantNode w;
            do {
                w = stack.pop();
                inStack.remove(w);
                scc.add(w);
            } while (!w.equals(u));

            if (scc.size() > 1) {
                executeSccClearing(scc);
            }
        }
    }

    private void executeSccClearing(List<MerchantNode> scc) {
        List<DebtVoucher> cycleVouchers = new ArrayList<>();
        if (findCycle(scc.get(0), scc.get(0), new HashSet<>(), cycleVouchers)) {
            double wMin = Double.MAX_VALUE;
            for (DebtVoucher v : cycleVouchers) {
                wMin = Math.min(wMin, v.getCurrentAmount());
            }

            if (wMin > 0) {
                System.out.println("\n[ChainMind Nexus Engine] Circular Credit Deadlock Detected & Resolved!");
                for (DebtVoucher v : cycleVouchers) {
                    v.setCurrentAmount(v.getCurrentAmount() - wMin);
                    if (v.getCurrentAmount() <= 0) {
                        v.setStatus("NETTED");
                    }
                    System.out.printf("   Ledger Updated: [%s] -> [%s] | Netted Cleared: %.2f HKD | Hash: %s\n",
                            v.getDebtor().getBusinessName(), v.getCreditor().getBusinessName(), wMin, v.getBlockchainHash());
                }
            }
        }
    }

    private boolean findCycle(MerchantNode curr, MerchantNode target, Set<MerchantNode> visited, List<DebtVoucher> res) {
        visited.add(curr);
        List<DebtVoucher> edges = graph.get(curr);
        if (edges != null) {
            for (DebtVoucher v : edges) {
                if (v.getCurrentAmount() <= 0) continue;
                MerchantNode next = v.getCreditor();
                if (next.equals(target) && visited.size() > 1) {
                    res.add(v);
                    return true;
                }
                if (!visited.contains(next)) {
                    if (findCycle(next, target, visited, res)) {
                        res.add(0, v);
                        return true;
                    }
                }
            }
        }
        visited.remove(curr);
        return false;
    }
}
