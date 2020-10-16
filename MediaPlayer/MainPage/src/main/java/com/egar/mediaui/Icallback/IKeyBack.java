package com.egar.mediaui.Icallback;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 17:30
 * @see {@link }
 */
public interface IKeyBack {
    /**
     * USB 页面中，需要自己去实现，根据返回键，判断是否退出界面
     * 监听了BACK 返回事件，谁注册谁接受，页面可见时注册，保证事件单一传递。
     */
    void onBack();
}
