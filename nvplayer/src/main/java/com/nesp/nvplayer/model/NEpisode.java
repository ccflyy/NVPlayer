package com.nesp.nvplayer.model;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-4-8 下午8:33
 * @project NVPlayerDemo
 **/
public class NEpisode {

    String name, url;
    Boolean isSelect = false;

    public Boolean getSelect() {
        return isSelect;
    }

    public NEpisode setSelect(Boolean select) {
        isSelect = select;
        return this;
    }

    public NEpisode(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public NEpisode setName(String name) {
        this.name = name;
        return this;
    }

    public NEpisode setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
