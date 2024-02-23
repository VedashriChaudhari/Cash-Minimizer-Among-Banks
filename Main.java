package ca_cp_sem1;

import java.util.*;

class Bank {
    public String name;
    public int netAmount;
    public Set<String> types;

    public Bank() {
        types = new HashSet<>();
    }
}

class MaxIndexAndType {
    public int maxIndex;
    public String matchingType;

    public MaxIndexAndType(int maxIndex, String matchingType) {
        this.maxIndex = maxIndex;
        this.matchingType = matchingType;
    }
}

public class Main {
    public static int getMinIndex(Bank[] listOfNetAmounts, int numBanks) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;

            if (listOfNetAmounts[i].netAmount < min) {
                minIndex = i;
                min = listOfNetAmounts[i].netAmount;
            }
        }
        return minIndex;
    }

    public static int getSimpleMaxIndex(Bank[] listOfNetAmounts, int numBanks) {
        int max = Integer.MIN_VALUE;
        int maxIndex = -1;
        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;

            if (listOfNetAmounts[i].netAmount > max) {
                maxIndex = i;
                max = listOfNetAmounts[i].netAmount;
            }
        }
        return maxIndex;
    }

    public static MaxIndexAndType getMaxIndex(Bank[] listOfNetAmounts, int numBanks, int minIndex, Bank[] input, int maxNumTypes) {
        int max = Integer.MIN_VALUE;
        int maxIndex = -1;
        String matchingType = "";

        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;

            if (listOfNetAmounts[i].netAmount < 0) continue;

            List<String> v = new ArrayList<>(maxNumTypes);
            v.addAll(Arrays.asList(new String[maxNumTypes]));
            List<String> listOfNetAmountsTypes = new ArrayList<>(listOfNetAmounts[i].types);
            List<String> inputTypes = new ArrayList<>(input[i].types);
            List<String> vCopy = new ArrayList<>(v);

            Collections.sort(listOfNetAmountsTypes);
            Collections.sort(inputTypes);

            int ls = setIntersection(listOfNetAmountsTypes, inputTypes, v, vCopy);

            if (ls != 0 && max < listOfNetAmounts[i].netAmount) {
                max = listOfNetAmounts[i].netAmount;
                maxIndex = i;
                matchingType = v.get(0);
            }
        }

        return new MaxIndexAndType(maxIndex, matchingType);
    }

    public static int setIntersection(List<String> list1, List<String> list2, List<String> result, List<String> resultCopy) {
        int i = 0, j = 0, k = 0;

        while (i < list1.size() && j < list2.size()) {
            int cmp = list1.get(i).compareTo(list2.get(j));
            if (cmp == 0) {
                result.set(k, list1.get(i));
                i++;
                j++;
                k++;
            } else if (cmp < 0) {
                i++;
            } else {
                j++;
            }
        }

        if (k < resultCopy.size()) {
            resultCopy.subList(k, resultCopy.size()).clear();
        }

        return k;
    }

    public static void printAns(List<List<MaxIndexAndType>> ansGraph, int numBanks, Bank[] input) {
        System.out.println("\nThe transactions for minimum cash flow are as follows : \n\n");
        for (int i = 0; i < numBanks; i++) {
            for (int j = 0; j < numBanks; j++) {

                if (i == j) continue;

                if (ansGraph.get(i).get(j).maxIndex != 0 && ansGraph.get(j).get(i).maxIndex != 0) {

                    if (ansGraph.get(i).get(j).maxIndex == ansGraph.get(j).get(i).maxIndex) {
                        ansGraph.get(i).get(j).maxIndex = 0;
                        ansGraph.get(j).get(i).maxIndex = 0;
                    } else if (ansGraph.get(i).get(j).maxIndex > ansGraph.get(j).get(i).maxIndex) {
                        ansGraph.get(i).get(j).maxIndex -= ansGraph.get(j).get(i).maxIndex;
                        ansGraph.get(j).get(i).maxIndex = 0;

                        System.out.println(input[i].name + " pays Rs" + ansGraph.get(i).get(j).maxIndex + " to " + input[j].name + " via " + ansGraph.get(i).get(j).matchingType);
                    } else {
                        ansGraph.get(j).get(i).maxIndex -= ansGraph.get(i).get(j).maxIndex;
                        ansGraph.get(i).get(j).maxIndex = 0;

                        System.out.println(input[j].name + " pays Rs " + ansGraph.get(j).get(i).maxIndex + " to " + input[i].name + " via " + ansGraph.get(j).get(i).matchingType);
                    }
                } else if (ansGraph.get(i).get(j).maxIndex != 0) {
                    System.out.println(input[i].name + " pays Rs " + ansGraph.get(i).get(j).maxIndex + " to " + input[j].name + " via " + ansGraph.get(i).get(j).matchingType);
                } else if (ansGraph.get(j).get(i).maxIndex != 0) {
                    System.out.println(input[j].name + " pays Rs " + ansGraph.get(j).get(i).maxIndex + " to " + input[i].name + " via " + ansGraph.get(j).get(i).matchingType);
                }

                ansGraph.get(i).get(j).maxIndex = 0;
                ansGraph.get(j).get(i).maxIndex = 0;
            }
        }
        System.out.println("\n");
    }

    public static void minimizeCashFlow(int numBanks, Bank[] input, Map<String, Integer> indexOf, int numTransactions, List<List<Integer>> graph, int maxNumTypes) {
        Bank[] listOfNetAmounts = new Bank[numBanks];

        for (int b = 0; b < numBanks; b++) {
            listOfNetAmounts[b] = new Bank();
            listOfNetAmounts[b].name = input[b].name;
            listOfNetAmounts[b].types.addAll(input[b].types);

            int amount = 0;

            for (int i = 0; i < numBanks; i++) {
                amount += graph.get(i).get(b);
            }

            for (int j = 0; j < numBanks; j++) {
                amount += (-1) * graph.get(b).get(j);
            }

            listOfNetAmounts[b].netAmount = amount;
        }

        List<List<MaxIndexAndType>> ansGraph = new ArrayList<>();
        for (int i = 0; i < numBanks; i++) {
            ansGraph.add(new ArrayList<>(Collections.nCopies(numBanks, new MaxIndexAndType(0, ""))));
        }

        int numZeroNetAmounts = 0;

        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) numZeroNetAmounts++;
        }
        while (numZeroNetAmounts != numBanks) {

            int minIndex = getMinIndex(listOfNetAmounts, numBanks);
            MaxIndexAndType maxAns = getMaxIndex(listOfNetAmounts, numBanks, minIndex, input, maxNumTypes);

            int maxIndex = maxAns.maxIndex;

            if (maxIndex == -1) {

                ansGraph.get(minIndex).get(0).maxIndex += Math.abs(listOfNetAmounts[minIndex].netAmount);
                ansGraph.get(minIndex).get(0).matchingType = input[minIndex].types.iterator().next();

                int simpleMaxIndex = getSimpleMaxIndex(listOfNetAmounts, numBanks);
                ansGraph.get(0).get(simpleMaxIndex).maxIndex += Math.abs(listOfNetAmounts[minIndex].netAmount);
                ansGraph.get(0).get(simpleMaxIndex).matchingType = input[simpleMaxIndex].types.iterator().next();

                listOfNetAmounts[simpleMaxIndex].netAmount += listOfNetAmounts[minIndex].netAmount;
                listOfNetAmounts[minIndex].netAmount = 0;

                if (listOfNetAmounts[minIndex].netAmount == 0) numZeroNetAmounts++;

                if (listOfNetAmounts[simpleMaxIndex].netAmount == 0) numZeroNetAmounts++;

            } else {
                int transactionAmount = Math.min(Math.abs(listOfNetAmounts[minIndex].netAmount), listOfNetAmounts[maxIndex].netAmount);

                ansGraph.get(minIndex).get(maxIndex).maxIndex += transactionAmount;
                ansGraph.get(minIndex).get(maxIndex).matchingType = maxAns.matchingType;

                listOfNetAmounts[minIndex].netAmount += transactionAmount;
                listOfNetAmounts[maxIndex].netAmount -= transactionAmount;

                if (listOfNetAmounts[minIndex].netAmount == 0) numZeroNetAmounts++;

                if (listOfNetAmounts[maxIndex].netAmount == 0) numZeroNetAmounts++;
            }
        }

        printAns(ansGraph, numBanks, input);
    }

    public static void main(String[] args) {
        System.out.println("\n\t\t\t\t********************* Welcome to CASH FLOW MINIMIZER SYSTEM ***********************\n\n\n");
        System.out.println("This system minimizes the number of transactions among multiple banks in the different corners of the world that use different modes of payment. There is one world bank (with all payment modes) to act as an intermediary between banks that have no common mode of payment. \n\n");
        System.out.println("Enter the number of banks participating in the transactions.");
        Scanner scanner = new Scanner(System.in);
        int numBanks = scanner.nextInt();

        Bank[] input = new Bank[numBanks];
        Map<String, Integer> indexOf = new HashMap<>();

        System.out.println("Enter the details of the banks and transactions as stated:");
        System.out.println("Bank name, the number of payment modes it has, and the payment modes.");
        System.out.println("Bank name and payment modes should not contain spaces.");

        int maxNumTypes = 0;
        for (int i = 0; i < numBanks; i++) {
            if (i == 0) {
                System.out.print("World Bank : ");
            } else {
                System.out.print("Bank " + i + " : ");
            }
            input[i] = new Bank();
            Scanner bankScanner = new Scanner(System.in);
            input[i].name = bankScanner.next();
            indexOf.put(input[i].name, i);
         // Check if the bank name already exists in the indexOf map
//            if (indexOf.containsKey(input[i].name)&& i>0) {
//                System.out.println("Bank with the same name already exists. Please provide a unique name.");
//                i--; // Decrement i to re-enter the bank's information
//                continue;
//            }
            int numTypes = bankScanner.nextInt();

            if (i == 0) maxNumTypes = numTypes;

            while (numTypes-- > 0) {
                input[i].types.add(bankScanner.next());
            }
        }

        System.out.println("Enter the number of transactions.");
        int numTransactions = scanner.nextInt();

        List<List<Integer>> graph = new ArrayList<>(numBanks);
        for (int i = 0; i < numBanks; i++) {
            graph.add(new ArrayList<>(Collections.nCopies(numBanks, 0)));
        }

        System.out.println("Enter the details of each transaction as stated:");
        System.out.println("Debtor Bank, creditor Bank, and amount.");
        System.out.println("The transactions can be in any order.");
        for (int i = 0; i < numTransactions; i++) {
            System.out.print((i) + " th transaction : ");
            Scanner transactionScanner = new Scanner(System.in);
            String s1 = transactionScanner.next();
            String s2 = transactionScanner.next();
            int amount = transactionScanner.nextInt();

            graph.get(indexOf.get(s1)).set(indexOf.get(s2), amount);
        }

        minimizeCashFlow(numBanks, input, indexOf, numTransactions, graph, maxNumTypes);
    }
}
