package android.app.job;

import java.util.Collections;
import java.util.List;

public class JobScheduler {
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;

    public JobScheduler() {}

    public void cancel(int p0) {}
    public void cancelAll() {}
    public int enqueue(JobInfo p0, JobWorkItem p1) { return RESULT_SUCCESS; }
    public List<JobInfo> getAllPendingJobs() { return Collections.emptyList(); }
    public JobInfo getPendingJob(int jobId) { return null; }
    public int schedule(JobInfo p0) { return RESULT_SUCCESS; }
    public int scheduleAsPackage(JobInfo p0, String packageName, int userId, String tag) {
        return RESULT_SUCCESS;
    }
}
