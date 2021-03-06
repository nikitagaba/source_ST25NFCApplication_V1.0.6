/*
  * @author STMicroelectronics MMY Application team
  *
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2017 STMicroelectronics</center></h2>
  *
  * Licensed under ST MIX_MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/Mix_MyLiberty
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
  * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
*/

package com.st.st25nfc.type5.st25dv02kw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration;
import com.st.st25sdk.type5.ST25DV02KWTag;

public class ST25DV02KWPwmCtrlConfigurationFragment extends STFragment {

    public ST25DV02KWTag myTag = null;
    static final String TAG = "PwmCtrlConfig";

    private View mView;

    // Memory Conf grid
    private final int ROW1 = 1;
    private final int PWM1_CONF_COLUMN = 1;
    private final int PWM2_CONF_COLUMN = 2;


    public static ST25DV02KWPwmCtrlConfigurationFragment newInstance(Context context) {
        ST25DV02KWPwmCtrlConfigurationFragment f = new ST25DV02KWPwmCtrlConfigurationFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.configuration));


        return f;
    }

    public ST25DV02KWPwmCtrlConfigurationFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_st25dv02kw_pwm_ctrl_config, container, false);

        mView = view;
        fillView();

        Button changePwmDriveConfigButton = (Button) mView.findViewById(R.id.changePwmDriveConfigButton);
        changePwmDriveConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25DV02KWChangePwmDriveConfigActivity.class);
                startActivity(intent);
            }
        });

        Button changeDualityManagementButton = (Button) mView.findViewById(R.id.changeDualityManagementButton);
        changeDualityManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25DV02KWChangePwmDualityManagementActivity.class);
                startActivity(intent);
            }
        });

        Button presentPasswordsButton = (Button) mView.findViewById(R.id.presentPasswordsButton);
        presentPasswordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentPassword();
            }
        });

        Button changePasswordsButton = (Button) mView.findViewById(R.id.changePasswordsButton);
        changePasswordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        return (View) view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getPwmCtrlAccessRights();
    }

    /**
     * Get the number of areas and the permission of each one.
     */
    private void getPwmCtrlAccessRights() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming pwm1Value = myTag.getPwm1OutputDriverTrimming();
                    final ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming pwm2Value = myTag.getPwm2OutputDriverTrimming();
                    final ST25DV02KWRegisterPwmRfConfiguration.DualityManagement dualityMgtValue = myTag.getDualityManagement();

                    // Update the GridArray (this should be done by the UI thread)
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            refreshPwmDriveGrid(pwm1Value, pwm2Value);
                            refreshPwmDualityMgtGrid(dualityMgtValue);
                        }
                    });

                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_reading_the_tag);
                    }
                }
            }
        }).start();
    }


    private void refreshPwmDriveGrid(ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming pwm1_value, ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming pwm2_value) {


        GridLayout gridLayout = (GridLayout) mView.findViewById(R.id.pwmCtrlPwmDriveGridLayout);

        gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(3); // +1 because there is the table header

        // Add header on row 0
        addTextToGridLayoutCell(gridLayout, 0, PWM1_CONF_COLUMN, "PWM1_DRIVE");
        addTextToGridLayoutCell(gridLayout, 0, PWM2_CONF_COLUMN, "PWM2_DRIVE");

        addTextToGridLayoutCell(gridLayout, ROW1, 0, getResources().getString(R.string.trimming_value));

        switch (pwm1_value) {
            case FULL_POWER:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.full_power));
                break;
            case ONE_QUARTER_FULL_POWER:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.one_quarter_power));
                break;
            case HALF_FULL_POWER:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.half_power));
                break;
            case THREE_QUARTER_FULL_POWER:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.three_quarter_power));
                break;
            case UNKNOWN:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.unknown));
                break;
        }

        switch (pwm2_value) {
            case FULL_POWER:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM2_CONF_COLUMN, getResources().getString(R.string.full_power));
                break;
            case ONE_QUARTER_FULL_POWER:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM2_CONF_COLUMN, getResources().getString(R.string.one_quarter_power));
                break;
            case HALF_FULL_POWER:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM2_CONF_COLUMN, getResources().getString(R.string.half_power));
                break;
            case THREE_QUARTER_FULL_POWER:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM2_CONF_COLUMN, getResources().getString(R.string.three_quarter_power));
                break;
            case UNKNOWN:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM2_CONF_COLUMN, getResources().getString(R.string.unknown));
                break;
        }
    }

    private void refreshPwmDualityMgtGrid(ST25DV02KWRegisterPwmRfConfiguration.DualityManagement dualityManagement) {


        GridLayout gridLayout = (GridLayout) mView.findViewById(R.id.pwmCtrlPwmDualityMgtGridLayout);

        gridLayout.removeAllViews();
        gridLayout.setColumnCount(2);
        gridLayout.setRowCount(3); // +1 because there is the table header

        // Add header on row 0
        addTextToGridLayoutCell(gridLayout, 0, PWM1_CONF_COLUMN, "Duality management");
        //addTextToGridLayoutCell(gridLayout, ROW1, 0, getResources().getString(R.string.trimming_value) );
        addTextToGridLayoutCell(gridLayout, ROW1, 0, "PWM & RF management");

        switch (dualityManagement) {
            case FULL_DUPLEX:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.full_duplex));
                break;
            case PWM_FREQ_REDUCED:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.freq_reduced));
                break;
            case PWM_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.full_power_while_rf_cmd));
                break;
            case PWM_IN_HZ_WHILE_RF_CMD:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.pwm_set_in_hz_while_rf_cmd));
                break;
            case PWM_FREQ_REDUCED_AND_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.pwm_set_in_hz_while_rf_cmd) + "And" +
                        getResources().getString(R.string.freq_reduced));
                break;
            case UNKNOWN_MGT:
                addTextToGridLayoutCell(gridLayout, ROW1, PWM1_CONF_COLUMN, getResources().getString(R.string.unknown_mgt));
                break;
        }
    }

    private void addTextToGridLayoutCell(GridLayout gridLayout, int x, int y, String txt) {

        TextView myTextView = new TextView(getActivity());
        myTextView.setText(txt);
        //myTextView.setBackgroundColor(0x888888);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setGravity(Gravity.CENTER);
        //layoutParams.setGravity(Gravity.FILL_HORIZONTAL);
        //layoutParams.setGravity(Gravity.RIGHT);
        layoutParams.rightMargin = 20;
        layoutParams.topMargin = 20;
        layoutParams.columnSpec = GridLayout.spec(y);
        layoutParams.rowSpec = GridLayout.spec(x);

        gridLayout.addView(myTextView, layoutParams);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        myTag = (ST25DV02KWTag) ((STFragmentListener) context).getTag();

    }

    public void fillView() {
        class ContentView implements Runnable {

            TextView mTagNameTextView;
            TextView mTagDescriptionTextView;
            TextView mTagTypeTextView;

            String mTagName;
            String mTagDescription;
            String mTagType;

            private void fillView() {

                if (myTag != null) {
                    mTagName = ((NFCTag) myTag).getName();
                    mTagDescription = ((NFCTag) myTag).getDescription();
                    mTagType = ((NFCTag) myTag).getTypeDescription();
                }
                if (mView != null) {
                    mTagNameTextView = (TextView) mView.findViewById(R.id.model_header);
                    mTagTypeTextView = (TextView) mView.findViewById(R.id.model_type);
                    mTagDescriptionTextView = (TextView) mView.findViewById(R.id.model_description);
                }
            }

            @Override
            public void run() {
                fillView();

                if (mView != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mTagNameTextView.setText(mTagName);
                            mTagTypeTextView.setText(mTagDescription);
                            mTagDescriptionTextView.setText(mTagType);
                        }
                    });
                }
            }
        }

        new Thread(new ContentView()).start();
    }

    private void presentPassword() {
        new Thread(new Runnable() {
            public void run() {
                byte passwordNumber = ST25DV02KWTag.ST25DV02KW_CONFIGURATION_PASSWORD_ID;
                STType5PwdDialogFragment.STPwdAction pwdAction = STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD;
                String message = "Enter configuration password";

                Log.v(TAG, "presentPassword");

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(pwdAction, passwordNumber, message, new STType5PwdDialogFragment.STType5PwdDialogListener() {

                    @Override
                    public void onSTType5PwdDialogFinish(int result) {
                        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
                        if (result == PwdDialogFragment.RESULT_OK) {
                            showToast(R.string.present_pwd_succeeded);
                        } else {
                            Log.e(TAG, "Action failed! Tag not updated!");
                        }
                    }
                });
                pwdDialogFragment.show(getActivity().getSupportFragmentManager(), "pwdDialogFragment");
            }
        }).start();
    }

    private void changePassword() {
        new Thread(new Runnable() {
            public void run() {
                byte passwordNumber = ST25DV02KWTag.ST25DV02KW_PWM_PASSWORD_ID;
                STType5PwdDialogFragment.STPwdAction pwdAction = STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD;
                String message = "Enter pwm password";

                Log.v(TAG, "enterOldPassword");

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(pwdAction, passwordNumber, message, new STType5PwdDialogFragment.STType5PwdDialogListener() {

                    @Override
                    public void onSTType5PwdDialogFinish(int result) {
                        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
                        if (result == PwdDialogFragment.RESULT_OK) {
                            enterNewPassword();
                        } else {
                            Log.e(TAG, "Action failed! Tag not updated!");
                        }
                    }
                });
                pwdDialogFragment.show(getActivity().getSupportFragmentManager(), "pwdDialogFragment");

            }
        }).start();
    }


    private void enterNewPassword() {
        new Thread(new Runnable() {
            public void run() {
                byte passwordNumber = ST25DV02KWTag.ST25DV02KW_PWM_PASSWORD_ID;
                STType5PwdDialogFragment.STPwdAction pwdAction = STType5PwdDialogFragment.STPwdAction.ENTER_NEW_PWD;
                String message = "Enter new pwm password";
                Log.v(TAG, "enterNewPassword");

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(pwdAction, passwordNumber, message, new STType5PwdDialogFragment.STType5PwdDialogListener() {

                    @Override
                    public void onSTType5PwdDialogFinish(int result) {
                        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
                        if (result == PwdDialogFragment.RESULT_OK) {
                            showToast(R.string.present_pwd_succeeded);
                        } else {
                            Log.e(TAG, "Action failed! Tag not updated!");
                        }
                    }
                });
                pwdDialogFragment.show(getActivity().getSupportFragmentManager(), "pwdDialogFragment");
            }
        }).start();
    }
}


