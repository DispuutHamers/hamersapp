package nl.ecci.Hamers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import nl.ecci.Hamers.R;

public class MotionFragment extends Fragment {
    private RadioGroup motionRadioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.motion_fragment, container, false);


        // TODO: Get radiobutton value

        return view;
    }

}