package com.example.pcControl;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.pcControl.data.References;

public class CustomAttrs extends androidx.appcompat.widget.AppCompatButton {
    private static final int[] STATE_CTRL_ACTIVE = {R.attr.state_ctrl_active};
    private static final int[] STATE_INPUT_ACTIVE = {R.attr.state_input_active};

    private boolean sCtrlActive = false;
    private boolean sInputActive = false;

    public CustomAttrs(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCtrlActive(boolean isActive) {sCtrlActive = isActive;}
    public void setinputActive(boolean isActive) {sInputActive = isActive;}

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (sCtrlActive) {
            mergeDrawableStates(drawableState, STATE_CTRL_ACTIVE);
        }
        if (sInputActive) {
            mergeDrawableStates(drawableState, STATE_INPUT_ACTIVE);
        }
        References.handler.post(new Runnable() {
            @Override
            public void run() {
                refreshDrawableState();
            }
        });
        return drawableState;
    }
}
