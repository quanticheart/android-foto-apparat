/*
 *
 *  *                                     /@
 *  *                      __        __   /\/
 *  *                     /==\      /  \_/\/
 *  *                   /======\    \/\__ \__
 *  *                 /==/\  /\==\    /\_|__ \
 *  *              /==/    ||    \=\ / / / /_/
 *  *            /=/    /\ || /\   \=\/ /
 *  *         /===/   /   \||/   \   \===\
 *  *       /===/   /_________________ \===\
 *  *    /====/   / |                /  \====\
 *  *  /====/   /   |  _________    /      \===\
 *  *  /==/   /     | /   /  \ / / /         /===/
 *  * |===| /       |/   /____/ / /         /===/
 *  *  \==\             /\   / / /          /===/
 *  *  \===\__    \    /  \ / / /   /      /===/   ____                    __  _         __  __                __
 *  *    \==\ \    \\ /____/   /_\ //     /===/   / __ \__  ______  ____ _/ /_(_)____   / / / /__  ____ ______/ /_
 *  *    \===\ \   \\\\\\\/   ///////     /===/  / / / / / / / __ \/ __ `/ __/ / ___/  / /_/ / _ \/ __ `/ ___/ __/
 *  *      \==\/     \\\\/ / //////       /==/  / /_/ / /_/ / / / / /_/ / /_/ / /__   / __  /  __/ /_/ / /  / /_
 *  *      \==\     _ \\/ / /////        |==/   \___\_\__,_/_/ /_/\__,_/\__/_/\___/  /_/ /_/\___/\__,_/_/   \__/
 *  *        \==\  / \ / / ///          /===/
 *  *        \==\ /   / / /________/    /==/
 *  *          \==\  /               | /==/
 *  *          \=\  /________________|/=/
 *  *            \==\     _____     /==/
 *  *           / \===\   \   /   /===/
 *  *          / / /\===\  \_/  /===/
 *  *         / / /   \====\ /====/
 *  *        / / /      \===|===/
 *  *        |/_/         \===/
 *  *                       =
 *  *
 *  * Copyright(c) Developed by John Alves at 2020/2/22 at 0:4:2 for quantic heart studios
 *
 */

package com.quanticheart.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quanticheart.camera.file.ImageDataModel
import kotlinx.android.synthetic.main.galery_item.view.*

class GaleryAdapter(private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<GaleryAdapter.GaleryViewHolder>() {

    init {
        recyclerView.apply {
            layoutManager = GridLayoutManager(recyclerView.context, 3)
            adapter = this@GaleryAdapter
        }
    }

    private val database = ArrayList<ImageDataModel>()

    fun addData(dataList: List<ImageDataModel>) {
        if (dataList.isNotEmpty()) {
            database.clear()
            database.addAll(dataList)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GaleryViewHolder =
        GaleryViewHolder(
            LayoutInflater.from(recyclerView.context).inflate(
                R.layout.galery_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = database.size

    override fun onBindViewHolder(holder: GaleryViewHolder, position: Int) {
        holder.bind(database[position])
    }

    class GaleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ImageDataModel) {
            Glide.with(itemView.context).load(data.path).into(itemView.img)
        }
    }
}