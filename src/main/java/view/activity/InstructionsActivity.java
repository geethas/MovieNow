package view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import gsrini.movienow.view.R;

public class InstructionsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText("Thank you for downloading MovieNow! Please read the following to understand the functionality of this app");

        TextView viewDesc = (TextView) findViewById(R.id.view_description);
        viewDesc.setText("The first view displays movies playing in theaters near you with in a 5 mile radius. " +
                "The radius can be changed by moving the red horizontal bar on the bottom of this view");

        TextView searchDesc = (TextView) findViewById(R.id.search_description);
        searchDesc.setText("This icon is used to get movies playing in a different location. " +
                "Search by postal code, city, city and state, or full address");

        TextView currentLocDesc = (TextView) findViewById(R.id.current_loc_description);
        currentLocDesc.setText("This icon is used to go back to the current location if location has been changed");

        TextView calendarDescription = (TextView) findViewById(R.id.calendar_description);
        calendarDescription.setText("This icon is used to find movies playing on a day other than the current day.");

        TextView ratingDesc = (TextView) findViewById(R.id.rating_description);
        ratingDesc.setText("This icon is used to sort movies by critic or audience rating. The movies are " +
                "automatically sorted by critic rating");

        TextView viewTwoDesc = (TextView) findViewById(R.id.view_two_description);
        viewTwoDesc.setText("The second view displays movie information");

        TextView calendarEventDesc = (TextView) findViewById(R.id.calendar_event_description);
        calendarEventDesc.setText("This icon is used to create a calendar event on the device");

        TextView textDesc = (TextView) findViewById(R.id.text_icon_description);
        textDesc.setText("This icon is used to send a sms message. This feature also uses contacts from the device if allowed");

        TextView ticketDesc = (TextView) findViewById(R.id.ticket_icon_description);
        ticketDesc.setText("This icon is used to buy tickets from the Fandango website");

        TextView apiUsed = (TextView) findViewById(R.id.api_used);
        apiUsed.setText("This app uses GraceNote OnConnect and Rotten Tomatoes API. Note: This app only works on U.S territories and may use the data plan!");

        Button readButton = (Button) findViewById(R.id.read_button);
        final Context context = this;
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieView.class);
                context.startActivity(intent);
            }
        });
        }
    }


