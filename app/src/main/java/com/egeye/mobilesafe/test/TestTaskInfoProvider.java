package com.egeye.mobilesafe.test;

import android.test.AndroidTestCase;

import com.egeye.mobilesafe.domain.TaskInfo;
import com.egeye.mobilesafe.engine.TaskInfoProvider;

import java.util.List;

/**
 * Created by Octavio on 2016/2/11.
 */
public class TestTaskInfoProvider extends AndroidTestCase {

    public void testGetTaskInfos() throws Exception {
        List<TaskInfo> infos = TaskInfoProvider.getTaskInfos(getContext());
        for (TaskInfo info : infos) {
            System.out.println(info.toString());
        }
    }
}
