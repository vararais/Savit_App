package com.example.savitapp.view.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savitapp.viewmodel.DetailUiState
import com.example.savitapp.viewmodel.DetailViewModel
import com.example.savitapp.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    userId: Int,
    stuffId: Int,
    navigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // 1. Color Palette
    val HijauMuda = Color(0xFFA2B29F)
    val HijauTua = Color(0xFF798777)
    val Cream = Color(0xFFF8EDE3)
    val Hitam = Color(0xFF000000)
    val Putih = Color(0xFFFFFFFF)
    val Merah = Color(0xFFA00000)

    val context = LocalContext.current // Butuh context untuk Toast

    LaunchedEffect(Unit) {
        viewModel.getStuffDetail(userId, stuffId)
    }

    // State Dialog Hapus
    val showDeleteDialog = remember { mutableStateOf(false) }

    // --- LOGIKA POP-UP KONFIRMASI HAPUS ---
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            containerColor = Cream,
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold, color = Hitam) },
            text = { Text("Apakah anda yakin ingin menghapus barang ini secara permanen?", color = Hitam) },
            confirmButton = {
                Button(
                    onClick = {
                        // Panggil ViewModel untuk delete ke Database
                        viewModel.deleteStuff(stuffId) {
                            // --- BLOK INI JALAN KALAU SUKSES DELETE ---
                            showDeleteDialog.value = false
                            // Tampilkan Toast sesuai SRS
                            Toast.makeText(context, "Barang berhasil di delete", Toast.LENGTH_SHORT).show()
                            navigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Merah)
                ) { Text("Ya, Hapus", color = Putih, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Batal", color = Hitam)
                }
            }
        )
    }

    Scaffold(
        containerColor = Cream
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        when (val state = viewModel.detailUiState) {
            is DetailUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = HijauTua) }
            }
            is DetailUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = Merah) }
            }
            is DetailUiState.Success -> {
                val stuff = state.stuff

                val percentage = if (stuff.hargaBarang > 0) {
                    ((stuff.uangTerkumpul.toDouble() / stuff.hargaBarang.toDouble()) * 100).toInt()
                } else 0
                val progressFloat = if (stuff.hargaBarang > 0) {
                    stuff.uangTerkumpul.toFloat() / stuff.hargaBarang.toFloat()
                } else 0f

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Hijau
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                            .background(HijauMuda)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(top = 20.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
                        ) {
                            Text(
                                text = "Hore , Progress Kamu sudah $percentage%",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Hitam,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            LinearProgressIndicator(
                                progress = { progressFloat },
                                modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(10.dp)),
                                color = HijauTua,
                                trackColor = Putih
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Dana terkumpul", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Hitam)
                            Text(text = "Rp ${stuff.uangTerkumpul}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Hitam)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- BUTTON EDIT ---
                    Button(
                        onClick = { onEditClick(stuffId) },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(130.dp), // Saya kembalikan ke 60dp biar rapi
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HijauTua)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Putih)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit barang", color = Putih, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // --- BUTTON HAPUS ---
                    Button(
                        onClick = { showDeleteDialog.value = true }, // Trigger Dialog Konfirmasi
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(130.dp), // Saya kembalikan ke 60dp biar rapi
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Merah)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Putih)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hapus", color = Putih, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info Box Container
                    Column(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StaticInfoBox("Nama Barang", stuff.namaBarang, HijauMuda, Hitam)
                        StaticInfoBox("Skala Prioritas", stuff.prioritas, HijauMuda, Hitam)
                        StaticInfoBox("Harga Barang", "Rp ${stuff.hargaBarang}", HijauMuda, Hitam)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Button Kembali
                    Button(
                        onClick = navigateBack,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HijauTua)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Kembali",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                style = TextStyle.Default.copy(
                                    drawStyle = Stroke(miter = 10f, width = 5f, join = StrokeJoin.Round)
                                ),
                                color = Putih
                            )
                            Text(
                                text = "Kembali",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Merah
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun StaticInfoBox(label: String, value: String, bgColor: Color, textColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}