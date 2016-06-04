package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.egeye.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Octavio on 2016/2/3.
 */
public class SelectContactActivity extends Activity {

    private ListView lvContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_contact);
        lvContact = (ListView) findViewById(R.id.lv_contact);

        final List<Map<String, String>> data = getContacts();

        lvContact.setAdapter(new SimpleAdapter(this, data,
                R.layout.activity_select_contact_item,
                new String[]{"name", "phone"},
                new int[]{R.id.tv_name, R.id.tv_phone}));

        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNumber = data.get(position).get("phone");
//                String phoneName = data.get(position).get("name");

                Intent i = new Intent();
                i.putExtra("phone",phoneNumber);
//                i.putExtra("name",phoneName);
                setResult(0, i);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    /**
     * 读取手机里面的联系人数据
     * /data/data/com.android.provider.contacts/databases/
     *
     * @return
     */
    private List<Map<String, String>> getContacts() {

        //用来存储联系人
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        //实例化一个内容解析器
        ContentResolver contentResolver = getContentResolver();

        //raw_contacts uri
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");

        Cursor cursor = contentResolver.query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(0);

            if (contactId != null) {
                Map<String, String> map = new HashMap<>();

                Cursor cursorData = contentResolver.query(uriData, new String[]{"data1", "mimetype"}, "contact_id=?", new String[]{contactId}, null);

                while (cursorData.moveToNext()) {
                    String data1 = cursorData.getString(0);

                    String mimetype = cursorData.getString(1);

                    //System.out.println("data1 =="+data1+" mimetype=="+mimetype);

                    if ("vnd.android.cursor.item/name".equals(mimetype)) {

                        //联系人的姓名
                        map.put("name", data1);

                    } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {

                        //联系人的电话号码
                        map.put("phone", data1);

                    }

                }

                list.add(map);
                cursorData.close();
            }

        }

        cursor.close();

        return list;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
