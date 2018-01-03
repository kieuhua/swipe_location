package com.xfsi.swipe_demo1.contents;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by local-kieu on 3/22/16.
 */
public class MyChooserTargetService extends ChooserTargetService {

        @Override
        public List<ChooserTarget> onGetChooserTargets (ComponentName compN, IntentFilter filter){
        // create ChooseTarget object for each Contact object store them in ArrayList
        ArrayList<ChooserTarget> targets = new ArrayList<ChooserTarget>();

        for (int i = 0; i < Contact.CONTACTS.length; i++) {
            if (Build.VERSION.SDK_INT >= 23 ) {
                Contact c = Contact.CONTACTS[i];
                Bundle extras = new Bundle();
                extras.putInt(Contact.ID, i);
                targets.add( new ChooserTarget(
                        c.getName(),
                        Icon.createWithResource(this, c.getIcon()),
                        0.5f,
                        compN,
                        extras ));
            }
        }
        return targets;
    }
}
