package com.demo.post_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.demo.R
import com.demo.databinding.LayoutPinboardItemBinding
import com.demo.model.Pinboard


class AddToBoardAdapter(context: Context, val rowLayoutId: Int, val dataList: List<Pinboard>) :
    ArrayAdapter<Pinboard>(context, rowLayoutId, dataList) {

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Pinboard {
        return dataList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            val binding = LayoutPinboardItemBinding.inflate(inflater, parent, false).apply {
                pinboard = dataList[position]
            }
            return binding.root
        }else{
            DataBindingUtil.getBinding<LayoutPinboardItemBinding>(convertView)?.apply {
                pinboard = dataList[position]
            }
            return convertView
        }
    }


}