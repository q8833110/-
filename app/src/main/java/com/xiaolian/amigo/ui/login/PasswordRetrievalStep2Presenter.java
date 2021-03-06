package com.xiaolian.amigo.ui.login;

import com.xiaolian.amigo.data.manager.intf.ILoginDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.common.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.login.PasswordResetReqDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.login.intf.IPasswordRetrievalStep2Presenter;
import com.xiaolian.amigo.ui.login.intf.IPasswordRetrievalStep2View;

import javax.inject.Inject;

/**
 * 找回密码2
 *
 * @author zcd
 * @date 17/9/20
 */

public class PasswordRetrievalStep2Presenter<V extends IPasswordRetrievalStep2View> extends BasePresenter<V>
        implements IPasswordRetrievalStep2Presenter<V> {

    private ILoginDataManager loginDataManager;

    @Inject
    PasswordRetrievalStep2Presenter(ILoginDataManager loginDataManager) {
        super();
        this.loginDataManager = loginDataManager;
    }

    @Override
    public void resetPassword(String code, String mobile, String password) {
        PasswordResetReqDTO dto = new PasswordResetReqDTO();
        dto.setCode(code);
        dto.setMobile(mobile);
        dto.setPassword(password);
        addObserver(loginDataManager.passwordReset(dto), new NetworkObserver<ApiResult<BooleanRespDTO>>() {
            @Override
            public void onReady(ApiResult<BooleanRespDTO> result) {
                if (null == result.getError()) {
                    if (result.getData().isResult()) {
                        getMvpView().onSuccess("密码重置成功，请登录");
                        getMvpView().gotoLoginView();
                    } else {
                        getMvpView().onError("密码重置失败，请重试");
                    }
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }
}
