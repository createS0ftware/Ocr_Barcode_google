package com.google.android.gms.samples.vision.inner.bink.analytics;

/**
 * Created by jmcdonnell on 20/09/2016.
 */

public enum Screen {

    Profile("profile"),
    EditAddress("editAddress"),
    EditDetails("editDetails"),
    EditMobile("editMobile"),
    Onboarding("onboarding"),
    Login("login"),
    Register("register"),
    ForgotPassword("forgotPassword"),
    Wallet("wallet"),
    FullscreenBarcode("barcodeFullscreen"),
    PaymentDetail("paymentCardAccount:%s"), // Payment Card Account ID
    SchemeAccountDetail("schemeAccount:%s"), // Scheme Account ID
    ScanPaymentCard("scannerPaymentCard"),
    ScanLoyaltyCard("scannerLoyaltyCard"),
    SchemesList("availableSchemes"),
    CardChoice("chooseCardToAdd"),
    AddPaymentCard("addPaymentCard"),
    AddLoyaltyCard("addLoyaltyCard"),
    PaymentTermsAndConditions("paymentCardTermsAndConditions"),
    HelpBarcodeDialog("addCardHelp:barcode"),
    HelpPaymentDialog("addCardHelp:payment"),
    HelpLoyaltyDialog("addCardHelp:loyalty"),
    Share("share"),
    Settings("settings"),
    ChangePassword("changePassword"),
    PasscodeSetup("addPasscode"),
    PasscodeDisplay("passcode"),
    WebView("web:%s"); // URL

    String name;

    Screen(String name) {
        this.name = name;
    }

    public String getName(Object... args) {
        return String.format(name, args);
    }
}
