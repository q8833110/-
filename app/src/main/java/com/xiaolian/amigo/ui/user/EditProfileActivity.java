package com.xiaolian.amigo.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.network.model.common.ApplySchoolCheckRespDTO;
import com.xiaolian.amigo.data.network.model.login.WeChatBindRespDTO;
import com.xiaolian.amigo.data.vo.User;
import com.xiaolian.amigo.ui.user.intf.IEditProfilePresenter;
import com.xiaolian.amigo.ui.user.intf.IEditProfileView;
import com.xiaolian.amigo.ui.widget.dialog.AvailabilityDialog;
import com.xiaolian.amigo.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;

import static com.xiaolian.amigo.util.Constant.CERTIFICATION_FAILURE;
import static com.xiaolian.amigo.util.Constant.CERTIFICATION_NONE;
import static com.xiaolian.amigo.util.Constant.CERTIFICATION_PASS;
import static com.xiaolian.amigo.util.Constant.CERTIFICATION_REVIEWING;
import static com.xiaolian.amigo.util.Constant.USER_INFO_ACTIVITY_SRC;

/**
 * 编辑个人信息Activity
 *
 * @author zcd
 * @date 17/9/15
 */

public class EditProfileActivity extends UserBaseActivity implements IEditProfileView {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHECK_PASSWORD = 0x0101;
    private static final int REQUEST_CODE_EDIT_NICKNAME = 0x0102;
    private static final int REQUEST_CODE_EDIT_SCHOOL = 0x0103;
    private static final int REQUEST_CODE_EDIT_DORMITORY = 0x0104;
    private static final int REQUEST_CODE_EDIT_SEX = 0x0105;
    private static final int REQUEST_CODE_EDIT_AVATAR = 0x0106;


    private static final String CERTIFICATION_NONE_TXT = "未认证" ;
    private static final String CERTIFICATION_REVIEWING_TXT = "审核中";
    private static final String CERTIFICATION_PASS_TXT = "已认证" ;
    private static final String CERTIFICATION_FAILURE_TXT = "认证失败";

    public static final int ALIPAY_BIND = 0;
    public static final int WECHAT_BIND = 1;
    @Inject
    IEditProfilePresenter<IEditProfileView> presenter;

    /**
     * 宿舍
     */
    @BindView(R.id.tv_residence)
    TextView tvResidence;

    /**
     * 学校
     */
    @BindView(R.id.tv_school)
    TextView tvSchool;

    /**
     * 手机
     */
    @BindView(R.id.tv_mobile)
    TextView tvMobile;

    /**
     * 性别
     */
    @BindView(R.id.tv_sex)
    TextView tvSex;

    /**
     * 昵称
     */
    @BindView(R.id.tv_nickname)
    TextView tvNickname;

    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;

    /**
     * 浴室密码提示
     */
    @BindView(R.id.tv_bathroom_tip)
    TextView tvBathroomTip;

    @BindView(R.id.rel_edit_bathroom_password)
    RelativeLayout relEditBathroomPassword;
    @BindView(R.id.text_certification)
    TextView textCertification;
    @BindView(R.id.tv_certification)
    TextView tvCertification;
    @BindView(R.id.rel_edit_certification)
    RelativeLayout relEditCertification;

    @BindView(R.id.rel_wechat_bind)
    RelativeLayout rl_wechat;

    @BindView(R.id.rel_pay_bind)
    RelativeLayout rl_alipay;

    @BindView(R.id.tv_wechat_tip)
    TextView tv_wechat;

    @BindView(R.id.tv_apay_tip)
    TextView tv_alipay;

    private String avatarUrl;

    private boolean isNeedRefresh;

    private AvailabilityDialog availabilityDialog;


    private Class<? extends Activity> activityClazz ;

    private String weChatNickName = null;
    private String aliPayNickName = null;
    private String userPhone = "";

    @Override
    protected void setUp() {

    }

    @Override
    protected void initView() {
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        presenter.onAttach(EditProfileActivity.this);
        setMainBackground(R.color.white);
    }




    @Override
    protected int setTitle() {
        return R.string.edit_profile;
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_edit_profile;
    }

    private void getCertificationStatus(int statusType){
        String status="" ;

        switch (statusType){
            case CERTIFICATION_NONE:
                status = CERTIFICATION_NONE_TXT ;
                activityClazz = UserCertificationActivity.class ;
                break;
            case CERTIFICATION_FAILURE:
                status = CERTIFICATION_NONE_TXT ;
                activityClazz = UserCertificationStatusActivity.class ;
                break;
            case CERTIFICATION_PASS:
                status = CERTIFICATION_PASS_TXT ;
                activityClazz = UserCertificationStatusActivity.class;
                break;
            case CERTIFICATION_REVIEWING:
                status = CERTIFICATION_NONE_TXT;
                activityClazz = UserCertificationStatusActivity.class ;
                break;
            default:
                break;

        }
        tvCertification.setText(status);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
        EventBus.getDefault().unregister(this);

    }


    public void onclick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rel_edit_avatar:
                intent = new Intent(this, EditAvatarActivity.class);
                intent.putExtra(EditAvatarActivity.INTENT_KEY_CURRENT_AVATAR, avatarUrl);
                startActivityForResult(intent, REQUEST_CODE_EDIT_AVATAR);
                break;
            case R.id.rel_edit_nickname:
                intent = new Intent(this, EditNickNameActivity.class);
                intent.putExtra(Constant.EXTRA_KEY, new EditNickNameActivity.Model(tvNickname.getText().toString().trim()));
                startActivityForResult(intent, REQUEST_CODE_EDIT_NICKNAME);
                break;
            case R.id.rel_edit_sex:
                intent = new Intent(this, ListChooseActivity.class);
                if (!TextUtils.isEmpty(tvSex.getText())) {
                    intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_SEX_TYPE,
                            TextUtils.equals(tvSex.getText(), "男") ? 1 : 2);
                }
                intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_ACTION,
                        ListChooseActivity.ACTION_LIST_SEX);

                startActivityForResult(intent, REQUEST_CODE_EDIT_SEX);
                break;
            case R.id.rel_edit_mobile:
                intent = new Intent(this, PasswordVerifyActivity.class);
                intent.putExtra("phoneNumber",userPhone);
                intent.putExtra("type",PasswordVerifyActivity.TYPE_CHANGE_PHONE);
                startActivity(intent);

                break;
            case R.id.rel_edit_password:
                intent = new Intent(this, EditPasswordActivity.class);
                intent.putExtra("nickName", "");
                startActivityForResult(intent, 1);
                break;
            case R.id.rel_edit_bathroom_password:
                intent = new Intent(this, FindBathroomPasswordActivity.class);
                intent.putExtra(Constant.EXTRA_KEY, presenter.isHadSetBathPassword());
                startActivityForResult(intent, 1);
                break;
            case R.id.rel_edit_school:
                isNeedRefresh = true;
                presenter.checkChangeSchool();
                break;
            case R.id.rel_edit_room:
                isNeedRefresh = true;
                intent = new Intent(this, ListChooseActivity.class);
                intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_IS_EDIT, false);
                intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_ACTION,
                        ListChooseActivity.ACTION_LIST_BUILDING);
                intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_SRC_ACTIVITY, USER_INFO_ACTIVITY_SRC);
                intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_DEVICE_TYPE, Device.HEATER.getType());
                startActivity(intent);
                break;
            case R.id.rel_edit_certification:
                if (activityClazz != null) startActivity(this ,activityClazz);
                break;
            case R.id.rel_wechat_bind:
                if (!TextUtils.isEmpty(weChatNickName)) {
                    gotoThirdLoginDetail(WECHAT_BIND, weChatNickName);
                    isNeedRefresh = true;
                }else {
                    presenter.getWeChatCode();
                }
                break;
            case R.id.rel_pay_bind:
                if (!TextUtils.isEmpty(aliPayNickName)) {
                    gotoThirdLoginDetail(ALIPAY_BIND, aliPayNickName);
                    isNeedRefresh = true;
                }else {
                    presenter.getAlipayAuthInfo();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void gotoThirdLoginDetail(int type,String nickName) {
        //0 是支付宝详情 1 是微信详情
        Intent intent = new Intent(this,ThirdBindActivity.class);
        intent.putExtra("third_login",type);
        intent.putExtra("nick_name",nickName);
        startActivity(intent);
    }

    @Override
    public void showChangeSchoolDialog() {
        if (null == availabilityDialog) {
            availabilityDialog = new AvailabilityDialog(this);
        }
        if (availabilityDialog.isShowing()) {
            return;
        }
        availabilityDialog.setOkText(getString(R.string.confirm));
        availabilityDialog.setTitle("更换学校");
        availabilityDialog.setTip(getString(R.string.change_school_tip));
        availabilityDialog.setOnOkClickListener(dialog1 -> {
//            Intent intent = new Intent(this, ListChooseActivity.class);
//            intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_ACTION,
//                    ListChooseActivity.ACTION_LIST_SCHOOL);
//            startActivityForResult(intent, REQUEST_CODE_EDIT_SCHOOL);

            Intent intent = new Intent(this, ChooseSchoolActivity.class);
            startActivityForResult(intent, REQUEST_CODE_EDIT_SCHOOL);
        });
        availabilityDialog.show();
    }

    @Override
    public void showBathroomPassword(boolean isExistBathroomBiz, boolean hadSetBathPassword) {
        if (!isExistBathroomBiz) {
            relEditBathroomPassword.setVisibility(View.GONE);
            return;
        }
        relEditBathroomPassword.setVisibility(View.VISIBLE);
        tvBathroomTip.setText(hadSetBathPassword ? "修改密码" : "未设置");
    }

    @Override
    public void gotoChooseSchool() {
        Intent intent = new Intent(this, ChooseSchoolActivity.class);
        startActivity(intent);
    }

    @Override
    public void gotoChangeSchool(ApplySchoolCheckRespDTO data) {
        Intent intent = new Intent(this, ChangeSchoolActivity.class);
        Log.e(TAG, "gotoChangeSchool: id =" + data.getId());
        intent.putExtra("id", Long.valueOf(data.getId()));
        intent.putExtra("reason", data.getReason());
        intent.putExtra("schoolName", data.getSchoolName());
        startActivity(intent);
    }

    @Override
    public void showWechatBind(String nickName) {
        Log.e(TAG, "showWechatBind: " + nickName );
        if(!TextUtils.isEmpty(nickName)) {
            tv_wechat.setText(nickName);
            weChatNickName = nickName;
        }else{
            tv_wechat.setText("未绑定");
            weChatNickName = null;
        }
    }

    @Override
    public void showAliPayBind(String nickName) {
        Log.e(TAG, "showAliPayBind: nickname =" +nickName );
        if(!TextUtils.isEmpty(nickName)) {
            tv_alipay.setText(nickName);
            aliPayNickName = nickName;
        }else{
            tv_alipay.setText("未绑定");
            aliPayNickName = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isNeedRefresh = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getPersonProfile();
        getCertificationStatus(presenter.getCertificationStatus());
    }

    @Override
    public void setAvatar(String pictureUrl) {
        if (!TextUtils.isEmpty(pictureUrl)) {
            avatarUrl = pictureUrl;
            Glide.with(this).load(Constant.IMAGE_PREFIX + pictureUrl)
                    .asBitmap()
                    .placeholder(R.drawable.ic_picture_error)
                    .error(R.drawable.ic_picture_error)
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_picture_error);
        }
    }

    @Override
    public void setNickName(String nickName) {
        tvNickname.setText(nickName);
    }

    @Override
    public void setSex(int sex) {
        tvSex.setText(sex == 1 ? "男" : "女");
    }

    @Override
    public void setMobile(String mobile) {
        showUserPhone(mobile);
    }

    @Override
    public void setSchoolName(String schoolName) {
        tvSchool.setText(schoolName);
    }

    @Override
    public void setResidenceName(String residenceName) {
        tvResidence.setText(residenceName);
    }

    @Override
    public void gotoChangeMobile() {
        Intent intent;
        intent = new Intent(getApplicationContext(), EditMobileActivity.class);
        intent.putExtra("nickName", "");
        startActivityForResult(intent, REQUEST_CODE_CHECK_PASSWORD);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        switch (event.getType()) {
            case REFRESH:
                isNeedRefresh = true;
                break;
            case CANCELAPPLYOK:
                onSuccess("已取消申请更换学校");
                break;
            case UNBIND_WECHAT:
                onSuccess("解除微信绑定成功");
                weChatNickName = null;
                break;
            case UNBIND_ALIPAY:
                onSuccess("解除支付宝绑定成功");
                tv_alipay.setText("未绑定");
                aliPayNickName = null;
                break;
            case BIND_WECHAT:
                onSuccess("微信绑定成功");
                WeChatBindRespDTO bindBean = (WeChatBindRespDTO)event.msg;
                weChatNickName = bindBean.getNickname();
                tv_wechat.setText(weChatNickName);
                break;
            case BIND_ALIPAY:
                onSuccess("支付宝绑定成功");
                User.AlipayBindBean alipayBindBean = (User.AlipayBindBean)event.msg;
                aliPayNickName = alipayBindBean.getAlipayNickName();
                tv_alipay.setText(aliPayNickName);
                break;
            case WECHAT_CODE:
                String weChatCode = (String)event.getMsg();
                presenter.bindWeChat(weChatCode);
                break;
            case CANCEL_WECHAT_AUTH:
                onError("已经退出微信授权");
                break;
            default:
                break;
        }
    }


    @Data
    public static class Event {
        EventType type;
        Object msg;


        public Event(EventType type) {
            this.type = type;
        }
        public void setMsg(Object msg) {
            this.msg = msg;
        }

        public enum EventType {
            /**
             * 刷新
             */
            REFRESH(1),
            CANCELAPPLYOK(2),
            UNBIND_WECHAT(3),
            UNBIND_ALIPAY(4),
            BIND_WECHAT(5),
            BIND_ALIPAY(6),
            WECHAT_CODE(7),
            CANCEL_WECHAT_AUTH(8);
            int type;

            EventType(int type) {
                this.type = type;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showUserPhone(String str) {
        if (!TextUtils.isEmpty(str)) {
            userPhone = str;
            String hide = "****";
            StringBuilder sb = new StringBuilder(str);
            sb.replace(3, 7, hide);

            String num = sb.toString();
            tvMobile.setText(num);
        }
    }

}
