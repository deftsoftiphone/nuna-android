package com.demo.login.CountryCodeDialogFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.demo.databinding.LayoutCountryCodeRecyclerItemBinding
import com.demo.model.response.baseResponse.Country

class CountryCodeRecyclerAdapter(
    private val countries: List<Country>,
    private val clickListener: CountryCodeClickListener
) :
    RecyclerView.Adapter<CountryCodeRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutCountryCodeRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { clickListener.onCountryCodeSelected(code = countries[adapterPosition].dial_code) }
        }

        fun bind(country: String) {
            binding.country = country
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutCountryCodeRecyclerItemBinding.inflate(inflater)
        val params = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        binding.root.layoutParams = params
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return countries.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]
        val value = "${country.name} (${country.dial_code})"
        holder.bind(value)
    }
}