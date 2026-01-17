package com.example.savitapp.view.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savitapp.model.Stuff
import com.example.savitapp.viewmodel.PenyediaViewModel
import com.example.savitapp.viewmodel.QuickAddViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddDialog(
    stuff: Stuff,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: QuickAddViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // 1. Color Palette
    val HijauMuda = Color(0xFFA2B29F)
    val HijauTua = Color(0xFF798777)
    val Cream = Color(0xFFF8EDE3)
    val Hitam = Color(0xFF000000)
    val Putih = Color(0xFFFFFFFF)
    val Merah = Color(0xFFA00000)

    val context = LocalContext.current

    // State untuk Pop-up
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    // Rumus Target Harian
    val kekurangan = if (stuff.hargaBarang > stuff.uangTerkumpul) stuff.hargaBarang - stuff.uangTerkumpul else 0
    val sisaHari = if (stuff.rencanaHari > 0) stuff.rencanaHari else 1
    val targetHarian = kekurangan / sisaHari

    // --- DIALOG KONFIRMASI (Yakin Tambah Saldo?) ---
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            containerColor = Cream,
            title = { Text("Konfirmasi", fontWeight = FontWeight.Bold, color = Hitam) },
            text = { Text("Yakin tambahkan saldo sebesar Rp ${viewModel.nominalInput}?", color = Hitam) },
            confirmButton = {
                Button(
                    onClick = {
                        // 1. KUNCI UTAMA: SIMPAN DULU NILAINYA KE VARIABEL LOKAL
                        // Sebelum viewModel membersihkannya jadi kosong
                        val nominalYangMauDisimpan = viewModel.nominalInput.toLongOrNull() ?: 0
                        val uangSaatIni = stuff.uangTerkumpul
                        val hargaBarang = stuff.hargaBarang

                        showConfirmationDialog = false

                        // Eksekusi Simpan ke Database
                        viewModel.saveTransaction(stuff.stuffId) {

                            // 2. GUNAKAN VARIABEL YANG TADI KITA SIMPAN
                            val totalBaru = uangSaatIni + nominalYangMauDisimpan

                            if (totalBaru >= hargaBarang) {
                                // Jika Target Tercapai -> Munculkan Dialog Finish
                                showFinishDialog = true
                            } else {
                                // Jika Belum -> Toast biasa & Tutup
                                Toast.makeText(context, "Saldo berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                                onSuccess()
                                onDismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HijauTua)
                ) {
                    Text("Ya", color = Putih)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Batal", color = Merah)
                }
            }
        )
    }

    // --- DIALOG TARGET TERCAPAI (FINISH) ---
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = {
                onSuccess()
                onDismiss()
            },
            containerColor = Cream,
            title = { Text("Selamat! 🎉", fontWeight = FontWeight.Bold, color = HijauTua, fontSize = 24.sp) },
            text = {
                Column {
                    Text("Barang ini sudah selesai ditabung!", color = Hitam, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hebat! Satu target berhasil dicapai.", color = Hitam)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSuccess() // Refresh Dashboard
                        onDismiss() // Tutup Dialog Quick Add
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HijauTua)
                ) {
                    Text("Mantap!", color = Putih)
                }
            }
        )
    }

    // --- DIALOG ERROR (Input Tidak Valid) ---
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            containerColor = Cream,
            title = { Text("Input Invalid!", fontWeight = FontWeight.Bold, color = Merah) },
            text = { Text("Harap cek input (harus angka dan tidak boleh kosong).", color = Hitam) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HijauTua)
                ) {
                    Text("OK", color = Putih)
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Cream), // Background Cream
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Hijau Muda
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HijauMuda)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Quick Add",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Hitam
                    )
                }

                // Konten Dialog
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Teks Informasi
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Masukkan saldo",
                            fontWeight = FontWeight.Bold,
                            color = Hitam,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Target harian: Rp $targetHarian",
                            fontWeight = FontWeight.Bold,
                            color = Hitam,
                            fontSize = 16.sp
                        )
                    }

                    // Input Nominal
                    OutlinedTextField(
                        value = viewModel.nominalInput,
                        onValueChange = { viewModel.updateNominal(it) },
                        label = { Text("Nominal (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Putih,
                            unfocusedContainerColor = Putih,
                            focusedBorderColor = HijauTua,
                            unfocusedBorderColor = HijauTua,
                            focusedLabelColor = Hitam,
                            cursorColor = Hitam
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button Selesai
                    Button(
                        onClick = {
                            val input = viewModel.nominalInput
                            if (input.isNotEmpty() && input.all { it.isDigit() } && input.toLongOrNull() != null && input.toLong() > 0) {
                                showConfirmationDialog = true
                            } else {
                                showErrorDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(containerColor = HijauTua)
                    ) {
                        Text(
                            text = "Selesai",
                            color = Putih,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // Button Kembali
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(containerColor = HijauMuda)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Kembali",
                                fontSize = 16.sp,
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
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Merah
                            )
                        }
                    }
                }
            }
        }
    }
}