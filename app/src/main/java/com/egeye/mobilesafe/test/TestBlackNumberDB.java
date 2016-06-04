package com.egeye.mobilesafe.test;

import android.test.AndroidTestCase;

import com.egeye.mobilesafe.db.BlackNumberDBHelper;
import com.egeye.mobilesafe.db.BlackNumberDao;
import com.egeye.mobilesafe.domain.BlackNumberInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by Octavio on 2016/2/7.
 */
public class TestBlackNumberDB extends AndroidTestCase {

    public void testCreateDB() throws Exception{
        BlackNumberDBHelper helper = new BlackNumberDBHelper(getContext());
        helper.getWritableDatabase();
    }

    public void testAdd() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        long baseNumber= 1352340567;
        Random random = new Random();
        for(int i =0;i<55;i++){
            dao.add(String.valueOf(baseNumber+i),String.valueOf(random.nextInt(3)+1));
        }
    }

    public void testFindAll() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        List<BlackNumberInfo> infos = dao.findAll();
        for(BlackNumberInfo info : infos){
            System.out.println(info.toString());
        }
    }

    public void testDelete() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.delete("110");
    }

    public void testUpdate() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.update("110","2");
    }

    public void testFind() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        boolean result = dao.find("110");
        assertEquals(true,result);
    }


}
