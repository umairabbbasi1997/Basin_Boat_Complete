package com.fictivestudios.basinboatlighting.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.databinding.HomeactivityBinbing
import com.fictivestudios.basinboatlighting.utils.Titlebar
import java.util.*


class HomeActivity : BaseActivity(), View.OnClickListener{

 var homeBinding : HomeactivityBinbing? = null
    var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_home_fragment) as NavHostFragment
        navController = navHostFragment!!.findNavController()

        hideBttomBar()
        hideButtonHome()

        setListner()


    }


    override fun setMainFrameLayoutID() {


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setListner(){

        homeBinding?.profleid?.setOnClickListener(this)
        homeBinding?.settingsid?.setOnClickListener(this)
        homeBinding?.homeid?.setOnClickListener(this)

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavController

    }


    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.homeFragment,
            R.id.profile, R.id.settings -> {
                navController.navigate(R.id.exitDialog)
            }
            else -> navController.popBackStack()
        }
    }

    fun mainHideTitle() {
        homeBinding?.titlebar?.visibility = View.GONE
    }

    fun mainShowTitle() {
        homeBinding?.titlebar?.visibility = View.VISIBLE
    }

    fun getTitlebar(): Titlebar {
        return homeBinding!!.titlebar
    }

    fun hideBttomBar() {
        homeBinding?.rlbottombar?.visibility = View.GONE
    }

    fun hideButtonHome() {
        homeBinding?.homeid?.visibility = View.GONE
    }

    fun showButtonHome() {
        homeBinding?.homeid?.visibility = View.VISIBLE
    }

    fun showBttomBar() {
        homeBinding!!.rlbottombar.visibility = View.VISIBLE
    }

    override fun onClick(pos: View?) {

        when(pos?.id){

            R.id.homeid->{
                navController.navigate(R.id.homeFragment)

            }

         
           R.id.profleid -> {
               navController.navigate(R.id.profile)

           }

            R.id.settingsid -> {
                navController.navigate(R.id.settings)
            }



        }

    }



}