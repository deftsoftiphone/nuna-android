package com.demo.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MainApplication
import com.demo.base.MyViewModelProvider
import com.demo.boardList.MyBoardsFragment
import com.demo.dashboard_search.DashboardSearchViewModel
import com.demo.dashboard_search.DashboardViewModelFactory
import com.demo.databinding.FragmentProfileBinding
import com.demo.databinding.LayoutAddToBoardDialogBinding
import com.demo.postList.PostListFragment
import com.demo.postList.PostsViewModel
import com.demo.util.ParcelKeys
import com.demo.util.Util
import com.demo.webservice.ApiRegister
import com.google.android.material.textfield.TextInputLayout
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ProfileFragment : BaseFragment() {
    lateinit var mBinding: FragmentProfileBinding
    lateinit var mViewModel: ProfileViewModel

    lateinit var mPagerAdapter: ViewPagerAdapter
    var addNewBoardNameDialog: Dialog? = null
    val mClickHandler by lazy { ClickHandler() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = FragmentProfileBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@ProfileFragment
                viewModel = mViewModel
                clickHandler = ClickHandler()
            }


            Util.updateStatusBarColor("#FAFAFA", activity as FragmentActivity)

            commonCallbacks?.setupToolBar(
                mBinding.toolbarLayout,
                false,
                getString(R.string.my_profile)
            )
            setHasOptionsMenu(true)

        }
//        mViewModel.callGetProfileApi()
        return mBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewPager()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mi_settings) {
            findNavController().navigate(R.id.SettingsFragment)
            return true
        } else return super.onOptionsItemSelected(item)
    }


    private fun setupViewPager() {
        mPagerAdapter = ViewPagerAdapter(childFragmentManager)

       // mBinding.viewpager.currentItem = 0
        mBinding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    mBinding.tvMyBoards.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    mBinding.tvMyPosts.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_grey
                        )
                    )
                    mBinding.btnAddBoard.setEnabled(true)
                    mBinding.btnAddBoard.setImageResource(R.drawable.add_board)
                } else {
                    mBinding.btnAddBoard.setEnabled(false)
                    mBinding.btnAddBoard.setImageResource(R.drawable.add_board_non_active)
                    mBinding.tvMyPosts.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    mBinding.tvMyBoards.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_grey
                        )
                    )
                }
            }
        })
        mBinding.viewpager.apply {
            adapter = mPagerAdapter
        }
        mBinding.viewpager.setCurrentItem(1)

    }

    private fun setupViewModel() {


        mViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(ProfileViewModel::class.java)
    }

    private fun setupObserver() {
        mViewModel.responseGetProfile.observe(this, Observer {
            Util.log("")
            var string:String

            string= it.data?.bio.toString()
            string = string.replace("null","Hey i am using Nuna")

            mBinding.tvTagline.setText(string)

        })

        mViewModel.responseAddPinboard.observe(this, Observer {
            if (it.data != null) {
                (mPagerAdapter.getItem(0) as MyBoardsFragment).addNewBoard(it.data!!)
            }
        })
    }

    inner class ClickHandler {

        fun onClickAddBoard() {
            val builder = AlertDialog.Builder(requireContext())

            LayoutAddToBoardDialogBinding.inflate(layoutInflater).apply {
                pinboardName = ""

                clickHandler = View.OnClickListener {
                    var input = ""


                    addNewBoardNameDialog?.findViewById<ImageButton>(R.id.button_cross)?.apply {

                        addNewBoardNameDialog?.dismiss()
                        addNewBoardNameDialog?.cancel()
                    }

                    val til =
                        addNewBoardNameDialog?.findViewById<TextInputLayout>(R.id.til_name)?.apply {
                            input = editText?.text.toString()
                        }

                    if (input.isBlank()) {
                        til?.error = getString(R.string.err_board_name_missing)
                        return@OnClickListener
                    }


                    til?.error = ""
                    addNewBoardNameDialog?.dismiss()
//                    mViewModel.callAddPinboardApi(input)
                    commonCallbacks?.hideKeyboard()
                }
                builder.setView(this.root)
            }

            addNewBoardNameDialog = builder.create()
            addNewBoardNameDialog?.show()
        }

        fun onClickTab1() {
            mBinding.viewpager.currentItem = 0
        }

        fun onClickTab2() {
            mBinding.viewpager.currentItem = 1
        }
    }

    override fun onApiRequestFailed(apiUrl: String, errCode: Int, errorMessage: String): Boolean {
        if (apiUrl == ApiRegister.GET_USER_PINBOARD_LIST) {
            (mPagerAdapter.getItem(0) as? MyBoardsFragment)?.onApiRequestFailed(
                apiUrl,
                errCode,
                errorMessage
            )
            return true
        } else
            return super.onApiRequestFailed(apiUrl, errCode, errorMessage)
    }

    @SuppressLint("WrongConstant")
    inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val mFragmentList: List<Fragment>

        init {
            mFragmentList = listOf(MyBoardsFragment(), PostListFragment().apply {
                arguments = bundleOf(ParcelKeys.PK_LIST_TYPE to PostsViewModel.LAYOUT_TYPE_VERTICAL)
            })
        }


        private val mFragmentTitleList: List<String> = listOf(
            MainApplication.get().getContext().getString(R.string.my_boards),
            MainApplication.get().getContext().getString(R.string.my_posts)
        )

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int = mFragmentList.size

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
            try {
                super.restoreState(state, loader)
            } catch (e: Exception) {
                Log.e("TAG", "Error Restore State of Fragment : " + e.message, e)
            }
        }
    }

    override fun onDestroyView() {
        mBinding.viewpager.adapter = null
        super.onDestroyView()
    }

}
