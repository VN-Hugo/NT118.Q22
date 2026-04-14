package com.example.travelapp.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState = authViewModel.authState

    // Google Sign-In configuration
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                authViewModel.signInWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onLoginSuccess()
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 500f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Chào mừng bạn \ntới Booking Lite",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 48.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Button(
                onClick = { onNavigateToRegister() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Tạo tài khoản mới", color = Color.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bạn đã có tài khoản? Đăng nhập",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.5f))
                Text(" Đăng nhập bằng ", color = Color.White, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SocialButton(
                    iconRes = R.drawable.ic_apple, // Giữ icon apple nhưng đổi text thành Facebook như yêu cầu
                    text = "Facebook",
                    modifier = Modifier.weight(1f),
                    onClick = { /* Xử lý Facebook ở đây nếu cần */ }
                )
                SocialButton(
                    iconRes = R.drawable.ic_google,
                    text = "Google",
                    modifier = Modifier.weight(1f),
                    onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (authState is AuthState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun SocialButton(
    iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}