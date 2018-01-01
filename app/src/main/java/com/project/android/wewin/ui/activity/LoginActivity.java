package com.project.android.wewin.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.data.remote.model.MyUser;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * A login screen that offers login via email/password.
 *
 * @author pengming
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean passwordLogin = true;
    private boolean signIn = true;

    @BindView(R.id.login_toolbar)
    Toolbar toolbar;
    @BindView(R.id.login_phone)
    EditText loginPhone;
    @BindView(R.id.ll_password)
    LinearLayout llPassword;
    @BindView(R.id.login_password)
    EditText loginPassword;


    @BindView(R.id.ll_confirm)
    LinearLayout llConfirm;
    @BindView(R.id.login_confirm)
    EditText loginConfirm;
    @BindView(R.id.login_get_confirm)
    Button loginGetConfirm;

    @BindView(R.id.phone_sign_in_button)
    CircularProgressButton phoneSignInButton;
    @BindView(R.id.phone_sign_up_button)
    CircularProgressButton phoneSignUpButton;

    @BindView(R.id.confirm_login)
    TextView confirmLogin;
    @BindView(R.id.to_sign_up)
    TextView toSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        initView();

    }


    private void initView() {
        loginGetConfirm.setOnClickListener(this);
        phoneSignInButton.setOnClickListener(this);
        phoneSignUpButton.setOnClickListener(this);
        confirmLogin.setOnClickListener(this);
        toSignUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_get_confirm:
                new MyCountDownTimer(60000, 1000).start();
                getConfirmSMS(loginPhone.getText().toString());
                break;
            case R.id.phone_sign_in_button:
                if (passwordLogin) {
                    signIn(loginPhone.getText().toString(), loginPassword.getText().toString());
                } else {
                    signIn(loginPhone.getText().toString(), loginConfirm.getText().toString());
                }
                break;
            case R.id.phone_sign_up_button:
                signUp(loginPhone.getText().toString(), loginPassword.getText().toString(), loginConfirm.getText().toString());
                break;
            case R.id.confirm_login:
                passwordLogin = !passwordLogin;
                if (passwordLogin) {
                    confirmLogin.setText(R.string.confirm_login);
                    llConfirm.setVisibility(View.GONE);
                    llPassword.setVisibility(View.VISIBLE);
                } else {
                    confirmLogin.setText(R.string.password_login);
                    llConfirm.setVisibility(View.VISIBLE);
                    llPassword.setVisibility(View.GONE);
                }
                break;
            case R.id.to_sign_up:
                llPassword.setVisibility(View.VISIBLE);
                llConfirm.setVisibility(View.VISIBLE);
                phoneSignUpButton.setVisibility(View.VISIBLE);
                phoneSignInButton.setVisibility(View.GONE);
                confirmLogin.setVisibility(View.GONE);
                toSignUp.setVisibility(View.GONE);
                break;
            default:
        }
    }

    private void getConfirmSMS(String phone) {

        BmobSMS.requestSMSCode(phone, "一键注册或登录模板", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    Toast.makeText(LoginActivity.this, getString(R.string.send_sms_success),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn(String phone, String password) {

        if (passwordLogin) {
            if (!validatePasswordForm()) {
                return;
            }

            phoneSignInButton.startAnimation();

            BmobUser.loginByAccount(phone, password, new LogInListener<MyUser>() {
                @Override
                public void done(MyUser user, BmobException e) {
                    if (e == null) {

                        phoneSignInButton.doneLoadingAnimation(R.color.colorPrimaryDark,
                                BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                        finish();

                    } else {
                        phoneSignInButton.revertAnimation();

                        Toast.makeText(LoginActivity.this, getString(R.string.error_incorrect_password),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            if (!validateCodeForm()) {
                return;
            }

            phoneSignInButton.startAnimation();

            BmobUser.loginBySMSCode(phone, password, new LogInListener<MyUser>() {
                @Override
                public void done(MyUser user, BmobException e) {
                    if (e == null) {

                        phoneSignInButton.doneLoadingAnimation(R.color.colorPrimaryDark,
                                BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                        finish();


                    } else {
                        phoneSignInButton.revertAnimation();

                        Toast.makeText(LoginActivity.this, getString(R.string.error_incorrect_confirm),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void signUp(String phone, String password, String code) {

        if (!validatePasswordForm() || !validateCodeForm()) {
            return;
        }

        phoneSignUpButton.startAnimation();

        MyUser user = new MyUser();
        user.setMobilePhoneNumber(phone);
        user.setPassword(password);
        user.signOrLogin(code, new SaveListener<MyUser>() {
            @Override
            public void done(MyUser user, BmobException e) {
                if (e == null) {

                    phoneSignUpButton.doneLoadingAnimation(R.color.colorPrimaryDark,
                            BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                    finish();


                } else {
                    phoneSignUpButton.revertAnimation();

                    Toast.makeText(LoginActivity.this, getString(R.string.error_create_user),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validatePasswordForm() {
        boolean valid = true;

        String password = loginPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError(getString(R.string.error_password_required));
            valid = false;
        } else {
            loginPassword.setError(null);
        }

        return valid;
    }

    private boolean validateCodeForm() {
        boolean valid = true;

        String code = loginConfirm.getText().toString();
        if (TextUtils.isEmpty(code)) {
            loginConfirm.setError(getString(R.string.error_confirm_required));
            valid = false;
        } else {
            loginConfirm.setError(null);
        }

        return valid;
    }


    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {

            loginGetConfirm.setClickable(false);
            loginGetConfirm.setText(l/1000+"s");

        }

        @Override
        public void onFinish() {

            loginGetConfirm.setText(getString(R.string.prompt_get_confirm));
            loginGetConfirm.setClickable(true);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        phoneSignInButton.dispose();
        phoneSignUpButton.dispose();
    }


    //wilddog login start

    /*private WilddogAuth wilddogAuth;
    wilddogAuth = WilddogAuth.getInstance();
    WilddogUser currentUser = wilddogAuth.getCurrentUser();*/

    /*@BindView(R.id.email)
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
    TextView toSignUp;*/

    /*case R.id.email_sign_in_button:
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
                break;*/

    /*mEmailSignInButton.setOnClickListener(this);
        mEmailSignUpButton.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        toSignUp.setOnClickListener(this);*/


    /*private void createAccount(final String email, String password) {
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
    }*/

    /*mEmailSignInButton.dispose();
        mEmailSignUpButton.dispose();*/

    //wilddog login end




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

