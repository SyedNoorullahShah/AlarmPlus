package com.alarm.android.alarmplus.ui.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.alarm.android.alarmplus.ui.settings.AlarmSettingsActivity;

public class AlarmSettingsModeDialog extends AppCompatDialogFragment {

    private SettingsDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (AlarmSettingsActivity) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        switch (getTag()) {
            case "label-dialog":
                buildLabelDialog(inflater, builder);
                break;
            case "mode-dialog":
                buildModeDialog(inflater, builder);
                break;
        }

        return builder.create();
    }


    private void buildLabelDialog(LayoutInflater inflater, AlertDialog.Builder builder) {
        View view = inflater.inflate(R.layout.label_dialog, null);
        final EditText labelText = view.findViewById(R.id.edit_label);

        builder.setView(view)
                .setTitle("Alarm Label")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String label = labelText.getText().toString();
                        if (!label.trim().isEmpty()) {
                            listener.setLabel(label);
                        } else {
                            listener.setLabel("Alarm");
                        }

                    }
                });
    }

    private void buildModeDialog(LayoutInflater inflater, AlertDialog.Builder builder) {
        final View view = inflater.inflate(R.layout.mode_dialog, null);
        setModeDialogComponents(view);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioGroup modeGroup = view.findViewById(R.id.mode_group);
                        Bundle data;

                        switch (modeGroup.getCheckedRadioButtonId()) {
                            case R.id.btn_game_mode:
                                data = getGameValues(view);
                                listener.setMode(data);
                                break;

                            case R.id.btn_step_mode:
                                data = getStepValues(view);
                                listener.setMode(data);
                                break;
                        }

                    }
                });

    }

    private Bundle getStepValues(View view) {
        Bundle bundle = new Bundle();

        //step mode
        String mode = "Step Counter";

        //steps
        NumberPicker stepsPicker = view.findViewById(R.id.steps_picker);
        int steps = stepsPicker.getValue();

        //saving data
        bundle.putString("mode", mode);
        bundle.putInt("steps", steps);

        return bundle;
    }


    private Bundle getGameValues(View view) {
        Bundle bundle = new Bundle();

        //game mode
        String mode = "Game";

        //difficulty level
        RadioGroup diffGroup = view.findViewById(R.id.diff_group);
        RadioButton checkedButton = view.findViewById(diffGroup.getCheckedRadioButtonId());
        String diff = checkedButton.getText().toString();

        //rounds
        NumberPicker roundsPicker = view.findViewById(R.id.rounds_picker);
        int rounds = roundsPicker.getValue();

        //saving data
        bundle.putString("mode", mode);
        bundle.putString("level", diff);
        bundle.putInt("rounds", rounds);

        return bundle;
    }

    private void setModeDialogComponents(final View view) {
        RadioGroup modeGroup = view.findViewById(R.id.mode_group);
        modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_game_mode:
                        view.findViewById(R.id.game_section).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.step_section).setVisibility(View.GONE);
                        break;

                    case R.id.btn_step_mode:
                        view.findViewById(R.id.step_section).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.game_section).setVisibility(View.GONE);
                        break;
                }
            }
        });
        NumberPicker roundsPicker = view.findViewById(R.id.rounds_picker);
        roundsPicker.setMinValue(1);
        roundsPicker.setMaxValue(3);

        NumberPicker stepsPicker = view.findViewById(R.id.steps_picker);
        stepsPicker.setMinValue(5);
        stepsPicker.setMaxValue(10);
    }


    public interface SettingsDialogListener {
        void setLabel(String label);
        void setMode(Bundle mode);
    }
}
