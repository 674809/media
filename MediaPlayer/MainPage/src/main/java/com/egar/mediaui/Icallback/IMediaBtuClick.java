package com.egar.mediaui.Icallback;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/31 09:46
 * @see {@link }
 */
public interface IMediaBtuClick {
    /**
     * 下一首长按事件
     */
    void onNextLongClick();

    /**
     * 下一首短按事件
     */
    void onNextClick();

    /**
     * 上一首长按事件
     */
    void onPrevLongClick();

    /**
     * 上一首短按事件
     */
    void onPrevClick();
}
