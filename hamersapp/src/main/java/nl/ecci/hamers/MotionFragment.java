package nl.ecci.hamers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import nl.ecci.hamers.helpers.DataManager;

public class MotionFragment extends Fragment {

    private final String DUURTLANG = "duurt lang";
    private final String ARELAXED = "vet arelaxed";
    private final String NIETCHILL = "niet chill";
    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.motion_fragment, container, false);

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

        JSONObject body = new JSONObject();
        try {
            body.put("motion_type", type);
            body.put("subject", subject);
            body.put("content", content);
        } catch (JSONException ignored) {
        }

        DataManager.postData(this.getContext(), MainActivity.prefs, DataManager.MOTIEURL, null, body);
    }
}