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

package com.nesp.nvplayer.cling.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.nesp.nvplayer.R
import com.nesp.nvplayer.model.NEpisode

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-8 下午6:31
 * @project FishMovie
 **/
class ClingSelectEpisodesRvAdapter(
        private var nEpisodes: MutableList<NEpisode>
) : RecyclerView.Adapter<ClingSelectEpisodesRvAdapter.EpisodesHolder>() {

    private var singleSelectPosition = -1

    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesHolder {
        val itemView =
                LayoutInflater.from(parent.context).inflate(
                        if (viewType == VIEW_TYPE_NORMAL)
                            R.layout.nvplayer_item_cling_episode
                        else
                            R.layout.nvplayer_episode_recycleview_empty
                        , parent, false)

        return EpisodesHolder(itemView)
    }

    override fun onBindViewHolder(holder: EpisodesHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_EMPTY) {
            return
        }

        val nEpisode = nEpisodes[position]
        val episodeName = nEpisode.name
        holder.radioButtonEpisode.text = removeStrings(episodeName, "第", "集")
        holder.radioButtonEpisode.isChecked = singleSelectPosition == position
        holder.checkBoxDownloadTag.visibility = View.GONE
        holder.radioButtonEpisode.setOnClickListener {
            setClickState(position)
            onEpisodeItemClickListener?.onEpisodeItemClick(nEpisode, position)
        }

    }

    override fun getItemCount(): Int {
        return if (nEpisodes.size == 0) 1 else nEpisodes.size
    }

    override fun getItemViewType(position: Int): Int {
        if (nEpisodes.size < 1) {
            return VIEW_TYPE_EMPTY
        }
        return VIEW_TYPE_NORMAL
    }

    class EpisodesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButtonEpisode: AppCompatRadioButton = itemView.findViewById(R.id.nvplayer_cling_episode_item_episode_rb_name)
        val checkBoxDownloadTag: CheckBox = itemView.findViewById(R.id.nvplayer_cling_episode_item_episode_cb_download_tag)
    }

    /*********************************Utils*************************************/
    //TODO:Utils

    fun setClickState(position: Int) {
        clearAllClickState()
        singleSelectPosition = position
        notifyDataSetChanged()
    }

    fun clearAllClickState() {
        singleSelectPosition = -1
        notifyDataSetChanged()
    }

    //=======================================================================

    private var onEpisodeItemClickListener: OnEpisodeItemClickListener? = null

    fun setOnEpisodeItemClickListener(onEpisodeItemClickListener: OnEpisodeItemClickListener) {
        this.onEpisodeItemClickListener = onEpisodeItemClickListener
    }

    interface OnEpisodeItemClickListener {
        fun onEpisodeItemClick(episode: NEpisode, position: Int)
    }

    private fun removeStrings(string: String, vararg removeStrings: String): String {
        var str = string
        for (removeString in removeStrings) {
            if (str.contains(removeString)) {
                str = str.replace(removeString, "")
            }
        }

        return str
    }
}