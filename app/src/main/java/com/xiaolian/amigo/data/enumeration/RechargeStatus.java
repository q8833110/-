package com.xiaolian.amigo.data.enumeration;

import com.xiaolian.amigo.R;

/**
 * 提现状态
 *
 * @author zcd
 * @date 17/10/27
 */

public enum RechargeStatus {
    KNOWN(0, "未知状态") {
        @Override
        public int getColorRes() {
            return R.color.colorFullRed;
        }

        @Override
        public String[] getNextOperations() {
            return new String[]{COMMON_PROBLEM, TO_COMPLAIN};
        }
    },
    AUDIT_PENDING(1, "等待审核") {
        @Override
        public int getColorRes() {
            return R.color.colorFullRed;
        }

        @Override
        public String[] getNextOperations() {
            return new String[]{"提醒客服尽快处理", COMMON_PROBLEM};
        }
    },
    AUDIT_FAIL(2, "审核未通过") {
        @Override
        public int getColorRes() {
            return R.color.colorFullRed;
        }

        @Override
        public String[] getNextOperations() {
            return new String[]{COMMON_PROBLEM, CONTACT_CUSTOMER_SERVICE};
        }
    },
    THIRD_PENDING(3, "等待支付确认") {
        @Override
        public int getColorRes() {
            return R.color.colorFullRed;
        }

        @Override
        public String[] getNextOperations() {
            return new String[]{COMMON_PROBLEM, TO_COMPLAIN};
        }
    },
    RECHARGE_SUCCESS(4, "充值成功") {
        @Override
        public int getColorRes() {
            return R.color.device_dispenser;
        }

        @Override
        public String[] getNextOperations() {
            return new String[]{COMMON_PROBLEM, TO_COMPLAIN};
        }
    },
    RECHARGE_FAIL(5, "充值失败") {
        @Override
        public int getColorRes() {
            return R.color.colorFullRed;
        }

        @Override
        public String[] getNextOperations() {
            return new String[]{COMMON_PROBLEM, CONTACT_CUSTOMER_SERVICE};
        }
    },
    BEHALF_OF_RECHARGE(-1, "代充值成功") {
        @Override
        public int getColorRes() {
            return R.color.colorYellow;
        }

        @Override
        public String[] getNextOperations() {
            return new String[]{COMMON_PROBLEM, TO_COMPLAIN};
        }
    };
    private static final String COMMON_PROBLEM = "常见问题";
    private static final String CONTACT_CUSTOMER_SERVICE = "联系客服";
    private static final String TO_COMPLAIN = "我要投诉";
    private int type;
    private String desc;

    RechargeStatus(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public abstract int getColorRes();

    // 获取下一步操作
    public abstract String[] getNextOperations();

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static RechargeStatus getRechargeStatus(int type) {
        for (RechargeStatus status : RechargeStatus.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return KNOWN;
    }
}
