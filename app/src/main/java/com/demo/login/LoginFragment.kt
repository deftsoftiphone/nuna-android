package com.demo.login

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.transition.TransitionManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentLoginBinding
import com.demo.login.CountryCodeDialogFragment.CountryCodeClickListener
import com.demo.login.CountryCodeDialogFragment.CountryCodeDialogFragment
import com.demo.model.request.RequestUserRegister
import com.demo.model.response.baseResponse.Country
import com.demo.util.ParcelKeys
import com.demo.util.Util
import com.demo.util.Validator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.IOException
import java.io.InputStream
import java.util.*

class LoginFragment : BaseFragment(), KodeinAware, CountryCodeClickListener {

    override val kodein by lazy { (context?.applicationContext as KodeinAware).kodein }
    private val viewModelFactory: LoginViewModelFactory by instance()
    lateinit var mBinding: FragmentLoginBinding
    lateinit var mViewModel: LoginViewModel
    private var countries: ArrayList<Country>? = null
    private var keyboardListener: Unregistrar? = null

    override fun onResume() {
        super.onResume()
        mBinding.etPhone.requestFocus()
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(mBinding.etPhone, 0)
        keyboardShowListener()
    }

    override fun onPause() {
        super.onPause()
        keyboardListener?.unregister()
    }

    private fun keyboardShowListener() {
        keyboardListener = KeyboardVisibilityEvent.registerEventListener(
            activity,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) removeConstraint() else addConstraint()
                    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            })
    }

    private fun removeConstraint() {
        val set = ConstraintSet()
        set.clone(mBinding.clParent)
        TransitionManager.beginDelayedTransition(mBinding.clParent)
        set.clear(R.id.llContinue, ConstraintSet.TOP)
        set.applyTo(mBinding.clParent)
    }

    private fun addConstraint() {
        val set = ConstraintSet()
        set.clone(mBinding.clParent)
        TransitionManager.beginDelayedTransition(mBinding.clParent)

        set.connect(R.id.llContinue, ConstraintSet.TOP, R.id.rlText2, ConstraintSet.BOTTOM)
        set.applyTo(mBinding.clParent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        countries = loadCountries()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }
        setupTnCAndPP()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
        Util.updateStatusBarColor(true, "#FFFFFF", activity as FragmentActivity)
        mBinding.tvCode.text = "+91"
        return mBinding.root
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }


    private fun login() = launch {
        commonCallbacks?.showProgressDialog()
        mViewModel.login().observe(viewLifecycleOwner, Observer {
            commonCallbacks?.hideProgressDialog()
            if (it.error == null && it.success!!) {
                val bundle = bundleOf(
                    ParcelKeys.PHONENUMBER to mViewModel.requestRegister.get()!!.mobileNo.toString(),
                    ParcelKeys.COUNTRYCODE to mBinding.tvCode.text.toString()
                )
                findNavController().navigate(R.id.OtpFragment, bundle)
            } else if (it.error != null) {
                handleAPIError(it.error!!.message.toString())
            }
        })
    }

    inner class ClickHandler {
        fun backPress() {
            findNavController().navigateUp()
        }

        fun onClickLogin() {
            mViewModel.requestRegister.apply {
                set(
                    RequestUserRegister(
                        countryCode = mBinding.tvCode.text.toString(),
                        mobileNo = if (!TextUtils.isEmpty(mBinding.etPhone.text.toString()) && mBinding.etPhone.text.toString().length == 10)
                            mBinding.etPhone.text.toString() else ""
//                        fcmToken = Prefs.init().deviceToken
                    )
                )
            }
            commonCallbacks?.hideKeyboard()

            if (isPhoneValid(mBinding.etPhone.text.toString(), mViewModel.errPhoneNumber)) {
                login()
            }
        }

        fun showCountries() {
            val fragment = CountryCodeDialogFragment(this@LoginFragment)
            val bundle = Bundle()
            bundle.putParcelableArrayList(ParcelKeys.COUNTRY_NAME_CODE, countries)
            fragment.arguments = bundle
            val activity = requireActivity() as AccountHandlerActivity
            val ft = activity.supportFragmentManager.beginTransaction()
            val prev = activity.supportFragmentManager.findFragmentByTag("countryDialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            fragment.show(ft, "countryDialog")
        }
    }

    private fun loadCountries(): ArrayList<Country>? {
        var json: String? = null
        json = try {
            val stream: InputStream =
                (requireActivity() as AccountHandlerActivity).assets.open("country_codes.json")
            val size: Int = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return Gson().fromJson(
            json,
            object : TypeToken<ArrayList<Country>?>() {}.type
        ) as ArrayList<Country>
    }

    fun isPhoneValid(phone: String?, errMsgHolder: ObservableField<String>): Boolean {
        println("phone = [${phone}], errMsgHolder = [${errMsgHolder}]")
        if (phone?.isEmpty() == true) {
            println("LoginFragment.isPhoneValid")
            errMsgHolder.set(getString(R.string.emptyMobileNoErr))
            Validator.showMessage(errMsgHolder.get() ?: "")
            return false
        } else errMsgHolder.set("")

        if (phone!!.length != 10) {
            errMsgHolder.set(getString(R.string.err_mobile_missing))
            Validator.showMessage(errMsgHolder.get() ?: "")
            return false
        } else errMsgHolder.set("")


        if (phone == "0000000000") {
            errMsgHolder.set(getString(R.string.invalid_mobile))
            Validator.showMessage(errMsgHolder.get() ?: "")
            return false
        } else errMsgHolder.set("")
        return true
    }

    private fun setupTnCAndPP() {
        val parentText = SpannableString(getString(R.string.t_n_c_and_privacy_policy))
        val tnCSpan: ClickableSpan = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = resources.getColor(R.color.hint_grey)
            }

            override fun onClick(v: View) {
                openHtml(R.string.terms_amp_conditions, "file:///android_asset/terms_of_use.html")
            }
        }
        val ppSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = resources.getColor(R.color.hint_grey)
            }

            override fun onClick(v: View) {
                openHtml(R.string.privacy_policy, "file:///android_asset/privacy_police_new.html")
            }
        }
        parentText.setSpan(tnCSpan, 16, 34, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        parentText.setSpan(ppSpan, 43, 57, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        mBinding.tvText2.text = parentText
        mBinding.tvText2.movementMethod = LinkMovementMethod.getInstance();
        mBinding.tvText2.highlightColor = resources.getColor(android.R.color.transparent)
    }

    private fun openHtml(title: Int, fileUrl: String) {
        val bundle = bundleOf(
            getString(R.string.toolbarTitle) to getString(title),
            getString(R.string.fileURL) to fileUrl
        )
        navigate(R.id.infoFragment, bundle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
        }
        return true
    }

//    override fun onFragBack(): Boolean {
//        findNavController().navigateUp()
//        return true
//    }

    override fun onCountryCodeSelected(code: String) {
        mBinding.tvCode.text = code
    }
}