package com.example.danielfajardo_calculadora

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    var text_resultado : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        text_resultado = findViewById(R.id.text_resultado)

    }

    fun calcular(view: View) {
        var boton = view as Button
        var texto_boton = boton.text.toString()
        var texto_actual = text_resultado?.text.toString()

        // Si el botón presionado es el igual
        if (texto_boton == "=") {
            var resultado = 0.0
            try {
                // Intentar evaluar la expresión
                resultado = eval(texto_actual)
                // Mostrar el resultado en el TextView
                text_resultado?.text = resultado.toString()
            } catch (e: Exception) {
                // Si ocurre un error, mostrar el error
                text_resultado?.text = "Error"
            }
        } else if (texto_boton == "AC") {
            // Si es el botón de "AC", limpiar la pantalla
            text_resultado?.text = "0"
        } else {

            var concadenar = texto_actual + texto_boton
            text_resultado?.text = eliminar_ceros(concadenar)
        }
    }


    fun eliminar_ceros(str: String): String {
        var i = 0
        while (i < str.length && str[i] == '0') {
            i++
        }
        val sb = StringBuilder(str)
        sb.replace(0, i, "")
        return sb.toString()
    }

    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < str.length) str[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }


            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.toInt())) x += parseTerm() // addition
                    else if (eat('-'.toInt())) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.toInt())) x *= parseFactor() // multiplication
                    else if (eat('/'.toInt())) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.toInt())) return parseFactor() // unary plus
                if (eat('-'.toInt())) return -parseFactor() // unary minus
                var x: Double
                val startPos = pos
                if (eat('('.toInt())) {
                    x = parseExpression()
                    eat(')'.toInt())
                } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) {
                    while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) { // functions
                    while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x = if (func == "sqrt") Math.sqrt(x) else if (func == "sin") Math.sin(Math.toRadians(x)) else if (func == "cos") Math.cos(Math.toRadians(x)) else if (func == "tan") Math.tan(Math.toRadians(x)) else throw RuntimeException("Unknown function: $func")
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                if (eat('^'.toInt())) x = Math.pow(x, parseFactor())
                return x
            }
        }.parse()
    }
}
