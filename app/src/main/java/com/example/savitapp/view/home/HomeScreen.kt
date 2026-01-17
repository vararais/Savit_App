package com.example.savitapp.view.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savitapp.model.Stuff
import com.example.savitapp.viewmodel.HomeUiState
import com.example.savitapp.viewmodel.HomeViewModel
import com.example.savitapp.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: Int,
    onNavigateToAdd: () -> Unit,
    onDetailClick: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // 1. Color Palette
    val HijauMuda = Color(0xFFA2B29F)
    val HijauTua = Color(0xFF798777)
    val Cream = Color(0xFFF8EDE3)
    val Putih = Color(0xFFFFFFFF)
    val Hitam = Color(0xFF000000)

    // State untuk Quick Add Dialog
    var selectedStuffForQuickAdd by remember { mutableStateOf<Stuff?>(null) }

    val context = LocalContext.current

    // 1. Gunakan 'mutableStateOf' biar UI bisa berubah
    var namaUser by remember { mutableStateOf("User") }

    // 2. Gunakan 'LaunchedEffect' untuk MEMBACA ULANG setiap kali masuk Home
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        // Baca data terbaru dari memori HP
        namaUser = sharedPreferences.getString("NAMA_USER", "User") ?: "User"
    }

    LaunchedEffect(userId) {
        viewModel.getStuffList(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // --- FIX POSISI KANAN ---
                    // Gunakan Box fillMaxWidth agar kita bisa atur alignment ke End (Kanan)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.CenterEnd) // <--- INI KUNCINYA (Rata Kanan)
                                .clickable { onNavigateToProfile() }
                                .padding(end = 8.dp) // Kasih jarak dikit dari pinggir
                        ) {
                            // Teks dulu baru Icon, biar rapi di kanan (Halo, User [Icon])
                            Text(
                                text = "Halo, $namaUser",
                                color = Hitam,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Hitam,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HijauMuda
                )
            )
        },
        floatingActionButton = {
            // 3. FAB UPDATE: Lingkaran Hijau Muda, Outline Hitam
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = HijauMuda,
                contentColor = Putih,
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    .border(width = 1.5.dp, color = Hitam, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Barang",
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        containerColor = Cream
    ) { innerPadding ->

        // Handling State
        when (val state = viewModel.homeUiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HijauTua)
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            }
            is HomeUiState.Success -> {
                HomeBody(
                    stuffList = state.stuffList,
                    onItemClick = onDetailClick,
                    onQuickAddClick = { stuff -> selectedStuffForQuickAdd = stuff },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (selectedStuffForQuickAdd != null) {
            QuickAddDialog(
                stuff = selectedStuffForQuickAdd!!,
                onDismiss = { selectedStuffForQuickAdd = null },
                onSuccess = {
                    selectedStuffForQuickAdd = null
                    viewModel.getStuffList(userId)
                }
            )
        }
    }
}

@Composable
fun HomeBody(
    stuffList: List<Stuff>,
    onItemClick: (Int) -> Unit,
    onQuickAddClick: (Stuff) -> Unit,
    modifier: Modifier = Modifier
) {
    if (stuffList.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada target. Yuk tambah!", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stuffList) { stuff ->
                StuffCard(
                    stuff = stuff,
                    onClick = { onItemClick(stuff.stuffId) },
                    onQuickAddClick = { onQuickAddClick(stuff) }
                )
            }
        }
    }
}

@Composable
fun StuffCard(
    stuff: Stuff,
    onClick: () -> Unit,
    onQuickAddClick: () -> Unit
) {
    val HijauMuda = Color(0xFFA2B29F)
    val KuningSedang = Color(0xFFE8D595)
    val OrangeRendah = Color(0xFFED985F)
    val Cream = Color(0xFFF8EDE3)

    val cardColor = when (stuff.prioritas) {
        "Penting" -> HijauMuda
        "Sedang" -> KuningSedang
        "Rendah" -> OrangeRendah
        "rendah" -> OrangeRendah
        else -> Cream
    }

    val Hitam = Color(0xFF000000)
    val Putih = Color(0xFFFFFFFF)

    val progress = if (stuff.hargaBarang > 0) {
        stuff.uangTerkumpul.toFloat() / stuff.hargaBarang.toFloat()
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stuff.namaBarang,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Hitam
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hari tersisa: ${stuff.rencanaHari} hari",
                    fontSize = 14.sp,
                    color = Hitam,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF798777),
                    trackColor = Color.White.copy(alpha = 0.5f)
                )

                Text(
                    text = "Rp ${stuff.uangTerkumpul} / Rp ${stuff.hargaBarang}",
                    fontSize = 12.sp,
                    color = Hitam,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { onQuickAddClick() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF798777)),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(45.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Add",
                    tint = Putih
                )
            }
        }
    }
}