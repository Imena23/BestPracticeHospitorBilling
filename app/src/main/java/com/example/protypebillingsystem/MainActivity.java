package com.example.protypebillingsystem;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    private final Fragment[] fragments = {
        new HomeFragment(),
        new BillsFragment(),
        new PaymentsFragment(),
        new ProfileFragment()
    };

    private final int[] navIds = {
        R.id.nav_home,
        R.id.nav_bills,
        R.id.nav_payments,
        R.id.nav_profile
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        // Load home fragment by default
        loadFragment(0);

        bottomNav.setOnItemSelectedListener(item -> {
            for (int i = 0; i < navIds.length; i++) {
                if (item.getItemId() == navIds[i]) {
                    loadFragment(i);
                    return true;
                }
            }
            return false;
        });
    }

    private void loadFragment(int index) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragments[index])
            .commit();
    }

    public void navigateToTab(int index) {
        if (index >= 0 && index < navIds.length) {
            bottomNav.setSelectedItemId(navIds[index]);
        }
    }
}
