package com.example.savitapp.view.home

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savitapp.model.History
import com.example.savitapp.viewmodel.HistoryUiState
import com.example.savitapp.viewmodel.HistoryViewModel
import com.example.savitapp.viewmodel.PenyediaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // 1. Color Palette
    val HijauMuda = Color(0xFFA2B29F)
    val HijauTua = Color(0xFF798777)
    val Cream = Color(0xFFF8EDE3)
    val Hitam = Color(0xFF000000)
    val Putih = Color(0xFFFFFFFF)
    val Merah = Color(0xFFFF0000)

    // Load data history saat halaman dibuka
    LaunchedEffect(userId) {
        viewModel.getHistoryList(userId)
    }

    Scaffold(
        topBar = {
            // 2. TopAppBar Hijau Muda, Tulisan Hitam Bold
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Riwayat Transaksi",
                        fontWeight = FontWeight.Bold,
                        color = Hitam,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = HijauMuda
                )
            )
        },
        containerColor = Cream, // Background Cream
        bottomBar = {
            // Button Kembali di bagian bawah layar (Fixed)
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = HijauMuda)
            ) {
                // Trik Outline Text
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Kembali",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle.Default.copy(
                            drawStyle = Stroke(
                                miter = 10f,
                                width = 5f,
                                join = StrokeJoin.Round
                            )
                        ),
                        color = Putih
                    )
                    Text(
                        text = "Kembali",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Merah
                    )
                }
            }
        }
    ) { innerPadding ->

        // Handling UI State
        when (val state = viewModel.historyUiState) {
            is HistoryUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HijauTua)
                }
            }
            is HistoryUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Merah)
                }
            }
            is HistoryUiState.Success -> {
                val historyList = state.historyList

                if (historyList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada riwayat transaksi", color = Color.Gray)
                    }
                } else {
                    // 3. LazyColumn untuk Scroll
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(historyList) { history ->
                            HistoryCard(history = history)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(history: History) {
    val HijauTua = Color(0xFF798777)
    val Hitam = Color(0xFF000000)
    val Putih = Color(0xFFFFFFFF)

    // 4. Custom Card Shape
    // Kiri: Rounded Penuh (50%) agar mengikuti lingkaran icon
    // Kanan: Rounded Sedikit (16.dp)
    val CustomShape = RoundedCornerShape(
        topStart = CornerSize(50),
        bottomStart = CornerSize(50),
        topEnd = CornerSize(16.dp),
        bottomEnd = CornerSize(16.dp)
    )

    Card(
        shape = CustomShape,
        colors = CardDefaults.cardColors(containerColor = HijauTua), // Warna Hijau Tua
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp) // Padding dalam card
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Centang (Success)
            // Menggunakan Surface bulat putih di belakang icon agar kontras
            Surface(
                shape = CircleShape,
                color = Putih,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = HijauTua,
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Informasi Transaksi
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nama Barang
                Text(
                    text = history.namaBarang ?: "Barang", // Handle null safety
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Hitam
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Nominal
                Text(
                    text = "Rp ${history.nominal}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Hitam
                )

                // Tanggal
                Text(
                    text = history.tanggal,
                    fontSize = 14.sp,
                    color = Hitam.copy(alpha = 0.7f), // Hitam agak transparan
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}