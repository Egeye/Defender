package com.egeye.mobilesafe.domain;

/**
 * Created by Octavio on 2016/2/7.
 * 黑名单的业务bean
 */
public class BlackNumberInfo {
    private String number;
    private String mode;

    @Override
    public String toString() {
        return "BlackNumberInfo [number=" + number + ", mode =" + mode + "]";
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
