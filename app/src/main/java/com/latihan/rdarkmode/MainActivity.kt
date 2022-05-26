package com.latihan.rdarkmode


import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.latihan.rdarkmode.databinding.ActivityMainBinding
import com.latihan.rdarkmode.helper.ViewModelFactory


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getCurrentTheme()
        binding.btnTheme.setOnClickListener {
            themeDialog()
        }

        //get current theme to change the img
        when (AppCompatDelegate.getDefaultNightMode()) {
            MODE_NIGHT_YES -> {
                binding.sunIcon.visibility = View.GONE
                binding.moonIcon.visibility = View.VISIBLE
            }
            MODE_NIGHT_NO -> {
                binding.sunIcon.visibility = View.VISIBLE
                binding.moonIcon.visibility = View.GONE
            }
            else -> getCurrentThemeBySystem()
        }
    }

    private fun themeDialog() {

        val singleItems = arrayOf("Light", "Dark", "Default")
        var checkedItem = 2

        val pref = SettingPreferences.getInstance(dataStore)
        val themeViewModel = ViewModelProvider(this, ViewModelFactory(pref))[ThemeViewModel::class.java]

        themeViewModel.getThemeSettings().observe(this) { theme: Int ->
            when (theme) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    themeViewModel.saveThemeSetting(0)
                    checkedItem = 0
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    themeViewModel.saveThemeSetting(1)
                    checkedItem = 1
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    themeViewModel.saveThemeSetting(2)
                    checkedItem = 2
                }
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.theme))
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                when(which) {
                    0 ->{
                        checkedItem = 0
                    }
                    1 ->{
                        checkedItem = 1
                    }
                    2 ->{
                        checkedItem = 2
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog,_ ->
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                when(checkedItem) {
                    0 ->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                       themeViewModel.saveThemeSetting(0)
                    }
                    1 ->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        themeViewModel.saveThemeSetting(1)

                    }
                    2 ->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        themeViewModel.saveThemeSetting(2)
                    }
                }

            }
            .show()
    }

    private fun getCurrentThemeBySystem() {
        val nightModeFlags: Int = applicationContext.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.sunIcon.visibility = View.GONE
                binding.moonIcon.visibility = View.VISIBLE
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.sunIcon.visibility = View.VISIBLE
                binding.moonIcon.visibility = View.GONE
            }
        }
    }

}