/*
 *
 *   Copyright (c) 2019  NESP Technology Corporation. All rights reserved.
 *
 *   This program is free software; you can redistribute it and/or modify it
 *   under the terms and conditions of the GNU General Public License,
 *   version 2, as published by the Free Software Foundation.
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License.See the License for the specific language governing permission and
 *   limitations under the License.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.nvplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import com.nesp.nvplayer.R;
import com.nesp.nvplayer.model.NEpisode;

import java.util.List;

/**
 * @Team: NESP Technology
 * @Author: 靳兆鲁
 * Email: 1756404649@qq.com
 * @Time: Created 2018/8/18 2:11
 * @Project NespMovie
 **/
public class NEpisodeRecyclerViewAdapter extends RecyclerView.Adapter<NEpisodeRecyclerViewAdapter.ViewHolder> {

    private List<NEpisode> nEpisodeList;
    private Context context;
    private RecyclerView recyclerView;
    private int singleSelectPosition = -1;

    private final int VIEW_TYPE_EMPTY = 0;
    private final int VIEW_TYPE_NORMAL = 1;


    class ViewHolder extends RecyclerView.ViewHolder {

        View episodeView;
        AppCompatRadioButton radioButtonEpisode;
        CheckBox checkBoxDownloadTag;

        ViewHolder(View view) {
            super(view);
            episodeView = view;
            radioButtonEpisode = view.findViewById(R.id.nvplayer_episode_item_episode_rb_name);
            checkBoxDownloadTag = view.findViewById(R.id.nvplayer_episode_item_episode_cb_download_tag);
        }
    }

    public NEpisodeRecyclerViewAdapter(List<NEpisode> nEpisodeList, Context context, RecyclerView recyclerView) {
        this.nEpisodeList = nEpisodeList;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @SuppressLint("LongLogTag")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = getItemViewType(i) == VIEW_TYPE_EMPTY ?
                LayoutInflater.from(context).inflate(R.layout.nvplayer_episode_recycleview_empty, viewGroup, false)
                :
                LayoutInflater.from(context).inflate(R.layout.nvplayer_item_episode, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_EMPTY) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
            ViewGroup.LayoutParams layoutParams1 = holder.itemView.getLayoutParams();
            layoutParams1.height = layoutParams.height;
            layoutParams1.width = 800;
            holder.itemView.setLayoutParams(layoutParams1);
            return;
        }


        NEpisode nEpisode = nEpisodeList.get(position);
        String episodeName = nEpisode.getName();

        holder.radioButtonEpisode.setText(removeString(episodeName, "第", "集"));        holder.radioButtonEpisode.setChecked(singleSelectPosition == position);
        holder.checkBoxDownloadTag.setVisibility(View.GONE);
        holder.radioButtonEpisode.setOnClickListener(v -> {
            setClickState(position);
            if (onEpisodeItemClickListener != null)
                onEpisodeItemClickListener.onEpisodeItemClick(nEpisode, position);
        });

    }

    @Override
    public int getItemViewType(int position) {
        if (nEpisodeList == null || nEpisodeList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (nEpisodeList == null || nEpisodeList.size() == 0)
            return 1;
        return nEpisodeList.size();
    }

    public void setClickState(int position) {
        clearAllClickState();
        nEpisodeList.get(position).setSelect(true);
        notifyDataSetChanged();
    }

    public void clearAllClickState() {
        for (NEpisode nEpisode : nEpisodeList) {
            nEpisode.setSelect(false);
        }
        notifyDataSetChanged();
    }

    public void setClickPosition(int position) {
        singleSelectPosition = position;
        notifyDataSetChanged();
    }

    public Integer getSelectPosition() {
        return singleSelectPosition;
    }

    //=======================================================================

    private OnEpisodeItemClickListener onEpisodeItemClickListener;

    public void setOnEpisodeItemClickListener(OnEpisodeItemClickListener onEpisodeItemClickListener) {
        this.onEpisodeItemClickListener = onEpisodeItemClickListener;
    }

    public interface OnEpisodeItemClickListener {
        void onEpisodeItemClick(NEpisode episode, int position);
    }

    private String removeString(String string, String... removeStrings) {
        for (String removeString : removeStrings) {
            if (string.contains(removeString)) {
                string = string.replace(removeString, "");
            }
        }

        return string;
    }

}
