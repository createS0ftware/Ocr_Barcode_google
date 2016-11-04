package com.google.android.gms.samples.vision.inner.bink.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.loyaltyangels.bink.model.Model;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;
import com.loyaltyangels.bink.service.BalanceUpdateService;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by jm on 14/07/16.
 */

public class BinkUtil {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2}[A-Za-z]*$");

    private static final String[] BARCLAYS_BIN_NUMBERS = new String[]{
            "543979",
            "492827", "492826", "485859", "465823", "452757", "425710", "492829", "464859", "675911",
            "557062", "557061", "556677", "554988", "554987", "554397", "554201", "554112", "552140",
            "550619", "550566", "550534", "550005", "548041", "547676", "545186", "540002", "536386",
            "531214", "530127", "526500", "518776", "518625", "512635", "670502", "492999", "492998",
            "492997", "492996", "492995", "492994", "492993", "492992", "492991", "492990", "492989",
            "492988", "492987", "492986", "492985", "492984", "492983", "492982", "492981", "492980",
            "492979", "492978", "492977", "492976", "492975", "492974", "492973", "492972", "492971",
            "492970", "492966", "492960", "492959", "492958", "492957", "492956", "492955", "492954",
            "492953", "492952", "492951", "492950", "492949", "492948", "492947", "492946", "492945",
            "492944", "492943", "492942", "492941", "492940", "492939", "492938", "492937", "492936",
            "492935", "492934", "492933", "492932", "492931", "492930", "492929", "492928", "492927",
            "492926", "492925", "492924", "492923", "492922", "492921", "492920", "492919", "492918",
            "492917", "492916", "492915", "492914", "492913", "492912", "492910", "492909", "492908",
            "492907", "492906", "492905", "492904", "492903", "492902", "492901", "492900", "491750",
            "491749", "491748", "489055", "489054", "487027", "486496", "486485", "486484", "486459",
            "486451", "486446", "486416", "486404", "486403", "484499", "484498", "484420", "484419",
            "475149", "474535", "471567", "471566", "471565", "471532", "465923", "465922", "465921",
            "465911", "465902", "465901", "465867", "465866", "465865", "465864", "465863", "465862",
            "465861", "465860", "465859", "465858", "462747", "461250", "459898", "459897", "459896",
            "459885", "459884", "459883", "459881", "459880", "459879", "456725", "453979", "453978",
            "449355", "447318", "432168", "430532", "429595", "427700", "426525", "426501", "425757",
            "416022", "416013", "412996", "412995", "412993", "412992", "412991", "412282", "412280",
            "409402", "409401", "409400", "409026", "409025", "409024", "409023", "408368", "408367",
            "405068", "403584", "402152", "402148", "402147", "400115", "424564", "557843", "556107",
            "543247", "541770", "539616", "530129", "530128", "530126", "530125", "530124", "530123",
            "530122", "530121", "530120", "523065", "520665", "518109", "517240", "517239", "517238",
            "517237", "517236", "517235", "517234", "517233", "439314", "530831", "426510", "492828"
    };

    public static boolean isBarclayCard(String cardNumber) {
        for (String num : BARCLAYS_BIN_NUMBERS) {
            if (cardNumber.startsWith(num)) {
                return true;
            }
        }

        return false;
    }

    public static boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean validateEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean hasStaleBalance(@NonNull SchemeAccount schemeAccount) {
        return schemeAccount.getScheme().hasPoints()
                && schemeAccount.getStatus() == SchemeAccount.Status.ACTIVE
                && (schemeAccount.getBalance() == null || schemeAccount.getBalance().isStale());
    }

    public static void resetApplication(Context context, Model model) {
        Intent intent = new Intent(context, BalanceUpdateService.class);
        context.stopService(intent);

        SharedPreferences accountPrefs = context.getSharedPreferences("EmailsnPasswords", Context.MODE_PRIVATE);
        accountPrefs.edit()
                .clear()
                .apply();

        SharedPreferences apiKeyPrefs;
        apiKeyPrefs = context.getSharedPreferences("api_key", Context.MODE_PRIVATE);
        apiKeyPrefs.edit()
                .clear()
                .apply();

        SharedPreferences marketingPrefs = context.getSharedPreferences("marketing-optins", Context.MODE_PRIVATE);
        marketingPrefs.edit()
                .clear()
                .apply();

        SharedPreferences userCardsPreferences = context.getSharedPreferences("userCards", Context.MODE_PRIVATE);
        userCardsPreferences.edit()
                .clear()
                .apply();

        model.clear();
        clearBarcodeCache(context);
    }

    private static void clearBarcodeCache(Context context)
    {
        try {
            File dir = new File(context.getCacheDir() + "/barcodes/");
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // delete dir recursively just in case 
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
