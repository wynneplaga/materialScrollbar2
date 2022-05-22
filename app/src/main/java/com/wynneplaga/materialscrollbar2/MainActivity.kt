package com.wynneplaga.materialscrollbar2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wynneplaga.materialscrollbar2.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityMainBinding.inflate(layoutInflater).apply {
            refresher.isRefreshing = true
            recyclerView.adapter = MainAdapter().apply {
                lifecycleScope.launch {
                    delay(2000)
                    refresher.isRefreshing = false
                    submitList((0..23).map { 'A' + it }.map(Char::toString).mapIndexed { i, s -> s.repeat(i + 1) })
                }
            }

            setContentView(root)
        }
    }

}