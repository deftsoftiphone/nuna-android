package com.demo.viewPost.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import com.demo.R
import com.demo.base.BaseActivity
import com.demo.viewPost.clickhandler.CommentListener
import com.demo.viewPost.home.viewModal.ViewPostViewModalFactory
import com.demo.viewPost.utils.ConstantObjects
import com.demo.viewPost.utils.ConstantObjects.toEditable
import com.demo.viewPost.utils.KeyboardHeightProvider
import com.demo.viewPost.utils.linedittext.LineHeightEditText
import com.demo.viewPost.home.viewModal.ViewPostViewModal
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlinx.android.synthetic.main.activity_sendcomments.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
class SendCommentActivity : BaseActivity(), KeyboardHeightProvider.KeyboardHeightObserver,
    KodeinAware, CommentListener {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: ViewPostViewModalFactory by instance()
    private lateinit var mViewModel: ViewPostViewModal

    companion object {
        var addComment: AppCompatEditText? = null
    }

    private lateinit var keyboardHeightProvider: KeyboardHeightProvider

    private var initialY: Float = 0.toFloat()
    private var postId: String? = null


    private var initialheightOfEditText: Int = 0

    private var wasNegative = false

    private var negativePixelHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpOrientation()
        setContentView(R.layout.activity_sendcomments)
        setUpViewModel()
        setUpUI()
        setEmoji()
        /* HeightProvider(this).init()
             .setHeightListener { height ->
                 if (height > 0) {
                     Timer().schedule(250) {
                         bottom_layout.translationY = (-height).toFloat()
                     }
                 } else {
                     Timer().schedule(250) {
                         bottom_layout.translationY = -200f
                     }
                 }
             }*/
    }

    private fun setUpViewModel() {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(ViewPostViewModal::class.java)
        mViewModel.setCommentListener(this@SendCommentActivity)
    }

    private fun setEmoji() {
        emoji_smiley.setOnClickListener {
            val smileEmoji = 0x1F644
            add_comment_main.text!!.insert(
                add_comment_main.selectionStart,
                ConstantObjects.getEmojiByUnicode(smileEmoji)
            )

            //add_comment_main.append(ConstantObjects.getEmojiByUnicode(smileEmoji));
        }

        emoji_cwl.setOnClickListener {
            val cwlEmoji = 0x1F602
            add_comment_main.text!!.insert(
                add_comment_main.selectionStart,
                ConstantObjects.getEmojiByUnicode(cwlEmoji)
            )
            // add_comment_main.append(ConstantObjects.getEmojiByUnicode(cwlEmoji));
        }

        emoji_wink.setOnClickListener {
            val winkEmoji = 0x1F923
            add_comment_main.text!!.insert(
                add_comment_main.selectionStart,
                ConstantObjects.getEmojiByUnicode(winkEmoji)
            )
            //add_comment_main.append(ConstantObjects.getEmojiByUnicode(winkEmoji));
        }

        emoji_kiss.setOnClickListener {
            val kissEmoji = 0x1F618
            add_comment_main.text!!.insert(
                add_comment_main.selectionStart,
                ConstantObjects.getEmojiByUnicode(kissEmoji)
            )
            //add_comment_main.append(ConstantObjects.getEmojiByUnicode(kissEmoji));
        }

        emoji_shy.setOnClickListener {
            val shyEmoji = 0x1F62D
            add_comment_main.text!!.insert(
                add_comment_main.selectionStart,
                ConstantObjects.getEmojiByUnicode(shyEmoji)
            )
            //add_comment_main.append(ConstantObjects.getEmojiByUnicode(shyEmoji));
        }

        emoji_heart.setOnClickListener {
            val heartEmoji = 0x2764
            add_comment_main.text!!.insert(
                add_comment_main.selectionStart,
                ConstantObjects.getEmojiByUnicode(heartEmoji)
            )
            //add_comment_main.append(ConstantObjects.getEmojiByUnicode(heartEmoji));
        }

        emoji_hug.setOnClickListener {
            val hugEmoji = 0x1F600
            add_comment_main.text!!.insert(
                add_comment_main.selectionStart,
                ConstantObjects.getEmojiByUnicode(hugEmoji)
            )
            //add_comment_main.append(ConstantObjects.getEmojiByUnicode(hugEmoji));
        }

        emoji_cool.setOnClickListener {
            val coolEmoji = 0x1F60D
            add_comment_main.text!!.insert(
                add_comment_main.selectionStart,
                ConstantObjects.getEmojiByUnicode(coolEmoji)
            )
            //add_comment_main.append(ConstantObjects.getEmojiByUnicode(coolEmoji));
        }

        send_comment_view.setOnClickListener {
            if (!add_comment_main.text.toString().isNullOrEmpty()) {
                postId?.let {
                    mViewModel.addComment(it, add_comment_main.text.toString())
                    val returnIntent = Intent()
                    returnIntent.putExtra("POST_ID", it)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
        }

        container.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("result", add_comment_main.text.toString())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }


        bottom_layout.setOnClickListener {

        }

        val onKeyPreImeListener: LineHeightEditText.OnKeyPreImeListener =
            LineHeightEditText.OnKeyPreImeListener {
                val returnIntent = Intent()
                returnIntent.putExtra("result", add_comment_main.text.toString())
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        add_comment_main.setOnKeyPreImeListener(onKeyPreImeListener)
    }

    private fun setUpUI() {
        keyboardHeightProvider = KeyboardHeightProvider(this)
        bottom_layout.post {
            initialY = bottom_layout.y
            initialheightOfEditText = add_comment_main.height
        }
        container.post {
            keyboardHeightProvider.start()
        }
        addComment = findViewById(R.id.add_comment_main)
        val comment = intent.getStringExtra("COMMENT_TEXT")
        postId = intent.getStringExtra("POST_ID")
        add_comment_main.text = comment?.toEditable()
        add_comment_main.text?.length?.let { add_comment_main.setSelection(it) }
        showInputMethod()
        add_comment_main!!.requestFocus()
    }

    private fun setUpOrientation() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
//        }

        //android O fix bug orientation
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        AppCenter.start(
            application, "059a3bc2-0e86-4b82-ada0-a46d987d4add",
            Analytics::class.java, Crashes::class.java
        )
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        var y = initialY
        var settledHeight = height
        settledHeight = Math.abs(settledHeight)
        // In case of 18:9
        if (height < 0) {
            wasNegative = true
            negativePixelHeight = settledHeight
        }
        if (settledHeight == 0) { // keyboard is closed
            if (add_comment_main.height != initialheightOfEditText) {
                y += (initialheightOfEditText - add_comment_main.height)
            }
        } else {
            y = if (wasNegative && settledHeight != negativePixelHeight) {
                initialY - (settledHeight + negativePixelHeight)
            } else if (wasNegative) {
                initialY
            } else {
                initialY - settledHeight
            }
            if (add_comment_main.height != initialheightOfEditText) {
                y -= (add_comment_main.height - initialheightOfEditText)
            }
        }
        bottom_layout.y = y
        bottom_layout.requestLayout()
    }

    public override fun onPause() {
        super.onPause()
        keyboardHeightProvider.setKeyboardHeightObserver(null)
    }

    public override fun onResume() {
        super.onResume()
        keyboardHeightProvider.setKeyboardHeightObserver(this)
    }

    public override fun onDestroy() {
        super.onDestroy()
        keyboardHeightProvider.close()
        hideKeyboard()
    }

    override fun setLang(strLang: String) {}

    fun showInputMethod() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    override fun onPostComment() {

    }

}