package com.xiaolian.amigo.data.enumeration;

/**
 * banner type
 *
 * @author zcd
 * @date 17/11/6
 */

public enum BannerType {
    NONE(1),
    INSIDE(2),
    OUTSIDE(3);
    int type;

    BannerType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
