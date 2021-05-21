package com.example.eco.ActivityOrFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eco.R
import kotlinx.android.synthetic.main.fragment_login_select.view.*

class LoginSelectFragment : Fragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 배너화면에서 로그인 버튼을 눌렀을때의 역할을 만듭니다.
        view?.goLoginPageButton?.setOnClickListener {
            (activity as BannerActivity).goLoginPager()
        }

        //배너 화면에서 회원가입 버튼을 눌렀을때 화면전환을 합니다.
        view?.signInButton?.setOnClickListener {
            (activity as BannerActivity).goSignPage()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_select, container, false)
        return view
    }
}