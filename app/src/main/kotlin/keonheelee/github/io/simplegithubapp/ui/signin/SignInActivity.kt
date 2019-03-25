package keonheelee.github.io.simplegithubapp.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast

import keonheelee.github.io.simplegithubapp.BuildConfig
import keonheelee.github.io.simplegithubapp.R
import keonheelee.github.io.simplegithubapp.ui.api.AuthApi
import keonheelee.github.io.simplegithubapp.ui.api.Model.GithubAccessToken
import keonheelee.github.io.simplegithubapp.ui.api.Model.GithubApiProvider
import keonheelee.github.io.simplegithubapp.ui.data.AuthTokenProvider
import keonheelee.github.io.simplegithubapp.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    lateinit internal var api: AuthApi
    lateinit internal var authTokenProvider: AuthTokenProvider
    lateinit internal var accessTokenCall: Call<GithubAccessToken>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnActivitySignInStart.setOnClickListener {
            // 사용자 인증을 처리하는 URL을 구성
            // 형식: https://github.com/login/oauth/
            //       authorize?client_id={어플리케이션의 Client ID}
            val authUri = Uri.Builder().scheme("https")
                    .authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id",
                            BuildConfig.GITHUB_CLIENT_ID)
                    .build()

            // 크롬 커스텀 탭으로 웹 페이지 표시
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@SignInActivity, authUri)
        }

        api = GithubApiProvider.provideAuthApi()
        authTokenProvider = AuthTokenProvider(this)

        // 저장된 액세스 토큰이 있다면 메인 액티비티로 이동
        if (authTokenProvider.token != null)
            launchMainActivity()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        showProgress()

        // 사용자 인증 완료 후 리다이렉션된 주소 가져옴
        val uri = intent.data ?: throw IllegalArgumentException("No data exists")
        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")

        getAccessToken(code)
    }

    private fun getAccessToken(code: String) {
        showProgress()

        // 액세스 토큰을 요청하는 REST API
        accessTokenCall = api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)

        // 비동기 방식으로 액세스 토큰을 요청
        accessTokenCall.enqueue(object : Callback<GithubAccessToken> {
            override fun onResponse(call: Call<GithubAccessToken>,
                                    response: Response<GithubAccessToken>) {
                hideProgress()

                val token = response.body()
                if (response.isSuccessful && token != null) {
                    // 발급받은 액세스 토큰을 저장
                    authTokenProvider.updateToken(token.accessToken)

                    // 메인 액티비티로 이동
                    launchMainActivity()
                } else {
                    showError(IllegalStateException(
                            "Not successful:" + response.message()))
                }
            }

            override fun onFailure(call: Call<GithubAccessToken>, t: Throwable) {
                hideProgress()
                showError(t)
            }
        })
    }


    private fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnActivitySignInStart.visibility = View.VISIBLE
        pbActivitySignIn.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
    }

    private fun launchMainActivity() {
        startActivity(Intent(
                this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
