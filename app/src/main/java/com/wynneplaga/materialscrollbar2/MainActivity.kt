package com.wynneplaga.materialscrollbar2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wynneplaga.materialscrollbar2.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityMainBinding.inflate(layoutInflater).apply {
            recyclerView.adapter = MainAdapter().apply {
                submitList((0..23).map { 'A' + it }.map(Char::toString).mapIndexed { i, s -> s.repeat(i + 1) })
            }

            setContentView(root)
        }
    }

}