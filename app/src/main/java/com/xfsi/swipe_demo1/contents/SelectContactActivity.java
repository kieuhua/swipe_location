package com.xfsi.swipe_demo1.contents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.xfsi.swipe_demo1.R;

/**
 * Created by local-kieu on 3/22/16.
 */
public class SelectContactActivity extends Activity {

    public static final String ACTION_SELECT_CONTACT = "com.xfis.swipe_demo1.intent.action.SELECT_CONTACT";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contact);

        Intent intent = getIntent();
        if (!intent.getAction().equals(ACTION_SELECT_CONTACT)) {
            finish();
            return;
        }
        // Set up the list of contacts
        ListView list = (ListView)findViewById(R.id.list);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(mOnItemClickListener);
    }

    private ListAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return Contact.CONTACTS.length;
        }

        @Override
        public Contact getItem(int position) {
            return Contact.CONTACTS[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent);
        }
    };

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent data = new Intent();
            data.putExtra(Contact.ID, position);
            setResult(SendMessageActivity.REQUEST_SELECT_CONTACT, data);
            finish();
        }
    };

}
