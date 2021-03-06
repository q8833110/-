package com.xiaolian.amigo.data.enumeration;

/**
 * 支付方式
 *
 * @author caidong
 * @date 17/9/18
 */
public enum Payment {
    BALANCE(1, "余额支付"), BONUS(2, "代金券支付");

    int type;
    String desc;

    Payment(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Payment getPayment(int type) {
        for (Payment payment : Payment.values()) {
            if (payment.getType() == type) {
                return payment;
            }
        }
        return null;
    }
}
