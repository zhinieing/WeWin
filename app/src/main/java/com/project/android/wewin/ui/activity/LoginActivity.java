package com.project.android.wewin.ui.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.project.android.wewin.R;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.wilddog.wilddogauth.model.WilddogUser;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 * @author pengming
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    

    private WilddogAuth wilddogAuth;

    @BindView(R.id.login_toolbar)
    Toolbar toolbar;
    
    @BindView(R.id.login_form)
    LinearLayout loginForm;
    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.email_sign_in_button)
    CircularProgressButton mEmailSignInButton;
    @BindView(R.id.email_sign_up_button)
    CircularProgressButton mEmailSignUpButton;
    @BindView(R.id.reset_password)
    TextView resetPassword;
    @BindView(R.id.to_sign_up)
    TextView toSignUp;
    
    @BindView(R.id.signed_in)
    LinearLayout signedIn;
    @BindView(R.id.user_userid)
    TextView userUserid;
    @BindView(R.id.user_email)
    TextView userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        wilddogAuth = WilddogAuth.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        mEmailSignInButton.setOnClickListener(this);
        mEmailSignUpButton.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        toSignUp.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        WilddogUser currentUser = wilddogAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(WilddogUser user) {
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
                signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
                break;

            case R.id.email_sign_up_button:
                createAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
                break;

            case R.id.reset_password:
                resetPassword(mEmailView.getText().toString());
                break;

            case R.id.to_sign_up:
                mEmailSignUpButton.setVisibility(View.VISIBLE);
                mEmailSignInButton.setVisibility(View.GONE);
                resetPassword.setVisibility(View.GONE);
                toSignUp.setVisibility(View.GONE);
                break;

            default:
        }
    }


    private void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mEmailSignUpButton.startAnimation();


        wilddogAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> var1) {
                        if (var1.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            mEmailSignUpButton.doneLoadingAnimation(R.color.colorPrimaryDark,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                            mEmailSignUpButton.setVisibility(View.GONE);
                            mEmailSignInButton.setVisibility(View.VISIBLE);
                            resetPassword.setVisibility(View.VISIBLE);
                            toSignUp.setVisibility(View.VISIBLE);

                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.send_confirm_email_to) + email,
                                    Toast.LENGTH_SHORT).show();

                            WilddogUser user = wilddogAuth.getCurrentUser();
                            user.sendEmailVerification();

                            finish();

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", var1.getException());
                            mEmailSignUpButton.revertAnimation();

                            Toast.makeText(LoginActivity.this, getString(R.string.error_create_user),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mEmailSignInButton.startAnimation();


        wilddogAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> var1) {
                        if (var1.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            mEmailSignInButton.doneLoadingAnimation(R.color.colorPrimaryDark,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                            finish();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", var1.getException());
                            mEmailSignInButton.revertAnimation();

                            Toast.makeText(LoginActivity.this, getString(R.string.error_incorrect_password),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }



    private void resetPassword(final String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_username_required));
            return;
        }

        wilddogAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.send_reset_email_to) + email,
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
        mEmailSignUpButton.dispose();
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

