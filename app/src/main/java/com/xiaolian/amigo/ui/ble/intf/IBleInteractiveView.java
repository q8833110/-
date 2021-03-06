package com.xiaolian.amigo.ui.ble.intf;


import com.xiaolian.amigo.ui.base.intf.IBaseView;

/**
 * @author caidong
 * @date 17/9/22
 */
public interface IBleInteractiveView extends IBaseView {

    // 连接设备失败
    void onConnectError();

    // 向设备发送数据失败
    void onWriteError();

    // 接受设备数据失败
    void onNotifyError();

    // 设备状态错误（通常为不在线）
    void onStatusError();

    // 显示结果
    void onShow(String content);
}
