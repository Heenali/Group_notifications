package intertech.com.nougatnotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String GROUP_KEY_BUNDLED = "group_key_bundled";
    private static final int NOTIFICATION_BUNDLED_BASE_ID = 1000;

    //simple way to keep track of the number of bundled notifications
    private static int numberOfBundled = 0;
    //Simple way to track text for notifications that have already been issued
    private static List<CharSequence> issuedMessages = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button inlineButton = (Button) findViewById(R.id.inlineResponse);
        inlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotificationUtil.inlineReplyNotification("This Notification includes inline actions");
            }
        });

        Button bundledButton = (Button) findViewById(R.id.bundled);
        bundledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               bundledNotification();
            }
        });

    }
    public static void bundledNotification()
    {
        Context context = NougatNotificationApplication.context();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        String message = "This is message # " + ++numberOfBundled
                + ". This text is generally too long to fit on a single line in a notification";
        issuedMessages.add(message);

        //Build and issue the group summary. Use inbox style so that all messages are displayed
        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Group Summary")
                .setContentText("This is the group summary")
                .setSmallIcon(R.drawable.ic_flag_notification)
                .setGroupSummary(true)
                .setGroup(GROUP_KEY_BUNDLED);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Messages:");
        for (CharSequence cs : issuedMessages)
        {
            inboxStyle.addLine(cs);
        }
        summaryBuilder.setStyle(inboxStyle);

        notificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID, summaryBuilder.build());



        //issue the Bundled notification. Since there is a summary notification, this will only display
        //on systems with Nougat or later
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Bundled Notification " + numberOfBundled)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_flag_notification)
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message))
                .setGroup(GROUP_KEY_BUNDLED);


        //Add an action that simply starts the main activity. This is not very useful it is mainly for demonstration
        Intent intent = new Intent(context, MainActivity.class);
        //Each notification needs a unique request code, so that each pending intent is unique. It does not matter
        //in this simple case, but is important if we need to take action on a specific notification, such as
        //deleting a message
        int requestCode = NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_input_get,
                        "Do Something", pendingIntent)
                        .build();
        builder.addAction(action);

        notificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled, builder.build());
    }
}
