package com.xiaolian.amigo.ui.user;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xiaolian.amigo.MvpApp;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.network.model.user.UserCertifyInfoRespDTO;
import com.xiaolian.amigo.data.vo.UserCertificationStatus;
import com.xiaolian.amigo.di.componet.DaggerUserActivityComponent;
import com.xiaolian.amigo.di.componet.UserActivityComponent;
import com.xiaolian.amigo.di.module.UserActivityModule;
import com.xiaolian.amigo.ui.base.BaseActivity;
import com.xiaolian.amigo.ui.repair.adaptor.ImageAddAdapter;
import com.xiaolian.amigo.ui.user.intf.IUserCerticifationStatusPresenter;
import com.xiaolian.amigo.ui.user.intf.IUserCertificationStatusView;
import com.xiaolian.amigo.ui.widget.ErrorLayout;
import com.xiaolian.amigo.ui.widget.GridSpacesItemDecoration;
import com.xiaolian.amigo.ui.widget.photoview.AlbumItemActivity;
import com.xiaolian.amigo.util.Log;
import com.xiaolian.amigo.util.ScreenUtils;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.xiaolian.amigo.util.Constant.CERTIFICATION_FAILURE;
import static com.xiaolian.amigo.util.Constant.CERTIFICATION_PASS;
import static com.xiaolian.amigo.util.Constant.CERTIFICATION_REVIEWING;
import static com.xiaolian.amigo.util.Constant.USER_CERTIFICATION_STATUS_ACTIVITY_SRC;

public class UserCertificationStatusActivity extends BaseActivity implements IUserCertificationStatusView {

    private static final String CERTIFICATION_FILURE = "（认证未通过）";

    private static final String CERTIFICATIONINT = "(审核中，请稍等...)";

    public static final String KEY_CERTIFICATION_TYPE = "KEY_CERTIFICATION_TYPE";

    @BindView(R.id.certification)
    Button certification;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.reason_ll)
    LinearLayout reasonLl;
    @BindView(R.id.reason_line)
    View reasonLine;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.change_dormitory)
    TextView changeDormitory;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.loading_rl)
    RelativeLayout loadingRl;
    @BindView(R.id.error_net_layout)
    ErrorLayout errorNetLayout;


    private UserActivityComponent mActivityComponent;

    @Inject
    IUserCerticifationStatusPresenter<IUserCertificationStatusView> presenter;

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_text)
    TextView tvToolbarText;
    @BindView(R.id.tv_toolbar_iv)
    ImageView tvToolbarIv;
    @BindView(R.id.tv_department)
    TextView tvDepartment;
    @BindView(R.id.tv_profession)
    TextView tvProfession;
    @BindView(R.id.tv_grade)
    TextView tvGrade;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.tv_studentId)
    TextView tvStudentId;
    @BindView(R.id.tv_dormitory)
    TextView tvDormitory;
    @BindView(R.id.student_card_id)
    RecyclerView studentCardId;
    @BindView(R.id.card_id)
    RecyclerView cardId;
    @BindView(R.id.main_content)
    LinearLayout mainContent;
    @BindView(R.id.sv_main_container)
    ScrollView svMainContainer;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.view_line)
    View viewLine;

    private Unbinder unbinder;

    private int screenWidth;
    private int imageWidth;

    private ImageAddAdapter studentIdAdapter;
    List<ImageAddAdapter.ImageItem> studentIdImages = new ArrayList<>();


    private ImageAddAdapter cardIdAdapter;  // 身份证
    List<ImageAddAdapter.ImageItem> cardIdImages = new ArrayList<>();


    List<String> cardIdUrlImages = new ArrayList<>();

    List<String> studentUrlImages = new ArrayList<>();


    private int rlToolBarHeight;

    private ValueAnimator loadingAnimator ;

    int[] loadingRes = new int[]{
            R.drawable.loading_one, R.drawable.loading_two,
            R.drawable.loading_three, R.drawable.loading_four
    };
    @Override
    protected void setUp() {

    }


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_certification_status);
        unbinder = ButterKnife.bind(this);
        setTitleVisiable(View.GONE);
        svMainContainer.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > rlToolBarHeight) {
                setTitleVisiable(View.VISIBLE);
            } else {
                setTitleVisiable(View.GONE);
            }
        });

        initJect();
        initScrollView();
        initView();
        initImageAdd();
        presenter.getCertifyInfo();
        presenter.getDormitory();
    }


    /**
     * 根据传过来的现不同页面
     */
    private void setStatus(int type) {
        if (type == CERTIFICATION_FAILURE) {
            showFail();
            tvToolbarText.setText(CERTIFICATION_FILURE);
        } else if (type == CERTIFICATION_REVIEWING) {
            showBarTxt();
            tvToolbarText.setText(CERTIFICATIONINT);
        } else if (type == CERTIFICATION_PASS) {
            showBarIv();
        } else {

        }
    }

    private void showFail() {
        tvToolbarText.setVisibility(View.VISIBLE);
        tvToolbarIv.setVisibility(View.GONE);
        certification.setVisibility(View.VISIBLE);
        changeDormitory.setVisibility(View.GONE);
        setMarginBottom(ScreenUtils.dpToPxInt(this, 91));
    }

    private void showBarTxt() {
        tvToolbarText.setVisibility(View.VISIBLE);
        tvToolbarIv.setVisibility(View.GONE);
        certification.setVisibility(View.GONE);
        changeDormitory.setVisibility(View.GONE);
        setMarginBottom(ScreenUtils.dpToPxInt(this, 20));
    }


    /**
     * 设置margin
     */
    private void setMarginBottom(int marginBottom) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) svMainContainer.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, marginBottom);
        svMainContainer.setLayoutParams(layoutParams);
    }

    private void showBarIv() {
        tvToolbarIv.setVisibility(View.VISIBLE);
        tvToolbarText.setVisibility(View.GONE);
        certification.setVisibility(View.GONE);
        setMarginBottom(ScreenUtils.dpToPxInt(this ,20));
        changeDormitory.setVisibility(View.VISIBLE);
    }

    private void initView() {
        presenter.onAttach(this);
        rlToolbar.post(() -> {
            rlToolBarHeight = rlToolbar.getHeight();
        });
        setErrorNetListener();
        initLoadingAnim();
    }


    @OnClick(R.id.certification)
    public void startCertification() {

        Intent intent = new Intent(this, UserCertificationActivity.class);
        UserCertificationStatus userCertificationStatus = new UserCertificationStatus(
           tvDepartment.getText().toString().trim() ,tvProfession.getText().toString().trim() ,
           tvGrade.getText().toString().trim() ,tvClass.getText().toString().trim(),
           tvStudentId.getText().toString().trim() ,studentUrlImages);
        if (cardIdUrlImages != null && cardIdUrlImages.size() > 1){
            userCertificationStatus.setFrontImageBase64(cardIdUrlImages.get(0));
            userCertificationStatus.setBackImageBase64(cardIdUrlImages.get(1));
        }

        EventBus.getDefault().postSticky(userCertificationStatus);
        startActivity(intent);
        this.finish();
    }

    /**
     * 滑动响应
     */
    private void initScrollView() {
        IOverScrollDecor iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll(svMainContainer);

    }

    private void initJect() {
        mActivityComponent = DaggerUserActivityComponent.builder()
                .userActivityModule(new UserActivityModule(this))
                .applicationComponent(((MvpApp) getApplication()).getComponent())
                .build();
        mActivityComponent.inject(this);
    }

    private void setTitleVisiable(int visiable) {
        tvTitle.setVisibility(visiable);
        viewLine.setVisibility(visiable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        presenter.onDetach();
    }


    private void initLoadingAnim() {
        loadingAnimator = ValueAnimator.ofInt(0, 3, 0);
        loadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnimator.setDuration(1000);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                Log.wtf(TAG, currentValue + "");
                if (ivLoading != null)
                    ivLoading.setImageResource(loadingRes[currentValue]);
            }
        });
    }

    @OnClick(R.id.certification)
    public void certification() {
        startActivity(this, UserCertificationActivity.class);
    }

    @OnClick(R.id.change_dormitory)
    public void changeDormitory() {
        Intent intent;
        intent = new Intent(this, ListChooseActivity.class);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_IS_EDIT, false);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_ACTION,
                ListChooseActivity.ACTION_LIST_BUILDING);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_SRC_ACTIVITY, USER_CERTIFICATION_STATUS_ACTIVITY_SRC);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_DEVICE_TYPE, Device.HEATER.getType());
        startActivity(intent);
    }


    private void initImageAdd() {
        screenWidth = ScreenUtils.getScreenWidth(this);
//        imageWidth = (screenWidth - ScreenUtils.dpToPxInt(this, 62)) / 3;
        imageWidth = ScreenUtils.dpToPxInt(this, 100);
        cardIdAdapter = new ImageAddAdapter(this, R.layout.item_image_add, cardIdImages, false);
        cardIdAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(UserCertificationStatusActivity.this, AlbumItemActivity.class);
                intent.putExtra(AlbumItemActivity.EXTRA_CURRENT, position);
                intent.putStringArrayListExtra(AlbumItemActivity.EXTRA_TYPE_BASE64, (ArrayList<String>) cardIdUrlImages);
                intent.putExtra(AlbumItemActivity.INTENT_ACTION, AlbumItemActivity.ACTION_NORMAL);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        cardIdAdapter.setViewWidth(imageWidth
        );
        cardId.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardId.addItemDecoration(new GridSpacesItemDecoration(3, ScreenUtils.dpToPxInt(this, 10), false));
        cardId.setAdapter(cardIdAdapter);


        studentIdAdapter = new ImageAddAdapter(this, R.layout.item_image_add, studentIdImages, false);
        studentIdAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(UserCertificationStatusActivity.this, AlbumItemActivity.class);
                intent.putExtra(AlbumItemActivity.EXTRA_CURRENT, position);
                intent.putStringArrayListExtra(AlbumItemActivity.EXTRA_TYPE_BASE64, (ArrayList<String>) studentUrlImages);
                intent.putExtra(AlbumItemActivity.INTENT_ACTION, AlbumItemActivity.ACTION_NORMAL);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        studentIdAdapter.setViewWidth(imageWidth);
        studentCardId.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        studentCardId.addItemDecoration(new GridSpacesItemDecoration(3, ScreenUtils.dpToPxInt(this, 10), false));
        studentCardId.setAdapter(studentIdAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (tvDormitory != null && presenter != null)
            tvDormitory.setText(presenter.getDormInfo());
    }

    @Override
    public void setInfo(UserCertifyInfoRespDTO data) {

        svMainContainer.setVisibility(View.VISIBLE);
        setStatus(data.getStatus());
        if (data.getStatus() == CERTIFICATION_FAILURE) {
            tvReason.setText(data.getFailReason());
            reasonLl.setVisibility(View.VISIBLE);
            reasonLine.setVisibility(View.VISIBLE);
        } else {
            reasonLl.setVisibility(View.GONE);
            reasonLine.setVisibility(View.GONE);
        }

        tvDepartment.setText(data.getFaculty());
        tvProfession.setText(data.getMajor());
        tvGrade.setText(data.getGrade() + "");
        tvClass.setText(data.getClassName());
        tvStudentId.setText(data.getStuNum() + "");
        tvDormitory.setText(presenter.getDormInfo());

        if (cardIdUrlImages.size() > 0) cardIdUrlImages.clear();

        if (studentUrlImages.size() > 0) studentUrlImages.clear();


        studentUrlImages.addAll(data.getStuPicturesData());
        for (String url : data.getStuPicturesData()) {

//            Log.wtf(TAG ,url);
            studentIdImages.add(new ImageAddAdapter.ImageItem(Base64.decode(url, Base64.DEFAULT)));

//            studentIdImages.add(new ImageAddAdapter.ImageItem(url));
        }
        if (studentIdAdapter != null) studentIdAdapter.notifyDataSetChanged();

        if (cardIdImages != null) {
            cardIdImages.clear();


            cardIdImages.add(new ImageAddAdapter.ImageItem(Base64.decode(data.getIdCardFrontData(), Base64.DEFAULT)));
            cardIdImages.add(new ImageAddAdapter.ImageItem(Base64.decode(data.getIdCardBehindData(), Base64.DEFAULT)));


            cardIdUrlImages.add(data.getIdCardFrontData());
            cardIdUrlImages.add(data.getIdCardBehindData());


        }

        cardIdAdapter.notifyDataSetChanged();
    }


    @Override
    public void showAnimaLoading() {
        loadingRl.setVisibility(View.VISIBLE);
        if (loadingAnimator == null) return;

        if (loadingAnimator.isRunning()) {
            loadingAnimator.cancel();
        }
        loadingAnimator.start();
    }

    private void setErrorNetListener(){
        if (errorNetLayout != null)
            errorNetLayout.setReferListener(() -> {
                    if(presenter != null) {
                        presenter.getCertifyInfo();
                    }});
    }

    @Override
    public void showErrorLayout() {
        errorNetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideContent() {
        svMainContainer.setVisibility(View.GONE);
    }

    @Override
    public void hideErrorLayout() {
        errorNetLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideAnimaLoading() {
        loadingRl.setVisibility(View.GONE);
        if (loadingAnimator == null) return;

        if (loadingAnimator.isRunning()) loadingAnimator.cancel();
    }


}
