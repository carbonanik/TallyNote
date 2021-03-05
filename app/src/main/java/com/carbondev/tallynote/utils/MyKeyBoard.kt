package com.carbondev.tallynote.utils

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import com.carbondev.tallynote.R
import kotlinx.android.synthetic.main.calculator_keybord.view.*
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.truncate

private const val ZERO: String = "0"
private const val ONE: String = "1"
private const val TWO: String = "2"
private const val THREE: String = "3"
private const val FOUR: String = "4"
private const val FIVE: String = "5"
private const val SIX: String = "6"
private const val SEVEN: String = "7"
private const val EIGHT: String = "8"
private const val NINE: String = "9"
private const val DECIMAL: String = "."

private const val addition = " + "
private const val subtraction = " - "
private const val multiplication = " * "
private const val division = " / "

class MyKeyBoard(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {
    // constructors
    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

//    private enum class ClickType {
//        OPERATOR,
//        NUMBER,
//        EQUAL,
//        PERCENTAGE,
//        NON
//    }

//    private var lastClick = ClickType.NON
    private var equalPressed = false

//    private var lastOperator = Operator.ADDITION

    private var inputConnection: InputConnection? = null
    private fun init(context: Context?, attrs: AttributeSet?) {

        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.calculator_keybord, this, true)

        // set button click listeners
        btn_1.setOnClickListener{
            numberClick(ONE)
        }
        btn_2.setOnClickListener{
            numberClick(TWO)
        }
        btn_3.setOnClickListener{
            numberClick(THREE)
        }
        btn_4.setOnClickListener{
            numberClick(FOUR)
        }
        btn_5.setOnClickListener{
            numberClick(FIVE)
        }
        btn_6.setOnClickListener{
            numberClick(SIX)
        }
        btn_7.setOnClickListener{
            numberClick(SEVEN)
        }
        btn_8.setOnClickListener{
            numberClick(EIGHT)
        }
        btn_9.setOnClickListener{
            numberClick(NINE)
        }
        btn_0.setOnClickListener {
            numberClick(ZERO)
        }
        btn_dot.setOnClickListener{
            numberClick(DECIMAL)
        }

        btn_add.setOnClickListener{
            operatorPress(addition)
        }
        btn_sub.setOnClickListener{
            operatorPress(subtraction)
        }
        btn_mul.setOnClickListener{
            operatorPress(multiplication)
        }
        btn_div.setOnClickListener{
            operatorPress(division)
        }

        btn_per.setOnClickListener {
            percentageClick()
        }

        btn_equal.setOnClickListener {
            equalPress()
        }

        btn_back.setOnClickListener{
            backspace()
        }

        btn_ac.setOnClickListener {
            ac()
        }
    }

    private fun numberClick(number: String) {
        if (equalPressed) {
            ac() // todo
        }
        inputConnection!!.commitText(number, 1)
        equalPressed = false
    }

    private fun operatorPress(operator: String) {
        val t = textFromField()
        val t2 = removeLastOperator(t)
        commitResult(t2)
        equalPress()
        inputConnection!!.commitText(operator, 1)
        equalPressed = false
    }

    private fun equalPress(){
        val r = calculateResult(textFromField())
        commitResult(r)
        equalPressed = true
    }

    fun publicEqualButton(){
        equalPress()
    }

    private fun calculateResult(str: String): String {
        val s = removeLastOperator(str)
        val e: Expression = ExpressionBuilder(s).build()

        val an = e.evaluate()

        val ans = if (an%truncate(an) == 0.0) {
            an.toString()
        } else {
            val ret = BigDecimal(an)
            val r = ret.setScale(2, RoundingMode.HALF_UP)
            r.toString()
        }
        return removeDotZero(ans)
    }


    private fun percentageClick() {
        percentageOfLast(textFromField())
//        if (lastClick == ClickType.NUMBER && (lastOperator == Operator.ADDITION || lastOperator == Operator.SUBTRACTION)) {
//            val pl = percentageOfLast(textFromField())
//            commitResult(pl)
//        }
//        lastClick = ClickType.PERCENTAGE
    }

    private fun percentageOfLast(strExp: String) {
        val s = strExp.split(addition, subtraction, multiplication, division).map { it.trim() }
        if (s.size >= 2) {
            if (s[1].isNotBlank()) {
                val p = percentage(s[s.size - 1].toDouble(), s[s.size - 2].toDouble())
                val rs = strExp.removeSuffix(s[s.size - 1])
                commitResult(rs + removeDotZero(p))
            }
        }
    }

    private fun percentage(n: Double, p: Double): String {
        val e: Expression = ExpressionBuilder("(x${multiplication}y)${division}100")
            .variables("x", "y")
            .build()
            .setVariable("x", n)
            .setVariable("y", p)
        return e.evaluate().toString()
    }


    private fun commitResult(currentResult: String) {
        clearField()
        inputConnection!!.commitText(currentResult, 1)
    }

    private fun removeDotZero(n: String): String {
        val suffix = ".0"
        return when {
            n.endsWith(suffix) -> {
                n.removeSuffix(suffix)
            }
            else -> {
                n
            }
        }
    }

    private fun removeLastOperator(str: String): String {
        return when {
            str.endsWith(addition)
                    || str.endsWith(subtraction)
                    || str.endsWith(multiplication)
                    || str.endsWith(division) ->
            {
                str.dropLast(3)
            }
            else -> str
        }
    }

    private fun textFromField(): String {
        val currentText = inputConnection!!.getExtractedText(ExtractedTextRequest(), 0).text
        val beforeCursorText = inputConnection!!.getTextBeforeCursor(currentText.length, 0)
        val afterCursorText = inputConnection!!.getTextAfterCursor(currentText.length, 0)
        return beforeCursorText.toString() + afterCursorText.toString()
    }

    private fun backspace(){
        val selectedText = inputConnection!!.getSelectedText(0)
        if (TextUtils.isEmpty(selectedText)) {
            val str = textFromField()
            if (equalPressed) {
                ac()
            } else if (str.endsWith(addition)
                || str.endsWith(subtraction)
                || str.endsWith(multiplication)
                || str.endsWith(division))
            {
                inputConnection!!.deleteSurroundingText(3, 0)
            } else {
                inputConnection!!.deleteSurroundingText(1, 0)
            }
        } else {
            inputConnection!!.commitText("", 1)
        }

        equalPressed = false

    }

    private fun ac(){
        clearField()
//        equalPressed = false
//        lastOperator = Operator.ADDITION
//        lastClick = ClickType.NON
//        cal.initNum(ZERO)
//        currentInput = ""
    }

    private fun clearField(){
        val currentText = inputConnection!!.getExtractedText(ExtractedTextRequest(), 0).text
        val beforeCursorText = inputConnection!!.getTextBeforeCursor(currentText.length, 0)
        val afterCursorText = inputConnection!!.getTextAfterCursor(currentText.length, 0)
        inputConnection!!.deleteSurroundingText(
            beforeCursorText.length,
            afterCursorText.length
        )
    }

    fun setInputConnection(ic: InputConnection?) {
        inputConnection = ic
    }

    init {
        init(context, attrs)
    }
}