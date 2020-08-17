package com.alodiga.primefaces.ultima.data.model;

/**
 * Class that holds all the info of the Status of a card for checking balance
 *
 * @author hvarona
 */
public class CardInfo {

    private String cardNumber;
    private String accountNumber;
    private String accountBalance;
    private String accountDollarBalance;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountDollarBalance() {
        return accountDollarBalance;
    }

    public void setAccountDollarBalance(String accountDollarBalance) {
        this.accountDollarBalance = accountDollarBalance;
    }

}
