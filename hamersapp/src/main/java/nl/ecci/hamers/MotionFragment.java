package nl.ecci.hamers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.helpers.DataManager;

public class MotionFragment extends Fragment {

    private String type;
    private SharedPreferences prefs;

    private final String DUURTLANG = "duurt lang";
    private final String ARELAXED = "vet arelaxed";
    private final String NIETCHILL = "niet chilll";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.motion_fragment, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.motionradiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_duurtlang:
                        type = DUURTLANG;
                        break;

                    case R.id.radio_arelaxed:
                        type = ARELAXED;
                        break;

                    case R.id.radio_nietchill:
                        type = NIETCHILL;
                        break;
                }
            }
        });

        Button button = (Button) view.findViewById(R.id.sendmotion_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MotionFragment.this.postMotion();
            }
        });

        return view;
    }

    private void postMotion() {
        EditText motion_subject = (EditText) getActivity().findViewById(R.id.motion_subject);
        EditText motion_content = (EditText) getActivity().findViewById(R.id.motion_content);

        String subject = motion_subject.getText().toString();
        String content = motion_content.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("motion[motion_type]", type);
        params.put("motion[subject]", subject);
        params.put("motion[content]", content);

        DataManager.postData(this.getContext(), prefs, DataManager.MOTIEURL, null, params);
    }
}