package com.ssafyb109.bangrang

import com.ssafyb109.bangrang.view.EventPage
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.ssafyb109.bangrang.view.BottomBar
import com.ssafyb109.bangrang.view.FavoritePage
import com.ssafyb109.bangrang.view.HomePage
import com.ssafyb109.bangrang.view.LoginPage
import com.ssafyb109.bangrang.view.MapPage
import com.ssafyb109.bangrang.view.MyPage
import com.ssafyb109.bangrang.view.RankPage
import com.ssafyb109.bangrang.view.SignupPage
import com.ssafyb109.bangrang.view.TopBar
import com.ssafyb109.bangrang.view.handleGoogleSignInResult
import com.ssafyb109.bangrang.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

val customBackgroundColor = Color(245, 245, 245)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            navController = rememberNavController()
            AppNavigation(navController)
        }
    }

    // 구글 로그인
    fun getGoogleLoginAuth(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.gcp_id))
            .requestId()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task, navController, userViewModel)
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val userViewModel: UserViewModel = hiltViewModel()

    // 시작화면
    val startDestination = "Login"

    Surface(modifier = Modifier.fillMaxSize(), color = customBackgroundColor) {
        Column {
            val currentDestination =
                navController.currentBackStackEntryAsState().value?.destination?.route

            if (currentDestination != "Login" && currentDestination != "SignUp") {
                TopBar(navController)
            }

            Box(modifier = Modifier.weight(1f)) {
                NavHost(navController, startDestination = startDestination) {
                    composable("Login") { LoginPage(navController, userViewModel) }
                    composable("Signup") { SignupPage(navController, userViewModel) }

                    composable("Home") { HomePage(navController, userViewModel) }
                    composable("MapPage") { MapPage(navController, userViewModel) }
                    composable("EventPage") { EventPage(navController, userViewModel) }
                    composable("FavoritePage") { FavoritePage(navController, userViewModel) }
                    composable("RankPage") { RankPage(navController, userViewModel) }
                    composable("MyPage") { MyPage(navController, userViewModel) }

                }
            }

            if (currentDestination != "Login" && currentDestination != "SignUp") {
                BottomBar(navController)
            }
        }
    }
}

