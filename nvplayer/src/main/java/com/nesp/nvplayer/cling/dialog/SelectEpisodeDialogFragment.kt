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

package com.nesp.nvplayer.cling.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nesp.nvplayer.R
import com.nesp.nvplayer.cling.adapter.ClingSelectEpisodesRvAdapter
import com.nesp.nvplayer.model.NEpisode
import com.nesp.sdk.android.widget.recyclerview.SuperSpaceItemDecoration
import java.util.HashMap

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-8 下午6:09
 * @project FishMovie
 **/
class SelectEpisodeDialogFragment(
        private var nEpisodes: MutableList<NEpisode>,
        private var initClickPosition: Int,
        private val iSelectEpisodeDialog: ISelectEpisodeDialog
) : BaseDialog() {

    private lateinit var clingSelectEpisodesRvAdapter: ClingSelectEpisodesRvAdapter
    private lateinit var rvEpisodes: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(R.layout.nvplayer_cling_dialog_select_episode, container, false)

        val ivClose: ImageView = findView(R.id.nvplayer_cling_dialog_select_episode_iv_close)
        ivClose.setOnClickListener { dismiss() }

        rvEpisodes = findView(R.id.nvplayer_cling_dialog_select_episode_rv_episodes)
        val gridLayoutManager = GridLayoutManager(context, 5)
        rvEpisodes.layoutManager = gridLayoutManager
        clingSelectEpisodesRvAdapter = ClingSelectEpisodesRvAdapter(nEpisodes)
        clingSelectEpisodesRvAdapter.setOnEpisodeItemClickListener(object : ClingSelectEpisodesRvAdapter.OnEpisodeItemClickListener {
            override fun onEpisodeItemClick(episode: NEpisode, position: Int) {
                iSelectEpisodeDialog.OnSelectEpisodeDialogEpisodeItemClick(episode, position)
            }
        })
        clingSelectEpisodesRvAdapter.setClickState(initClickPosition)
        rvEpisodes.adapter = clingSelectEpisodesRvAdapter

        val padding = 16f
        val stringIntegerHashMap = HashMap<String, Float>()
        stringIntegerHashMap[SuperSpaceItemDecoration.HORIZONTAL_DECORATION] = 15f
        stringIntegerHashMap[SuperSpaceItemDecoration.VERTICAL_DECORATION] = 10f
        stringIntegerHashMap[SuperSpaceItemDecoration.EDGE_PADDING] = padding
        rvEpisodes.addItemDecoration(SuperSpaceItemDecoration(stringIntegerHashMap))
        return contentView
    }

    override fun onShown(dialog: DialogInterface) {
        super.onShown(dialog)
        isShown = true
        iSelectEpisodeDialog.onDialogShown(dialog)
        iSelectEpisodeDialog.OnSelectEpisodeDialogShown(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isShown = false
        iSelectEpisodeDialog.onDialogDismiss(dialog)
        iSelectEpisodeDialog.OnSelectEpisodeDialogDismiss(dialog)
    }

    fun show(manager: FragmentManager, tag: String?, position: Int) {
        super.show(manager, tag)
        initClickPosition = position
        setEpisodeSelectPosition(position)
    }


    fun setEpisodeSelectPosition(position: Int) {
        try {
            clingSelectEpisodesRvAdapter.setClickState(position)
        } catch (e: Exception) {
        }
    }

}