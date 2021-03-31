package com.demo.login.CountryCodeDialogFragment

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.R
import com.demo.model.response.baseResponse.Country
import com.demo.util.ParcelKeys
import kotlinx.android.synthetic.main.fragment_dialog.view.*

class CountryCodeDialogFragment(
    private val clickListener: CountryCodeClickListener
) : DialogFragment() {
    private lateinit var parent: View
    private lateinit var countries: ArrayList<Country>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countries = arguments?.getParcelableArrayList<Country>(ParcelKeys.COUNTRY_NAME_CODE)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parent = inflater.inflate(R.layout.fragment_dialog, container, false)
        parent.rvCountries.layoutManager = LinearLayoutManager(requireContext())
        parent.rvCountries.adapter =
            CountryCodeRecyclerAdapter(countries.toList(), object : CountryCodeClickListener {
                override fun onCountryCodeSelected(code: String) {
                    clickListener.onCountryCodeSelected(code)
                    dialog?.dismiss()
                }
            })

        return parent
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val displayMetrics = DisplayMetrics()
            dialog.window!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            val dialogWindowWidth = (displayMetrics.widthPixels * 0.9f).toInt()
            val dialogWindowHeight = (displayMetrics.heightPixels * 0.7f).toInt()
            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight
            dialog.window!!.attributes = layoutParams
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}