package com.xiaolian.amigo.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.login.intf.ILoginPresenter;
import com.xiaolian.amigo.ui.login.intf.ILoginView;
import com.xiaolian.amigo.ui.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by caidong on 2017/9/14.
 */

public class LoginActivity extends LoginBaseActivity implements ILoginView {

    @Inject
    ILoginPresenter<ILoginView> presenter;
//    @BindView(R.id.et_mobile)
//    TextView et_mobile;
//    @BindView(R.id.et_userpwd)
//    TextView et_userpwd;

    @BindView(R.id.tv_login)
    TextView tv_login;

    @BindView(R.id.tv_registry)
    TextView tv_registry;

    LoginFragment loginFragment;
    RegisterFragment registerFragment;
    RegisterStep1Fragment registerStep1Fragment;

    private String mobile;
    private String code;

    @Override
    protected void setUp() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registry_group);

        setUnBinder(ButterKnife.bind(this));

        getActivityComponent().inject(this);

        presenter.onAttach(LoginActivity.this);

        if (findViewById(R.id.sv_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sv_container, loginFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @OnClick(R.id.tv_registry)
    void gotoRegister() {
        if (registerStep1Fragment == null) {
            registerStep1Fragment = new RegisterStep1Fragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sv_container, registerStep1Fragment);
        transaction.commit();

        tv_login.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextGray));
        tv_registry.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDark2));

    }

    @OnClick(R.id.tv_login)
    void gotoLogin() {
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sv_container, loginFragment);
        transaction.commit();

        tv_login.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDark2));
        tv_registry.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextGray));
    }

    public void registerSetp2() {
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sv_container, registerFragment);
        transaction.commit();
    }

    @Override
    public void gotoLoginView() {
        gotoLogin();
    }

    @Override
    public void gotoRegisterStep2View() {
        registerSetp2();
    }

    @Override
    public void startTimer() {
        if (registerStep1Fragment != null) {
            registerStep1Fragment.startTimer();
        }
    }

    @Override
    public void gotoMainView() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void sendVerificationCode(String mobile) {
        presenter.getVerification(mobile);
    }

    public void checkVerificationCode(String mobile, String code) {
        presenter.checkVerification(mobile, code);
    }

    public void setMobileAndCode(String mobile, String code) {
        this.mobile = mobile;
        this.code = code;
    }

    public void register(String password, Long schoolId) {
        presenter.register(this.code, this.mobile, password, schoolId);
    }

    public void login(String mobile, String password) {
        presenter.onLoginClick(mobile, password);
    }

}
