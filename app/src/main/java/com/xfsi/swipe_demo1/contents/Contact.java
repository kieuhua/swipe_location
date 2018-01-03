package com.xfsi.swipe_demo1.contents;

import com.xfsi.swipe_demo1.R;

/**
 * Created by local-kieu on 3/22/16.
 */
public class Contact {
    // the list of dummy contacts
    public static final Contact[] CONTACTS = {
            new Contact("Kieu"),
            new Contact("Morgan"),
            new Contact("Bambi"),
            new Contact("Tuyet"),
            new Contact("Eric"),
            new Contact("Aiden"),
            new Contact("Trimi")
    };

    public static final String ID = "contact_id";
    public static final int INVALID_ID = -1;
    private final String mName;

    public Contact(String name) {
        mName = name;
    }
    public static Contact byId(int id) {
        return CONTACTS[id];
    }
    public String getName() {
        return mName;
    }
    public int getIcon() {
        return R.mipmap.logo_avatar;
    }

}
