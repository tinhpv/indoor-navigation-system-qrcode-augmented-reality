package fpt.capstone.inqr.fragment;


import androidx.fragment.app.Fragment;

import fpt.capstone.inqr.activity.AppActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    private AppActivity activity;

    public BaseFragment() {
        // Required empty public constructor

    }

    public void setTitle(String title) {
        activity = (AppActivity) getActivity();
        activity.setTitle(title);
    }

    public void showLoadingBar() {
        activity = (AppActivity) getActivity();
        activity.showLoadingBar();
    }

    public void removeLoadingBar() {
        activity = (AppActivity) getActivity();
        activity.removeLoadingBar();
    }

    public void changeFragment(Fragment fragment, boolean isBackStack, boolean isHomeFragment) {
        activity = (AppActivity) getActivity();
        activity.changeFragment(fragment, isBackStack, isHomeFragment);
    }


}
