package com.deep.deepwifi.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangfei on 2018/3/21.
 */

public class ThreadUtils {
    public static ExecutorService getPool(int num){
        return Executors.newFixedThreadPool(num);
    }
}
