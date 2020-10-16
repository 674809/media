package com.egar.mediaui.Icallback;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/14 13:50
 * @see {@link }
 */
public interface IAppApplication {
        /**
         * Appliaction 创建
         */
        void appOnCreate();
        /**
         * Appliaction 低内存时销毁
         */
        void appOnTerminate();
}
