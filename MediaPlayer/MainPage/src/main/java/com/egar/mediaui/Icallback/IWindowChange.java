package com.egar.mediaui.Icallback;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 11:35
 * @see {@link }
 */
public interface IWindowChange {
    /**
     * 窗口切换为全屏
     */
   void  onWindowChangeFull();
    /**
     * 窗口切换为半屏
     */
    void  onWindowChangeHalf();
}
