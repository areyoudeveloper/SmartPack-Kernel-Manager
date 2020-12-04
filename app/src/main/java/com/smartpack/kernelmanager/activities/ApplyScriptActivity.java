/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.smartpack.kernelmanager.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.ScriptManager;
import com.smartpack.kernelmanager.views.dialog.Dialog;

import java.util.ConcurrentModificationException;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 30, 2020
 */

public class ApplyScriptActivity extends AppCompatActivity {

    private boolean mCancelled = false;
    private MaterialTextView mScriptTitle, mOutput;
    private NestedScrollView mScrollView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyscript);

        AppCompatImageButton mBackButton = findViewById(R.id.back_button);
        mScriptTitle = findViewById(R.id.script_name);
        mOutput = findViewById(R.id.result_text);
        mScrollView = findViewById(R.id.scroll_view);

        mScriptTitle.setText(getString(R.string.executing) + " " + ScriptManager.mScriptName);

        mBackButton.setOnClickListener(v -> onBackPressed());
        refreshStatus();
    }

    public void refreshStatus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(() -> {
                            try {
                                mOutput.setText(Utils.getOutput(ScriptManager.mOutput).isEmpty() ? getString(R.string.executing) + " ..." : Utils.getOutput(ScriptManager.mOutput));
                            } catch (ConcurrentModificationException ignored) {}
                            if (ScriptManager.mApplying) {
                                mScriptTitle.setText(getString(R.string.executing));
                                mScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                            } else {
                                mScriptTitle.setText(mCancelled ? getString(R.string.exceute_cancel_title, ScriptManager.mScriptName) : getString(R.string.script_executed, ScriptManager.mScriptName));
                                mOutput.setText(Utils.getOutput(ScriptManager.mOutput).isEmpty() ? getString(R.string.script_executed, ScriptManager.mScriptName) : Utils.getOutput(ScriptManager.mOutput));
                            }
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (ScriptManager.mApplying) {
            new Dialog(this)
                    .setMessage(getString(R.string.exceute_cancel_question, ScriptManager.mScriptName))
                    .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                    })
                    .setPositiveButton(getString(R.string.ok), (dialog1, id1) -> {
                        mCancelled = true;
                        RootUtils.closeSU();
                    }).show();
            return;
        }
        if (mCancelled) {
            mCancelled = false;
        }
        super.onBackPressed();
    }

}