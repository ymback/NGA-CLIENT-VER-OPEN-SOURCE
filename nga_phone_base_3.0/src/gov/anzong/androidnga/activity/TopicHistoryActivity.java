package gov.anzong.androidnga.activity;

import android.os.Bundle;

import sp.phone.fragment.TopicHistoryFragment;

public class TopicHistoryActivity extends SwipeBackAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,new TopicHistoryFragment()).commit();
    }
}
