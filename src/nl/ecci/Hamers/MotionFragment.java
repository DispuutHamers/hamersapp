package nl.ecci.Hamers;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import nl.ecci.Hamers.Helpers.SendPostRequest;

public class MotionFragment extends Fragment {
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
                        type = "duurt lang";
                        break;

                    case R.id.radio_arelaxed:
                        type = "vet arelaxed";
                        break;

                    case R.id.radio_nietchill:
                        type = "niet chill";
                        break;
                }
            }
        });

        Button button = (Button) view.findViewById(R.id.sendmotion_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                postMotion();
            }
        });

        return view;
    }

    public void postMotion() {
        EditText motion_subject = (EditText) getActivity().findViewById(R.id.motion_subject);
        EditText motion_content = (EditText) getActivity().findViewById(R.id.motion_content);

        String subject = motion_subject.getText().toString();
        String content = motion_content.getText().toString();

        String arguments = "motion[motion_type]=" + type + "&motion[subject]=" + subject + "&motion[content]=" + content;
        SendPostRequest req = new SendPostRequest(this.getActivity(), SendPostRequest.MOTIEURL, PreferenceManager.getDefaultSharedPreferences(this.getActivity()), arguments);
        req.execute();
    }
}