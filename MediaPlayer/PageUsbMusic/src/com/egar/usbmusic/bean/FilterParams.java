package com.egar.usbmusic.bean;

/**
 * 过滤参数
 * <p>针对
 * {@link com.egar.scanner.api.EgarApiProvider#getAllMedias(int type, int sortBy, String[] params)}
 * 的参数params，用来构造传入参数</p>
 *
 * @author Jun.Wang
 */
public final class FilterParams {

    // 参数数组
    private String[] params;

    public String[] getParams() {
        return params;
    }

    /**
     * Set parameters
     */
    public void setFolderPath(String folderPath) {
        params = new String[6];
        params[0] = folderPath;//folderName
        params[1] = null;//mediaName
        params[2] = null;//fileName
        params[3] = null;//artistName
        params[4] = null;//albumName
        params[5] = null;//collected
    }

    /**
     * Set parameters
     */
    public void setArtist(String artist) {
        params = new String[6];
        params[0] = null;//folderName
        params[1] = null;//mediaName
        params[2] = null;//fileName
        params[3] = artist;//artistName
        params[4] = null;//albumName
        params[5] = null;//collected
    }

    /**
     * Set parameters
     */
    public void setAlbum(String album) {
        params = new String[6];
        params[0] = null;//folderName
        params[1] = null;//mediaName
        params[2] = null;//fileName
        params[3] = null;//artistName
        params[4] = album;//albumName
        params[5] = null;//collected
    }

    /**
     * Set parameters
     *
     * @param collect {@link juns.lib.media.flags.MediaCollectState}
     */
    public void setCollect(int collect) {
        params = new String[6];
        params[0] = null;//folderName
        params[1] = null;//mediaName
        params[2] = null;//fileName
        params[3] = null;//artistName
        params[4] = null;//albumName
        params[5] = "" + collect;//collected
    }
}
