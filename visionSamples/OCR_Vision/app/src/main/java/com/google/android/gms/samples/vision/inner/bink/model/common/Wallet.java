package com.google.android.gms.samples.vision.inner.bink.model.common;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.loyaltyangels.bink.model.payment.PaymentCardAccount;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;

import java.util.ArrayList;

/**
 * Created by hansonaboagye on 26/07/16.
 */
public class Wallet {

    @SerializedName("scheme_accounts")
    ArrayList<SchemeAccount> schemeAccounts;

    @SerializedName("payment_card_accounts")
    ArrayList<PaymentCardAccount> paymentCardAccounts;

    @NonNull
    public ArrayList<SchemeAccount> getSchemeAccounts() {
        if (schemeAccounts == null) {
            schemeAccounts = new ArrayList<>();
        }
        return schemeAccounts;
    }

    @NonNull
    public ArrayList<PaymentCardAccount> getPaymentCardAccounts() {
        if (paymentCardAccounts == null) {
            paymentCardAccounts = new ArrayList<>();
        }
        return paymentCardAccounts;
    }

    public void addPaymentCard(PaymentCardAccount paymentCard) {
        getPaymentCardAccounts().add(paymentCard);
    }

    public void deletePaymentCard(String accountId) {
        for (int i = 0; i < getPaymentCardAccounts().size(); i++) {
            Account account = getPaymentCardAccounts().get(i);

            if (accountId.equals(account.getId())) {
                getPaymentCardAccounts().remove(i);
            }
        }
    }

    public void addSchemeAccount(SchemeAccount schemeAccount) {
        getSchemeAccounts().add(schemeAccount);
    }

    public void deleteSchemeAccount(String accountId) {
        for (int i = 0; i < getSchemeAccounts().size(); i++) {
            Account account = getSchemeAccounts().get(i);

            if (accountId.equals(account.getId())) {
                getSchemeAccounts().remove(i);
            }
        }
    }

    public int getWalletSize() {
        return getPaymentCardAccounts().size() + getSchemeAccounts().size();
    }
}
