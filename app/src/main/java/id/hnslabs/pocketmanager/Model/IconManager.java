package id.hnslabs.pocketmanager.Model;

import id.hnslabs.pocketmanager.R;

/**
 * Created by HaidarNS on 05/12/2015.
 */
public class IconManager {
    public static final boolean TYPE_INCOME = true;
    public static final boolean TYPE_OUTCOME = false;

    public static int getIconResId(boolean tipeData, int stringId){
        int tmpId=0;
        if (!tipeData){ //kebalik naruhnya -_-
            switch (stringId){
                case 0 : tmpId = R.drawable.ic_type_daily; break;
                case 1 : tmpId = R.drawable.ic_type_education; break;
                case 2 : tmpId = R.drawable.ic_type_food; break;
                case 3 : tmpId = R.drawable.ic_type_health; break;
                case 4 : tmpId = R.drawable.ic_type_hobby; break;
                case 5 : tmpId = R.drawable.ic_type_person; break;
                case 6 : tmpId = R.drawable.ic_type_transportation; break;
                case 7 : tmpId = R.drawable.ic_type_transfer; break;
                case 8 : tmpId = R.drawable.ic_type_other_out; break;
            }
        } else {
            switch (stringId){
                case 0 : tmpId = R.drawable.ic_type_work; break;
                case 1 : tmpId = R.drawable.ic_type_present; break;
                case 2 : tmpId = R.drawable.ic_type_transfer; break;
                case 3 : tmpId = R.drawable.ic_type_loan; break;
                case 4 : tmpId = R.drawable.ic_type_other_in; break;
            }
        }
        return tmpId;
    }
}