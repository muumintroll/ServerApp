package com.maria.serverapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class MainActivity extends ActionBarActivity {
    private Firebase rootDB;
    private Date t;
    private ListView lv;
    private ArrayList<ChatMessage> messenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        messenger=new ArrayList<ChatMessage>();
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        lv=(ListView)findViewById(R.id.list);
        MyAdapter m=new MyAdapter();
        lv.setAdapter(m);
        rootDB=new Firebase("https://brilliant-inferno-9405.firebaseio.com/");
        rootDB.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ChatMessage current;
                ArrayList<ChatMessage> msgs = new ArrayList<ChatMessage>((int) snapshot.getChildrenCount());
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    current = dataSnapshot1.getValue(ChatMessage.class);
                    messenger.add(current);
                }
                ((MyAdapter)lv.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onClickButton(View view){
        EditText ed=(EditText)findViewById(R.id.edit);
        String text=ed.getText().toString();
        if(text.length()!=0) {
            Firebase messageRef = rootDB.child("messages");
            messageRef.push().setValue(new ChatMessage("Maria", text));
            ed.setText("");
            lv.invalidateViews();
        }
        return true;
    }


    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messenger.size();
        }

        @Override
        public Object getItem(int position) {
            return messenger.get(position%messenger.size()).getmText();
        }

        @Override
        public long getItemId(int position) {
            return position % messenger.size() ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if(convertView!=null ){
                tv=(TextView)convertView;
                ChatMessage message=messenger.get(position % messenger.size());
                tv.setText(message.getmUser()+" : "+message.getmText());
                return tv;
            }else {
                tv = new TextView(MainActivity.this);
                ChatMessage message=messenger.get(position % messenger.size());
                tv.setText(message.getmUser()+" : "+message.getmText());
                Log.d("Run", "Drawing view at position" + position);
                tv.setTextSize(22);
                tv.setPadding(5, 5, 5, 5);
            }
            lv.invalidateViews();
            return tv;
        }
    }
}
