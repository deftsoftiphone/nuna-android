/*
 * Copyright (C) 2017 Hanks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.demo.viewPost.utils.linedittext;

import android.graphics.drawable.ShapeDrawable;

import com.demo.viewPost.home.SendCommentActivity;

import java.util.Objects;

/**
 * Editor cursor
 * Created by hanks on 2017/1/6.
 */

public class LineSpaceCursorDrawable extends ShapeDrawable {
    private int mHeight;

    public LineSpaceCursorDrawable(int cursorColor, int cursorWidth, int cursorHeight) {
        mHeight = cursorHeight;
        setDither(false);
        getPaint().setColor(cursorColor);
        setIntrinsicWidth(cursorWidth);
    }

    /*public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.setBounds(paramInt1, paramInt2, paramInt3, this.mHeight + paramInt2);
    }*/

    public void setBounds(int left, int top, int right, int bottom) {
        if (Objects.requireNonNull(Objects.requireNonNull(SendCommentActivity.Companion.getAddComment()).getText()).toString().endsWith("\n") && SendCommentActivity.Companion.getAddComment().getSelectionStart() == SendCommentActivity.Companion.getAddComment().length()) {
            int lineSpace = (int) SendCommentActivity.Companion.getAddComment().getLineSpacingExtra();
            super.setBounds(left, top + lineSpace, right, this.mHeight + top + lineSpace);
        } else {
            super.setBounds(left, top, right, this.mHeight + top);
        }
    }
}
