package fpt.capstone.inqr.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.fragment.ListBuildingFragment;

public class AppActivity extends AppCompatActivity {

    private ImageView imgNavigation;
    private TextView tvTitle;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        setView();

        changeFragment(new ListBuildingFragment(), false, true);
    }

    private void setView() {
        imgNavigation = findViewById(R.id.imgNavigation);
        tvTitle = findViewById(R.id.toolbar_title);
        frameLayout = findViewById(R.id.bgLoading);
    }

    public void showLoadingBar() {
        frameLayout.setVisibility(View.VISIBLE);
    }

    public void removeLoadingBar() {
        frameLayout.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void changeFragment(Fragment fragment, boolean isBackStack, final boolean isHomeFragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (!isHomeFragment) {


//            imgNavigation.setImageResource(R.drawable.ic_baseline_arrow_back_24px);
            imgNavigation.setVisibility(View.VISIBLE);
        } else {
//            imgNavigation.setImageResource(R.drawable.ic_baseline_sort_24px);
            imgNavigation.setVisibility(View.INVISIBLE);
        }

        if (isHomeFragment) {
            fragmentTransaction.add(R.id.container, fragment, "home_fragment");
        } else {
            fragmentTransaction.add(R.id.container, fragment);
        }

        if (isBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commit();

        imgNavigation.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (checkHomeFragment()) {
//            imgNavigation.setImageResource(R.drawable.ic_baseline_sort_24px);
            imgNavigation.setVisibility(View.INVISIBLE);
        } else {
            imgNavigation.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkHomeFragment() {
        Fragment fragment = this.getSupportFragmentManager().findFragmentByTag("home_fragment");
        return (fragment != null && fragment.isVisible());
    }
}
