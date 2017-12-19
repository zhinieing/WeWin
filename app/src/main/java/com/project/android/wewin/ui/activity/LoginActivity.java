package com.project.android.wewin.ui.activity;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.android.wewin.R;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private static final String USER_NAME = "username";

    private FirebaseAuth mAuth;

    @BindView(R.id.login_form)
    ScrollView loginForm;
    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.email_sign_in_button)
    CircularProgressButton mEmailSignInButton;
    @BindView(R.id.reset_password)
    TextView resetPassword;
    @BindView(R.id.login_toolbar)
    Toolbar toolbar;

    @BindView(R.id.signed_in)
    LinearLayout signedIn;
    @BindView(R.id.user_userid)
    TextView userUserid;
    @BindView(R.id.user_email)
    TextView userEmail;
    @BindView(R.id.sign_out_button)
    Button signOutButton;
    @BindView(R.id.verify_email_button)
    Button verifyEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = getSharedPreferences("userdata", MODE_PRIVATE);
        if (pref.getString(USER_NAME, "") == "") {
            mEmailSignInButton.setText(R.string.action_sign_up);
        } else {
            mEmailSignInButton.setText(R.string.action_sign_in);
            resetPassword.setVisibility(View.VISIBLE);
            mEmailView.setText(pref.getString(USER_NAME, ""));
        }

        mEmailSignInButton.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        verifyEmailButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            loginForm.setVisibility(View.GONE);
            signedIn.setVisibility(View.VISIBLE);

            userUserid.setText(user.getUid());
            userEmail.setText(user.getEmail());
        } else {
            loginForm.setVisibility(View.VISIBLE);
            signedIn.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                if (getResources().getString(R.string.action_sign_up)
                        .equals(mEmailSignInButton.getText().toString())) {
                    createAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
                } else {
                    signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
                }
                break;

            case R.id.reset_password:
                resetPassword(mEmailView.getText().toString());
                break;

            case R.id.sign_out_button:
                signOut();
                break;

            case R.id.verify_email_button:
                sendEmailVerification();
                break;

            default:
        }
    }


    private void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mEmailSignInButton.startAnimation();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            mEmailSignInButton.doneLoadingAnimation(R.color.colorPrimaryDark,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                            SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                            editor.putString(USER_NAME, email);
                            editor.apply();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mEmailSignInButton.revertAnimation();

                            Toast.makeText(LoginActivity.this, getString(R.string.error_create_user),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mEmailSignInButton.startAnimation();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mEmailSignInButton.doneLoadingAnimation(R.color.colorPrimaryDark,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            mEmailSignInButton.revertAnimation();

                            Toast.makeText(LoginActivity.this, getString(R.string.error_incorrect_password),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        verifyEmailButton.setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        verifyEmailButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.send_email_to) + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(LoginActivity.this, getString(R.string.send_email_fail),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void resetPassword(final String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_username_required));
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.send_email_to) + email,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendPasswordResetEmail", task.getException());
                            Toast.makeText(LoginActivity.this, getString(R.string.send_email_fail),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_username_required));
            valid = false;
        } else {
            mEmailView.setError(null);
        }

        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_password_required));
            valid = false;
        } else {
            mPasswordView.setError(null);
        }

        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mEmailSignInButton.dispose();
    }




    /*EventBus.getDefault().register(this);
    //EventBus.getDefault().unregister(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });*/

    /*private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_password_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mEmailView.setError(getString(R.string.error_username_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isValid(username)) {
            mEmailView.setError(getString(R.string.error_invalid));
            focusView = mEmailView;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            mEmailSignInButton.startAnimation();

            String userName = mEmailView.getText().toString();
            String passWord = mPasswordView.getText().toString();
            User user = new User(userName, passWord);
            EventBus.getDefault().post(user);
        }
    }

    private boolean isValid(String word) {
        Pattern p = Pattern.compile("^[A-Za-z0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(word);
        return m.matches();
    }*/


    /*@Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUserEvent(User event) {
        Boolean mStatus = false;

        try {
            InputStream is = this.getAssets().open("test.json");
            BufferedReader bufr = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = bufr.readLine()) != null) {
                builder.append(line);
            }
            is.close();
            bufr.close();


            JSONArray jsonArray = new JSONArray(builder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (event.getUserName().equals(jsonObject.getString("username"))) {
                    mStatus = event.getPassWord().equals(jsonObject.getString("password"));
                    break;
                }
            }

            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new LoginEvent(mStatus));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(LoginEvent event) {

        if (event.getmStatus()) {
            mEmailSignInButton.doneLoadingAnimation(R.color.colorPrimaryDark,
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

            finish();
        } else {
            mEmailSignInButton.revertAnimation();

            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }*/


}

