package com.optima.plugin.repluginlib.download;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * create by wma
 * on 2020/9/16 0016
 */
public class TaskQueue {

    private int runningTaskCount;
    private LinkedHashMap<String, DownloadTask> taskQueue = new LinkedHashMap<>();

    public void addTask(DownloadTask task) {
        taskQueue.put(task.getDownloadUrl(), task);
    }

    public void excuse() {
        Iterator<String> iterator = taskQueue.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            final DownloadTask curTask = taskQueue.get(key);
            curTask.addOnFinishListener(new TaskFinishListener() {
                @Override
                public void finish(DownloadTask task) {
                    if (curTask == task) {
                        runningTaskCount--;
                        if(runningTaskCount<=0){
                            curTask.notify();
                        }
                    }
                }
            });
            curTask.excuse();
            runningTaskCount++;
            if (runningTaskCount >= 1) {
                try {
                    curTask.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
