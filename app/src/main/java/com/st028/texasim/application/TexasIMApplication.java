package com.st028.texasim.application;

import android.app.Application;

import com.st028.texasim.R;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


/**
 * Created by sam on 7/31/14.
 */
@ReportsCrashes (
        formKey = "",
        formUri = "http://www.cs.utexas.edu/~st028/cgi-bin/acra.scgi",
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
