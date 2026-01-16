package com.example.savitapp.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savitapp.model.InsertUiEvent
import com.example.savitapp.viewmodel.EntryViewModel
import com.example.savitapp.viewmodel.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryStuffScreen(
    userId: Int,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // 1. Color Palette
    val HijauMuda = Color(0xFFA2B29F)
    val HijauTua = Color(0xFF798777)
    val Cream = Color(0xFFF8EDE3)
    val Hitam = Color(0xFF000000)
    val Putih = Color(0xFFFFFFFF)
    val Merah = Color(0xFFFF0000)

    Scaffold(
        topBar = {
            // 2. TopAppBar Hijau Muda, Tulisan Tengah Bold Hitam
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Tambah Barang",
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
        containerColor = Cream // Background Cream
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Panggil Form Input
            FormInput(
                insertUiEvent = viewModel.uiState.insertUiEvent,
                onValueChange = viewModel::updateUiState,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = HijauTua,
                    unfocusedIndicatorColor = HijauMuda,
                    focusedLabelColor = HijauTua,
                    unfocusedLabelColor = HijauTua,
                    cursorColor = Hitam
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Button "Simpan" (Hijau Tua, Teks Putih)
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveStuff(userId)
                        navigateBack()
                    }
                },
                enabled = viewModel.uiState.isEntryValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50), // Rounded Edge
                colors = ButtonDefaults.buttonColors(
                    containerColor = HijauTua,
                    contentColor = Putih
                )
            ) {
                Text(
                    text = "Simpan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 4. Button "Kembali" (Hijau Muda, Teks Merah Outline Putih)
            Button(
                onClick = navigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50), // Rounded Edge
                colors = ButtonDefaults.buttonColors(containerColor = HijauMuda)
            ) {
                // Trik Outline Text
                Box(contentAlignment = Alignment.Center) {
                    // Layer 1: Stroke Putih
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
                    // Layer 2: Fill Merah
                    Text(
                        text = "Kembali",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Merah
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(
    insertUiEvent: InsertUiEvent,
    onValueChange: (InsertUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    colors: TextFieldColors
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Input Nama Barang
        OutlinedTextField(
            value = insertUiEvent.namaBarang,
            onValueChange = { onValueChange(insertUiEvent.copy(namaBarang = it)) },
            label = { Text("Nama Barang") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = colors,
            singleLine = true
        )

        // Input Rencana Hari
        OutlinedTextField(
            value = insertUiEvent.rencanaHari,
            onValueChange = { onValueChange(insertUiEvent.copy(rencanaHari = it)) },
            label = { Text("Rencana Hari (cth: 30)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = colors,
            singleLine = true
        )

        // DROPDOWN Skala Prioritas
        // Kita butuh state lokal untuk mengatur dropdown terbuka/tutup
        var expanded by remember { mutableStateOf(false) }
        val options = listOf("Penting", "Sedang", "Rendah")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = insertUiEvent.prioritas,
                onValueChange = {},
                readOnly = true, // Supaya tidak bisa diketik manual
                label = { Text("Skala Prioritas") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(), // Penting agar menu muncul di bawah field
                shape = RoundedCornerShape(12.dp),
                colors = colors
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(insertUiEvent.copy(prioritas = option))
                            expanded = false
                        }
                    )
                }
            }
        }

        // Input Harga
        OutlinedTextField(
            value = insertUiEvent.hargaBarang,
            onValueChange = { onValueChange(insertUiEvent.copy(hargaBarang = it)) },
            label = { Text("Harga Barang (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = colors,
            singleLine = true
        )
    }
}