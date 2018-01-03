package com.xfsi.swipe_demo1.contents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xfsi.swipe_demo1.R;

/**
 * Created by local-kieu on 3/22/16.
 */
public class SendMessageActivity extends Activity {

    public static final int REQUEST_SELECT_CONTACT = 1;
    private String mBody;
    private int mContactId;

    TextView mTextContactName;
    TextView mTextMessageBody;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);
        setTitle(R.string.sending_message);

        mTextContactName = (TextView)findViewById(R.id.contact_name);
        mTextMessageBody = (TextView) findViewById(R.id.message_body);

        findViewById(R.id.send).setOnClickListener(mOnClickListener);
        // Resolve the share Intent.
        boolean resolved = resolveIntent(getIntent());
        if (!resolved) {
            finish();
            return;
        }
        // Set up the UI.
        prepareUI();
        if (mContactId == Contact.INVALID_ID) {
            selectContact();
        }
    }

    // SelectContactActivity come back here
    @Override public void onActivityResult(int req, int res, Intent data) {
        switch (req) {
            case REQUEST_SELECT_CONTACT:
                if (res == RESULT_OK) {
                    mContactId = data.getIntExtra(Contact.ID, Contact.INVALID_ID);
                }
                // Give up sharing the send_message if the user didn't choose a contact.
                if (mContactId == Contact.INVALID_ID) {
                    finish();
                    return;
                }
                prepareUI();
                break;
            default:
                super.onActivityResult(req, res, data);
        }
    }

    private boolean resolveIntent(Intent intent) {
       switch (intent.getAction()) {
           case Intent.ACTION_SEND:
               if (intent.getType().equals("text/plain")) {
                   mBody = intent.getStringExtra(Intent.EXTRA_TEXT);
               }
               return true;
       }
        return false;
    }

    private void prepareUI() {
        if (mContactId != Contact.INVALID_ID) {
            Contact c = Contact.CONTACTS[mContactId];
            ContactViewBinder.bind(c, mTextContactName);
        }
        mTextMessageBody.setText(mBody);
    }

    private void selectContact() {

        Intent intent = new Intent(this, SelectContactActivity.class);
        intent.setAction(SelectContactActivity.ACTION_SELECT_CONTACT);
        startActivityForResult(intent, REQUEST_SELECT_CONTACT);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.send:
                    send();
                    break;
            }
        }
    };

    // Pretends to send the text to the contact. This only shows a dummy message.
    private void send() {
        Toast.makeText(this, getString(R.string.message_sent, mBody, Contact.byId(mContactId).getName()),
                Toast.LENGTH_LONG).show();
        finish();   // back to DirectShareActivity
    }
}
