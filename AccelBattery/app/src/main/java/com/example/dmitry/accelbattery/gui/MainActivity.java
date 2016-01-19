package com.example.dmitry.accelbattery.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.dmitry.accelbattery.utils.Events;
import com.example.dmitry.accelbattery.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private boolean isSleeping = false;


    public void onEvent(Events.PhoneSleptEvent event) {
        image.setImageResource(R.drawable.sleep_icon);
        isSleeping = true;
    }

    public void onEvent(Events.PhoneActivatedEvent event) {
        image.setImageResource(R.drawable.ic_launcher);
        isSleeping = false;
    }

    @Bind(R.id.image_state)
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().registerSticky(this);
    }

    @OnClick(R.id.image_state)
    void onImageClick() {
        isSleeping = !isSleeping;
        if (isSleeping) {
            EventBus.getDefault().postSticky(new Events.PhoneSleptEvent());
        } else {
            EventBus.getDefault().postSticky(new Events.PhoneActivatedEvent());
        }
    }
}
