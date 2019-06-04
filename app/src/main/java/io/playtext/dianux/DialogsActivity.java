package io.playtext.dianux;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.MessageFormat;

public class DialogsActivity extends AppCompatActivity  implements View.OnClickListener{

    private DialogsActivity dialogsActivity;
    public static final String TAG = "SettingsActivity";

    private int min_count = 2;
    private int max_count = 10;
    private int max_line = 5;
    private String sDefault = "Old text";
    private String stHint = "Write here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        dialogsActivity = this;

        TextView hello_world = findViewById(R.id.textView_dialogs);
        hello_world.setText("Input Dialog & Edit Dialog\n(Base on Android AlertDialog)");
        hello_world.setGravity(Gravity.CENTER);
        hello_world.setTextSize(20);


        Button alert_dialog = findViewById(R.id.alert_dialog);
        alert_dialog.setOnClickListener(this);

        Button input_dialog_with_counter = findViewById(R.id.input_dialog_with_counter);
        input_dialog_with_counter.setOnClickListener(this);

        Button input_dialog_without_counter = findViewById(R.id.input_dialog_without_counter);
        input_dialog_without_counter.setOnClickListener(this);

        Button edit_dialog_with_counter = findViewById(R.id.edit_dialog_with_counter);
        edit_dialog_with_counter.setOnClickListener(this);

        Button edit_dialog_without_counter = findViewById(R.id.edit_dialog_without_counter);
        edit_dialog_without_counter.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.alert_dialog:

                AlertDialog.Builder builder = new AlertDialog.Builder(dialogsActivity);
                builder.setIcon(R.drawable.ic_cloud_upload);
                builder.setTitle(R.string.backup);
                builder.setMessage("Compress and backup My Library to Google Drive App Folder...");
                builder.setPositiveButton(getResources().getText(R.string.backup), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "PositiveButton");

                    }
                });
                builder.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "NegativeButton");

                    }
                });

                builder.create();
                builder.show();
                break;

            case R.id.input_dialog_with_counter:

                AlertDialog input_dialog_with_counter = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_input)
                        .setTitle(R.string.input)
                        .setMessage("Minimum " + min_count + " and maximum " + max_count + " characters.")
                        .setCancelable(false)
                        .setPositiveButton(R.string.submit, null) //Set to null. We override the onclick
                        .setNegativeButton(R.string.cancel, null)
                        .create();

                LinearLayout container1 = new LinearLayout(this);
                container1.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_left_margin);
                params1.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_right_margin);

                final EditText inputText1 = new EditText(this);
                inputText1.setHint(stHint);

                inputText1.setSingleLine();
                inputText1.setLayoutParams(params1);
                inputText1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                inputText1.setSelection(inputText1.getText().length());

                final TextView counter1 = new TextView(this);
                counter1.setText(MessageFormat.format("{0}/{1}", 0, max_count));
                counter1.setLayoutParams(params1);
                counter1.setGravity(Gravity.END);

                container1.addView(inputText1);
                container1.addView(counter1);

                input_dialog_with_counter.setView(container1);

                input_dialog_with_counter.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Log.i(TAG, "onShow: show");
                        final Button button_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                        if (inputText1.getText().length() >= min_count && inputText1.getText().length() <= max_count) {
                            button_pos.setVisibility(View.VISIBLE);
                        } else {
                            button_pos.setVisibility(View.INVISIBLE);
                            counter1.setTextColor(Color.RED);
                        }

                        final int defaultColor = inputText1.getTextColors().getDefaultColor();
                        inputText1.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                Log.i(TAG, "onShow beforeTextChanged: count: " + count);
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                Log.i(TAG, "onShow onTextChanged: start: " + start);

                                counter1.setText(MessageFormat.format("{0}/{1}", count, max_count));

                                if (count >= min_count && count <= max_count) {
                                    button_pos.setVisibility(View.VISIBLE);
                                    inputText1.setTextColor(defaultColor);
                                    counter1.setTextColor(defaultColor);
                                } else {
                                    button_pos.setVisibility(View.INVISIBLE);
                                    inputText1.setTextColor(Color.RED);
                                    counter1.setTextColor(Color.RED);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        button_pos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String mValue = inputText1.getText().toString();
                                Log.i(TAG, "onShow button_pos onClick: mValue: " + mValue);

                                Toast.makeText(dialogsActivity, mValue, Toast.LENGTH_SHORT).show();


                                dialog.dismiss();
                            }
                        });

                    }
                });

                input_dialog_with_counter.show();
                break;

            case R.id.input_dialog_without_counter:

                AlertDialog input_dialog_without_counter = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_input)
                        .setTitle(R.string.input)
                        .setMessage("Unlimited characters.")
                        .setCancelable(false)
                        .setPositiveButton(R.string.submit, null) //Set to null. We override the onclick
                        .setNegativeButton(R.string.cancel, null)
                        .create();

                LinearLayout container2 = new LinearLayout(this);
                container2.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_left_margin);
                params2.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_right_margin);

                final EditText inputText2 = new EditText(this);
                inputText2.setHint(stHint);
                inputText2.setMaxLines(max_line);
                inputText2.setLayoutParams(params2);
                inputText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                inputText2.setSelection(inputText2.getText().length());

                container2.addView(inputText2);

                input_dialog_without_counter.setView(container2);

                input_dialog_without_counter.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Log.i(TAG, "onShow: show");
                        Button button_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button_pos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String mValue = inputText2.getText().toString();
                                Log.i(TAG, "onShow button_pos onClick: mValue: " + mValue);

                                Toast.makeText(dialogsActivity, mValue, Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        });

                    }
                });

                input_dialog_without_counter.show();
                break;

            case R.id.edit_dialog_with_counter:

                AlertDialog edit_dialog_with_counter = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_edit)
                        .setTitle(R.string.edit)
                        .setMessage("Minimum " + min_count + " and maximum " + max_count + " characters.")
                        .setCancelable(false)
                        .setPositiveButton(R.string.submit, null) //Set to null. We override the onclick
                        .setNegativeButton(R.string.cancel, null)
                        .create();

                LinearLayout container3 = new LinearLayout(this);
                container3.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params3.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_left_margin);
                params3.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_right_margin);


                final EditText inputText3 = new EditText(this);
                inputText3.setText(sDefault);
                inputText3.setSingleLine();
                inputText3.setLayoutParams(params3);
                inputText3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                inputText3.setSelection(inputText3.getText().length());

                final TextView counter3 = new TextView(this);
                counter3.setText(MessageFormat.format("{0}/{1}", sDefault.length(), max_count));
                counter3.setLayoutParams(params3);
                counter3.setGravity(Gravity.END);

                container3.addView(inputText3);
                container3.addView(counter3);

                edit_dialog_with_counter.setView(container3);

                edit_dialog_with_counter.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Log.i(TAG, "onShow: show");
                        final Button button_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                        if (inputText3.getText().length() >= min_count && inputText3.getText().length() <= max_count) {
                            button_pos.setVisibility(View.VISIBLE);
                        } else {
                            button_pos.setVisibility(View.INVISIBLE);
                            counter3.setTextColor(Color.RED);
                        }

                        final int defaultColor = inputText3.getTextColors().getDefaultColor();
                        inputText3.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                Log.i(TAG, "onShow beforeTextChanged: count: " + count);
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                Log.i(TAG, "onShow onTextChanged: before: " + before);
                                Log.i(TAG, "onShow onTextChanged: count: " + count);
                                Log.i(TAG, "onShow onTextChanged: count: " + count);

                                counter3.setText(MessageFormat.format("{0}/{1}", inputText3.getText().length(), max_count));

                                if (inputText3.getText().length() >= min_count && inputText3.getText().length() <= max_count) {
                                    button_pos.setVisibility(View.VISIBLE);
                                    inputText3.setTextColor(defaultColor);
                                    counter3.setTextColor(defaultColor);
                                } else {
                                    button_pos.setVisibility(View.INVISIBLE);
                                    inputText3.setTextColor(Color.RED);
                                    counter3.setTextColor(Color.RED);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        button_pos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String mValue = inputText3.getText().toString();
                                Log.i(TAG, "onShow button_pos onClick: mValue: " + mValue);

                                Toast.makeText(dialogsActivity, mValue, Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        });

                    }
                });

                edit_dialog_with_counter.show();
                break;

            case R.id.edit_dialog_without_counter:

                AlertDialog edit_dialog_without_counter = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_edit)
                        .setTitle(R.string.edit)
                        .setMessage("Unlimited characters.")
                        .setCancelable(false)
                        .setPositiveButton(R.string.submit, null) //Set to null. We override the onclick
                        .setNegativeButton(R.string.cancel, null)
                        .create();

                LinearLayout container4 = new LinearLayout(this);
                container4.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params4.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_left_margin);
                params4.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_right_margin);


                final EditText inputText4 = new EditText(this);
                inputText4.setText(sDefault);
                inputText4.setMaxLines(max_line);
                inputText4.setLayoutParams(params4);
                inputText4.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                inputText4.setSelection(inputText4.getText().length());


                container4.addView(inputText4);

                edit_dialog_without_counter.setView(container4);

                edit_dialog_without_counter.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Log.i(TAG, "onShow: show");
                        Button button_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button_pos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String mValue = inputText4.getText().toString();
                                Log.i(TAG, "onShow button_pos onClick: mValue: " + mValue);

                                Toast.makeText(dialogsActivity, mValue, Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        });

                    }
                });

                edit_dialog_without_counter.show();
                break;


            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
