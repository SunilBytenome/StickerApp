package com.github.gabrielbb.cutout.test;


import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class ReminderService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e(">>>",jobParameters.getService());
        return false;

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
