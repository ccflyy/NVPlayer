/*
 *
 *   Copyright (c) 2019  NESP Technology Corporation. All rights reserved.
 *
 *   This program is free software; you can redistribute it and/or modify it
 *   under the terms and conditions of the GNU General Public License,
 *   version 2, as published by the Free Software Foundation.
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License.See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.nvplayer.model;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-4-8 下午8:33
 * @project NVPlayerDemo
 **/
public class NEpisode {

    private String name, playInfoUrl ;
    private Boolean isSelect = false;

    public Boolean getSelect() {
        return isSelect;
    }

    public NEpisode setSelect(Boolean select) {
        isSelect = select;
        return this;
    }

    public NEpisode(String name, String playInfoUrl) {
        this.name = name;
        this.playInfoUrl = playInfoUrl;
    }

    public NEpisode setName(String name) {
        this.name = name;
        return this;
    }

    public NEpisode setPlayInfoUrl(String playInfoUrl) {
        this.playInfoUrl = playInfoUrl;
        return this;
    }


    public String getName() {
        return name;
    }

    public String getPlayInfoUrl() {
        return playInfoUrl;
    }

}
