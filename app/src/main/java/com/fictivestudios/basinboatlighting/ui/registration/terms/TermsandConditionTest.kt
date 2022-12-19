package com.fictivestudios.basinboatlighting.ui.registration.terms

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.TermsconditionBinding
import com.fictivestudios.basinboatlighting.models.content.Content
import com.fictivestudios.basinboatlighting.ui.registration.termscondition.TermsConditionViewModel
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class TermsandConditionTest : BaseFragment() {

    private lateinit var termsconditionBinding: TermsconditionBinding


    override fun setTitlebar(titlebar: Titlebar) {




    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        termsconditionBinding = DataBindingUtil.inflate(inflater, R.layout.termscondition,container,false)
        termsconditionBinding.lifecycleOwner = this




        termsconditionBinding.ivback.setOnClickListener {
            getActivitContextRegistration!!.onBackPressed()

        }

        termsandCondition("tc")

        return termsconditionBinding.root

    }

    private fun termsandCondition(tc:String){

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,false,
                        "",
                        object : CallHandler<Response<Content>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<Content> {
                                return apiInterFace.content(tc)
                            }

                            override fun success(response: Response<Content>) {
                                if (response.body()!!.status == 1) {

                                  //  termsconditionBinding.termscondtion.setText(response.body()?.data?.content)
                                    termsconditionBinding.termscondtion.setText(Html.fromHtml(response.body()?.data?.content))

                                } else {
                                    showToast(response.body()?.message.toString(),getActivityContext)
                                }
                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                            }

                        }
                    )


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

}