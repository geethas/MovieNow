package view.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import gsrini.movienow.view.R;
import model.data.StaticDataHolder;
import model.vo.MovieVO;

public class MessageView extends ActionBarActivity {

    static HashMap<String, String> contactsMap = new HashMap<>();
    AutoCompleteTextView textPhoneNo;
    EditText textSMS;

    MovieVO movieVO = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String movieTitle = (String) getIntent().getExtras().get("movieTitle");
        final String theaterName = (String) getIntent().getExtras().get("theaterName");
        final String showtime = (String) getIntent().getExtras().get("showtime");
        final String selectedDate = (String) getIntent().getExtras().get("selectedDate");
        final String day = (String) getIntent().getExtras().get("day");

        if (StaticDataHolder.movieCache != null && StaticDataHolder.movieCache.get(StaticDataHolder.postalCode) != null) {
            movieVO = StaticDataHolder.movieCache.get(StaticDataHolder.postalCode).get(movieTitle);
        }

        textPhoneNo = (AutoCompleteTextView) findViewById(R.id.editTextPhoneNo);
        textSMS = (EditText) findViewById(R.id.editTextSMS);

        // show contacts when user tries to type number or name in phone number box
        if (contactsMap.isEmpty())
            displayContacts();

        ArrayList<String> fullContacts = new ArrayList<>();
        fullContacts.addAll(contactsMap.keySet());
        fullContacts.add(String.valueOf(contactsMap.values()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, fullContacts);

        textPhoneNo.setThreshold(2);
        textPhoneNo.setAdapter(adapter);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        final String todayDate = dateFormat.format(cal.getTime());

        if (selectedDate != null && selectedDate.equals(todayDate)) {
            textSMS.setText("Hey, you want to watch " + movieVO.getTitle() + " today in " + theaterName + " at " + showtime);
        } else if (day != null) {
            textSMS.setText("Hey, you want to watch " + movieVO.getTitle() + " on " + day + ", " + selectedDate + " in " + theaterName + " at " + showtime);
        } else {
            textSMS.setText("Hey, you want to watch " + movieVO.getTitle() + " on " + selectedDate + " in " + theaterName + " at " + showtime);
        }
    }

    /**
     * Show contacts when user is typing name in phone number edit text field
     *
     */
    private void displayContacts() {

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            Log.d("MessageView", "name: " + name);
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.d("MessageView", "phone number: " + phoneNumber);

            contactsMap.put(name, phoneNumber);
        }

        phones.close();
    }

    /**
     * Configure menu for actionbar
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return true;
    }

    public void sendMessage(){

        Log.d(this.getClass().getSimpleName(), "Sending Message");

        if(textPhoneNo != null && textSMS != null) {
            String phoneNo = textPhoneNo.getText().toString();

            if(!contactsMap.isEmpty() && (!Character.isDigit(phoneNo.charAt(0)))) {
                phoneNo = contactsMap.get(phoneNo);
                Log.d("MessageView", "Phone no sending to: " + phoneNo);
            }

            String sms = textSMS.getText().toString();

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent!",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again later!",
                        Toast.LENGTH_LONG).show();
                Log.d(this.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.send:
                sendMessage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *  hides keyboard if user touches outside edit text
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int mEvent[] = new int[2];
            if(w != null) {
                w.getLocationOnScreen(mEvent);
                float x = event.getRawX() + w.getLeft() - mEvent[0];
                float y = event.getRawY() + w.getTop() - mEvent[1];

                if (event.getAction() == MotionEvent.ACTION_UP
                        && (x < w.getLeft() || x >= w.getRight()
                        || y < w.getTop() || y > w.getBottom())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(getWindow() != null && getWindow().getCurrentFocus() != null)
                        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }
            }
        }
        return ret;
    }

}
