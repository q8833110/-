package com.xiaolian.amigo.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.ui.device.bathroom.ChooseBathroomActivity;
import com.xiaolian.amigo.ui.user.ListChooseActivity;
import com.xiaolian.amigo.ui.device.bathroom.ChooseBathroomActivity;
import com.xiaolian.amigo.data.network.model.bathroom.BathRouteRespDTO;
import com.xiaolian.amigo.ui.user.intf.ICompleteInfoPresenter;
import com.xiaolian.amigo.ui.user.intf.ICompleteInfoView;
import com.xiaolian.amigo.util.Constant;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*该页面只针对公共浴室的设置，如果已经选择了普通洗澡地址，但未设置其他信息，不应该进到该页面*/
public class CompleteInfoActivity extends UserBaseActivity implements ICompleteInfoView {

    public static final String TAG = "CompleteInfoActivity";
    public static final String KEY_BATHROUTERESPDTO = "key_bathRouteRespDTO";

    public static final int DORMITORY =  1 ;

    public static final int BATHROOM = 2 ;

    @Inject
    ICompleteInfoPresenter<ICompleteInfoView> presenter;

    @BindView(R.id.radio_man)
    RadioButton radioMan;
    @BindView(R.id.man)
    LinearLayout man;
    @BindView(R.id.radio_woman)
    RadioButton radioWoman;
    @BindView(R.id.woman)
    LinearLayout woman;
    @BindView(R.id.tv_add_dormitory)
    RelativeLayout tvAddDormitory;

    @BindView(R.id.dormitory_address)
    TextView dormitoryAddressTextView;

    @BindView(R.id.choose_bathroom)
    Button chooseBathroom;

    ///性别，1：男，2：女，0或其他：未设置（和服务器端同步）
    private  int sex = -1 ;

    ///宿舍地址信息
    private  String dormitoryName;

    ///上一个页面传递进来的值
    private  BathRouteRespDTO bathRouteRespDTO;


    @Override
    protected void initView() {
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        presenter.onAttach(this);
        setMainBackground(R.color.colorBackgroundGray);
    }

    @Override
    protected int setTitle() {
        return R.string.complete_info;
    }


    @Override
    protected int setLayout() {
        return R.layout.activity_complete_info;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //配置用户的初始值
        initialPersonalInfo();

        //刷新页面
        refreshCompleteInfoView();

    }

    @OnClick(R.id.tv_add_dormitory)
    public void addDormitory() {
        //跳转到选择宿舍页面
        Log.d(TAG, "addDormitory: " + "跳转到选择宿舍列表页面");
        Intent intent = new Intent(this, ListChooseActivity.class);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_IS_EDIT, false);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_ACTION,
                ListChooseActivity.ACTION_LIST_BUILDING);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_SRC_ACTIVITY, Constant.COMPLETE_INFO_ACTIVITY_SRC);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_DEVICE_TYPE, Device.HEATER.getType());
        startActivity(intent );
    }

    @OnClick(R.id.choose_bathroom)
    public void chooseBathroom() {
        //跳转到选择洗澡地址页面，如果设置完成则跳转到洗澡页面
        //先判断性别、宿舍是否已经设置成功，必须要先设置性别和宿舍
        if (sex == -1 || (sex != 1 && sex != 2)) /*未设置性别，提示先设置性别*/{
            //弹框提示设置性别
            Toast toast = Toast.makeText(this, "请先设置你的性别哦！", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (TextUtils.isEmpty(dormitoryName)) /*未设置宿舍地址信息*/{
            //弹框提示设置宿舍信息
            Toast toast = Toast.makeText(this, "请先设置你的宿舍信息哦！", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        presenter.updateSex(sex); //更新个人性别，异步操作，有可能会失败

        if (bathRouteRespDTO!=null && !bathRouteRespDTO.isExistHistory()) /*未设置洗澡地址信息*/ {
            Log.d(TAG, "chooseBathroom: " + "跳转到选择洗澡地址页面");
            Intent intent = new Intent(this, ListChooseActivity.class);
            intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_IS_EDIT, false);
            intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_ACTION,
                    ListChooseActivity.ACTION_LIST_BUILDING);
            intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_SRC_ACTIVITY, Constant.MAIN_ACTIVITY_BATHROOM_SRC);
            intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_DEVICE_TYPE, Device.HEATER.getType());
            startActivity(intent);
        } else if (bathRouteRespDTO!=null)/*跳转到公共浴室页面*/{
            Intent intent = new Intent(this, ChooseBathroomActivity.class)
                    .putExtra(ChooseBathroomActivity.KEY_BUILDING_ID, bathRouteRespDTO.getBuildingId())
                    .putExtra(ChooseBathroomActivity.KEY_RESIDENCE_TYPE, bathRouteRespDTO.getResidenceType())
                    .putExtra(ChooseBathroomActivity.KEY_RESIDENCE_ID, bathRouteRespDTO.getResidenceId());
            startActivity(intent);
            Log.d(TAG, "chooseBathroom: " + "跳转到洗澡页面");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
    }


    @OnClick({R.id.woman ,R.id.radio_woman})
    public void choseWoman(){
        sex = 2;
        radioMan.setChecked(false);
        radioWoman.setChecked(true);
    }

    @OnClick({R.id.man , R.id.radio_man})
    public void choseMan(){
        sex = 1;
        radioMan.setChecked(true);
        radioWoman.setChecked(false);
    }

    ///刷新页面
    @Override
    public void refreshCompleteInfoView() {
        refreshSexStatus();
        refreshDomitoryAddress();
        refreshBathroomAddress();
    }

    private void refreshSexStatus() {
        if (sex == -1) {
            return;
        }
        if (sex == 1) /*选中男*/{
            radioMan.setChecked(true);
            radioWoman.setChecked(false);
        } else if (sex == 2)/*选中女*/ {
            radioMan.setChecked(false);
            radioWoman.setChecked(true);
        } else /*都没有选中*/{
            radioMan.setChecked(false);
            radioWoman.setChecked(false);
        }
    }

    private void refreshDomitoryAddress() {
        if (!TextUtils.isEmpty(dormitoryName)) /*设置了宿舍地址信息*/{
            dormitoryAddressTextView.setText(dormitoryName);
        } else {
            dormitoryAddressTextView.setText("添加宿舍");
        }
    }

    private void refreshBathroomAddress() {
        if (bathRouteRespDTO!=null && bathRouteRespDTO.isExistHistory()) /*设置了洗澡地址信息*/{
            chooseBathroom.setText("可以去洗澡啦");
        } else {
            chooseBathroom.setText("选择洗澡地址");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    ///根据已有的值配置初始信息
    private void initialPersonalInfo() {
        if (presenter.getUserInfo() != null) {
            if (presenter.getUserInfo().getSex() != null) {
                sex = presenter.getUserInfo().getSex();
            }
            if (presenter.getUserInfo().getResidenceName() != null)
            dormitoryName = presenter.getUserInfo().getResidenceName();
        }
        initIntent();
    }

    ///获取上个页面传递进来的值
    protected void initIntent() {
        if (getIntent() != null) /*由上一个页面带进来的值*/{
            bathRouteRespDTO = getIntent().getParcelableExtra(KEY_BATHROUTERESPDTO);
        }
    }
}
