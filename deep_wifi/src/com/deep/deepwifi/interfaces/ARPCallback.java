package com.deep.deepwifi.interfaces;

import java.util.ArrayList;

import com.deep.deepwifi.bean.ARPBean;

/**
 * Created by wangfei on 2018/3/28.
 */

public interface ARPCallback {
    public void callback(ArrayList<ARPBean> list);
}
