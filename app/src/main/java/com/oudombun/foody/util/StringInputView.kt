package com.oudombun.foody.util

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import com.oudombun.foody.R
import kotlinx.android.synthetic.main.edit_text_input.view.inputText
import kotlinx.android.synthetic.main.edit_text_input.view.labelInput
import kotlinx.android.synthetic.main.edit_text_input.view.rightIcon
import kotlinx.android.synthetic.main.edit_text_input.view.rightLabel
import kotlinx.android.synthetic.main.edit_text_input.view.startIconView
import kotlinx.android.synthetic.main.edit_text_input.view.stringText


class StringInputView : LinearLayout {

    private lateinit var onSearchActionListener: OnSearchActionListener

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        initView()
        initAttr(context, attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.edit_text_input, this)
    }

    private fun initAttr(
        context: Context?,
        attrs: AttributeSet?
    ) {
        val attrView = context!!.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StringInputView,
            0, 0
        )
        try {
            val label = attrView.getString(R.styleable.StringInputView_label)
            val hint = attrView.getString(R.styleable.StringInputView_hint)
            val icon = attrView.getDrawable(R.styleable.StringInputView_rightIcon)
            val startIcon = attrView.getDrawable(R.styleable.StringInputView_startIcon)
            val inputType =
                attrView.getInt(R.styleable.StringInputView_inputType, InputType.TYPE_CLASS_TEXT)
            val imeOptions = attrView.getInt(
                R.styleable.StringInputView_viewImeOptions,
                EditorInfo.IME_ACTION_DONE
            )
            val isEnabled = attrView.getBoolean(R.styleable.StringInputView_isViewEnable, true)
            val editableView = attrView.getBoolean(R.styleable.StringInputView_editableView, false)
            inputText.inputType = inputType
            if (label == null) {
                labelInput.visibility = View.GONE
            } else {
                labelInput?.text = label
            }

            if (startIcon != null) {
                startIconView.visibility = View.VISIBLE
                startIconView.setImageDrawable(startIcon)
            }

            if (isEnabled) {
                inputText.hint = hint
                inputText.imeOptions = imeOptions
            } else {
                stringText.visibility = View.VISIBLE
                stringText.hint = hint
                inputText.visibility = View.GONE
            }

            if (editableView) {
                rightIcon.setImageResource(R.drawable.ic_field_editable)
                inputText.isEnabled = false
                rightIcon.visibility = View.VISIBLE
            } else {
                // in case for password toggle only, this is ugly please refactor next time
                if (icon != null) {
                    rightIcon.setImageDrawable(icon)
                    rightIcon.visibility = View.VISIBLE
                    rightIcon.setOnClickListener {
                        if (inputText.inputType == InputType.TYPE_CLASS_TEXT) {
                            inputText.setTextAppearance(R.style.FontNormal)
                            inputText.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            inputText.setSelection(inputText.length())
                            rightIcon.setImageResource(R.drawable.ic_show_password)
                        } else {
                            inputText.setTextAppearance(R.style.FontNormal)
                            inputText.inputType = InputType.TYPE_CLASS_TEXT
                            inputText.setSelection(inputText.length())
                            rightIcon.setImageResource(R.drawable.ic_toggle_password)
                        }
                    }
                }
            }
        } finally {
            attrView.recycle()
        }
    }

    fun setFocus() {
        inputText.requestFocus()
        val imm: InputMethodManager? =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun setText(text: String?) {
        inputText.setText(text)
        stringText.text = text
    }

    fun initSearchListener(onSearchActionListener: OnSearchActionListener) {
        this.onSearchActionListener = onSearchActionListener
        inputText.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchActionListener.onSearchClicked(inputText.text.toString())
                inputText.clearFocus()
                handled = true
            }
            handled
        }
    }

    fun inputView() = inputText!!
    fun value() = inputText.text!!.toString()
    fun setValue(value: String) {
        inputText.setText(value)
    }
    fun showRightLabel(): TextView {
        rightLabel.visibility = View.VISIBLE
        return rightLabel
    }

}

interface OnSearchActionListener {
    fun onSearchClicked(keyword: String)
}
