/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.demo.marveleditor.interfaces

import com.demo.marveleditor.fragments.OptiBaseCreatorDialogFragment
import java.io.File

interface OptiDialogueHelper {
    fun setHelper(helper: OptiBaseCreatorDialogFragment.CallBacks)
    fun setMode(mode: Int)
    fun setFilePathFromSource(file: File)
    fun setDuration(duration: Long)
}
