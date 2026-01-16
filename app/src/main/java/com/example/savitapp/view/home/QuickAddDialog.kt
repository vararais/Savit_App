package com.example.savitapp.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val Merah = Color(0xFFFF0000)

    // Rumus Target Harian
    val kekurangan = if (stuff.hargaBarang > stuff.uangTerkumpul) stuff.hargaBarang - stuff.uangTerkumpul else 0
    val sisaHari = if (stuff.rencanaHari > 0) stuff.rencanaHari else 1
    val targetHarian = kekurangan / sisaHari

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
                // 2. "TopAppBar" Buatan (Header Hijau Muda)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HijauMuda)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Quick Add", // Judul Bold Besar Hitam
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

                    // 3. Button "Selesai" (Hijau Tua, Teks Putih, Rounded)
                    Button(
                        onClick = {
                            viewModel.saveTransaction(stuff.stuffId) {
                                onSuccess()
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50), // Rounded Edge
                        colors = ButtonDefaults.buttonColors(containerColor = HijauTua)
                    ) {
                        Text(
                            text = "Selesai",
                            color = Putih,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // 4. Button "Kembali" (Hijau Muda, Teks Merah Outline Putih, Rounded)
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50), // Rounded Edge
                        colors = ButtonDefaults.buttonColors(containerColor = HijauMuda)
                    ) {
                        // Trik membuat Outline Text di Compose: Tumpuk 2 Text
                        Box(contentAlignment = Alignment.Center) {
                            // Layer 1: Stroke Putih (Outline)
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
                            // Layer 2: Fill Merah (Teks Utama)
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