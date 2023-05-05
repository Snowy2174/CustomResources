package plugin.customresources.controllers;

import com.palmergames.bukkit.towny.object.Town;

public class CustomResourcesTownBalanceManager {

    public static boolean townHasEnoughMoney(Town town, double amount){
        double townBankBalance = town.getAccount().getHoldingBalance();

        return !(amount > townBankBalance);
    }

    public static void withdrawTownMoney(Town town, double amount){
        // todo: implement
    }

    public static void depositTownMoney(Town town, double amount){
        // todo: implement
    }
}
