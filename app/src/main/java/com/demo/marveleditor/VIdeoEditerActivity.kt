/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.demo.marveleditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.R
//import com.facebook.drawee.backends.pipeline.Fresco
import com.demo.marveleditor.fragments.OptiMasterProcessorFragment

class VIdeoEditerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Fresco.initialize(this)
        setContentView(R.layout.opti_activity_main)

        val fragment = OptiMasterProcessorFragment()

        val args = Bundle()
        args.putString("position", intent.getStringExtra("path"))
        fragment.setArguments(args)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, fragment).commit()
    }
}
