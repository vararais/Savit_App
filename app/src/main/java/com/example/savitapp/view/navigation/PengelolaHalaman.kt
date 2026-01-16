package com.example.savitapp.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.savitapp.view.auth.LoginScreen
import com.example.savitapp.view.auth.RegisterScreen
import com.example.savitapp.view.home.* // Mengimpor HomeScreen, EntryStuffScreen, Detail, Edit, Profile, History

@Composable
fun SavitAppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        // 1. Halaman Login
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { userId ->
                    // Masuk ke Home dengan membawa User ID
                    navController.navigate("home/$userId") {
                        popUpTo("login") { inclusive = true } // Hapus login dari history back
                    }
                }
            )
        }

        // 2. Halaman Register
        composable("register") {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 3. Halaman Dashboard (Home)
        composable(
            route = "home/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            HomeScreen(
                userId = userId,
                onNavigateToAdd = { navController.navigate("entry/$userId") },
                onDetailClick = { stuffId ->
                    navController.navigate("detail/$userId/$stuffId")
                },
                // --- TAMBAHKAN BARIS INI (JANGAN LUPA KOMA DI ATASNYA) ---
                onNavigateToProfile = {
                    navController.navigate("profile/$userId")
                }
            )
        }

        // 4. Halaman Input Barang (Entry)
        composable(
            route = "entry/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            EntryStuffScreen(
                navigateBack = { navController.popBackStack() },
                userId = userId
            )
        }

        // 5. Halaman Detail Barang
        composable(
            route = "detail/{userId}/{stuffId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("stuffId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val stuffId = backStackEntry.arguments?.getInt("stuffId") ?: 0
            DetailScreen(
                userId = userId,
                stuffId = stuffId,
                navigateBack = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate("edit/$userId/$stuffId")
                }
            )
        }

        // 6. Halaman Edit Barang
        composable(
            route = "edit/{userId}/{stuffId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("stuffId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val stuffId = backStackEntry.arguments?.getInt("stuffId") ?: 0
            EditStuffScreen(
                userId = userId,
                stuffId = stuffId,
                navigateBack = { navController.popBackStack() }
            )
        }

        // 7. Halaman Profil (INI PERBAIKANNYA BIAR GA CRASH)
        composable(
            route = "profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            ProfileScreen(
                onNavigateToHistory = { navController.navigate("history/$userId") },
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    // Logout kembali ke Login & Hapus semua history
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 8. Halaman Riwayat Transaksi (History)
        composable(
            route = "history/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            HistoryScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}