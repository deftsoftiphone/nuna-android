package com.demo.language_select

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.demo.databinding.LayoutItemLanguageBinding
import com.demo.model.response.baseResponse.Language
import java.util.*


class LanguageAdapter(
    private val languages: List<Language>,
    val mClickHandler: LanguageSelectFragment.ClickHandler,
    val activity: FragmentActivity?
) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemLanguageBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = languages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(languages[position])
    }

    inner class ViewHolder(val binding: LayoutItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var context: Context = binding.root.context
        fun bind(language: Language) {
            binding.language = language
            binding.clickHandler = ClickHandler()
        }

        inner class ClickHandler {
            fun onSelectLanguage() {
                notifyItemRangeChanged(0, languages.size)
                mClickHandler.onClickLanguage(languages[adapterPosition])
            }
        }

        private fun translate(locale: Locale?, resId: Int): String? {
            val config =
                Configuration(context.resources.configuration)
            config.setLocale(locale)
            return context.createConfigurationContext(config).getText(resId).toString()
        }
    }
}