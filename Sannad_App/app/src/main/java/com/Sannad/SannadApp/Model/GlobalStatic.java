package com.Sannad.SannadApp.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GlobalStatic {
    //public static AtomicInteger counter = new AtomicInteger(0);
    public final static ArrayList<String> references = new ArrayList<String>();

    //true for listview, false for gridview
    public static boolean listViewChosen = false;
    //link when no image is added to request
    public static final String noImage = "https://firebasestorage.googleapis.com/v0/b/sannadapp.appspot.com/o/no_image%2FNo-Image-Available.png?alt=media&token=a2c73922-a3c4-40d9-9da1-b3a4d58979aa";

    //allowed length of title and text in listview
    public static final int allowedTitleLength_listview = 20;
    public static final int allowedTextLength_listview = 100;

    //allowed length of title and text in gridview
    public static final int allowedTextLength_gridview = 19;
    public static final int allowedTitleLength_gridview = 12;

    //accessing gallery on phone
    public static final int PICK_IMAGE_REQUEST = 71;

    //link when user has no personal image to be added to user
    public static final String accountNoPersonalImage = "https://firebasestorage.googleapis.com/v0/b/sannadapp.appspot.com/o/no_personal_image%2Faccount_no_personal_image.png?alt=media&token=bddd98f3-a943-4074-bf64-a18ebe127035";

    //link when user has no personal image to be added to user
    public static final String requestNoPersonalImage = "https://firebasestorage.googleapis.com/v0/b/sannadapp.appspot.com/o/no_personal_image%2Frequest_no_personal_image.png?alt=media&token=3585fa05-2d54-48c9-b391-7942adcba551";

    //personal phone number
    public static final String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

    //my database reference
    public static final DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(GlobalStatic.myPhoneNumber);


    public static String getDate(String unparsed){

        String currentUnparsedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String currentDate = currentUnparsedDate.split(" ")[0];

        String currentDay = currentDate.split("-")[0];
        String currentMonth = currentDate.split("-")[1];
        String currentYear = currentDate.split("-")[2];

        String day = unparsed.split(" ")[0].split("-")[0];
        String month = unparsed.split(" ")[0].split("-")[1];
        String year = unparsed.split(" ")[0].split("-")[2];
        String unparsedTime = unparsed.split(" ")[1];
        String time = unparsedTime.split(":")[0] + ':' + unparsedTime.split(":")[1];

        //request of today
        if(currentMonth.equals(month) && currentDay.equals(day) && currentYear.equals(year))
            return time;

        //request of yesterday
        else if(!currentDay.equals("1") && currentMonth.equals(month) && String.valueOf(Integer.parseInt(currentDay)-1).equals(day) && currentYear.equals(year))
            return "yesterday" + ' ' + time;
        else if(currentDay.equals("1") && day.equals(lastDayInMonth(lastMonth(currentMonth),year)))
            return "yesterday" + ' ' + time;

        //else
        else
                   //request of this year
            return currentYear.equals(year) ?
                    day + '.' + month + ' ' + time :
                   //request  of another year
                    day + '.' + month + '.' + year + ' ' + time;
    }

    private static String lastDayInMonth(final String month, final String year){
        final int yearInt = Integer.parseInt(year);
        switch(month){
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                return "31";
            case "02": return yearInt % 4 == 0 ? "29" : "28";
            case "04":
            case "06":
            case "09":
            case "11":
                return "30";
            default: throw new IllegalArgumentException(String.format("month must be between 1 and 12, but it's %d",Integer.parseInt(month)));
        }
    }

    private static String lastMonth(final String month){
        switch (month){
            case "01": return "12";
            case "02": return "01";
            case "03": return "02";
            case "04": return "03";
            case "05": return "04";
            case "06": return "05";
            case "07": return "06";
            case "08": return "07";
            case "09": return "08";
            case "10": return "09";
            case "11": return "10";
            case "12": return "11";
            default: throw new IllegalArgumentException(String.format("month must be between 1 and 12, but it's %d",Integer.parseInt(month)));
        }
    }
}
