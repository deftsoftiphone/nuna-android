package com.demo.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentHelpPagesBinding
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.demo.util.selectLanguage
import kotlinx.android.synthetic.main.fragment_help_pages.*

class HelpFragment : BaseFragment() {
    lateinit var binding: FragmentHelpPagesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHelpPagesBinding.inflate(inflater, container, false)

        requireContext().selectLanguage(Prefs.init().selectedLang.languageIntId)


        if (arguments?.getString(ParcelKeys.PK_PAGE).equals("About_us")) {

            binding.description.loadUrl("file:///android_asset/about.html")
            binding.titleToolbar.setText(R.string.about_app)

        } else if (arguments?.getString(ParcelKeys.PK_PAGE).equals("Content_Policy")) {

            binding.description.loadUrl("file:///android_asset/content.html")

            binding.titleToolbar.setText(R.string.cont_polcy)


        } else if (arguments?.getString(ParcelKeys.PK_PAGE).equals("Privacy_Policy")) {
            binding.description.loadUrl("file:///android_asset/privacy_policy_2.html")

            binding.titleToolbar.setText(R.string.privacy_policy)


        } else if (arguments?.getString(ParcelKeys.PK_PAGE).equals("Terms_Conditions")) {

            binding.description.loadUrl("file:///android_asset/terms_of_use.html")

            binding.titleToolbar.setText(R.string.terms_amp_conditions)

        } else if (arguments?.getString(ParcelKeys.PK_PAGE).equals("Reward")) {


            /*if (selectedLanguage == 1) {
                (activity as LocalizationActivity).setLanguage("en")
            } else if (selectedLanguage == 2) {
                (activity as LocalizationActivity).setLanguage("hi")
            } else if (selectedLanguage == 5) {
                (activity as LocalizationActivity).setLanguage("bn")
            } else if (selectedLanguage == 4) {
                (activity as LocalizationActivity).setLanguage("mr")
            } else if (selectedLanguage == 8) {
                (activity as LocalizationActivity).setLanguage("te")
            } else if (selectedLanguage == 6) {
                (activity as LocalizationActivity).setLanguage("ta")
            } else if (selectedLanguage == 3) {
                (activity as LocalizationActivity).setLanguage("gu")
            } else if (selectedLanguage == 7) {
                (activity as LocalizationActivity).setLanguage("kn")
            }
*/

            if (Prefs.init().selectedLangId == 2) {
                binding.description.loadUrl("file:///android_asset/redeem_points_hindi.html")

            } else if (Prefs.init().selectedLangId == 5) {
                binding.description.loadUrl("file:///android_asset/redeem_points_bengali.html")
            } else if (Prefs.init().selectedLangId == 4) {
                binding.description.loadUrl("file:///android_asset/redeem_points_marathi.html")

            } else if (Prefs.init().selectedLangId == 8) {
                binding.description.loadUrl("file:///android_asset/redeem_points_telugu.html")

            } else if (Prefs.init().selectedLangId == 6) {
                binding.description.loadUrl("file:///android_asset/redeem_points_tamil.html")

            } else if (Prefs.init().selectedLangId == 3) {
                binding.description.loadUrl("file:///android_asset/redeem_points_gujarati.html")

            } else if (Prefs.init().selectedLangId == 7) {
                binding.description.loadUrl("file:///android_asset/redeem_points_kannada.html")


            } else if (Prefs.init().selectedLangId == 1) {
                binding.description.loadUrl("file:///android_asset/redeem_points_english.html")
            }


            binding.titleToolbar.setText(R.string.redeem_likes)


        }

        binding.back.setOnClickListener {
            onFragBack()
        }

        return binding.root
    }


}