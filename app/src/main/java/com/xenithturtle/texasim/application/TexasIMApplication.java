package com.xenithturtle.texasim.application;

import android.app.Application;

import com.xenithturtle.texasim.R;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


/**
 * Created by sam on 7/31/14.
 */
@ReportsCrashes (
        formKey = "",
        formUri = "http://www.cs.utexas.edu/~st028/cgi-bin/<script-here>",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class TexasIMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
    }
}
