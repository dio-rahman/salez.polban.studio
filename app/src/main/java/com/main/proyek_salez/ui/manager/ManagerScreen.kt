package com.main.proyek_salez.ui.manager

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.UserRole
import com.main.proyek_salez.data.viewmodel.AuthViewModel
import com.main.proyek_salez.data.viewmodel.ManagerViewModel
import com.main.proyek_salez.ui.sidebar.SidebarManager
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerScreen(
    navController: NavController,
    viewModel: ManagerViewModel = hiltViewModel(),
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var categoryName by rememberSaveable { mutableStateOf("") }
    var foodId by rememberSaveable { mutableStateOf("") }
    var foodName by rememberSaveable { mutableStateOf("") }
    var foodDesc by rememberSaveable { mutableStateOf("") }
    var foodPrice by rememberSaveable { mutableStateOf("") }
    var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var editingFoodItem by rememberSaveable { mutableStateOf<FoodItemEntity?>(null) }
    var showDeleteCategoryDialog by remember { mutableStateOf<String?>(null) }
    var showDeleteFoodItemDialog by remember { mutableStateOf<Long?>(null) }

    val categories by viewModel.categories.collectAsState()
    val foodItems by viewModel.foodItems.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Putih, Jingga, UnguTua)
    )

    showDeleteCategoryDialog?.let { categoryId ->
        AlertDialog(
            onDismissRequest = { showDeleteCategoryDialog = null },
            title = {
                Text(
                    "Konfirmasi Hapus",
                    style = MaterialTheme.typography.headlineLarge.copy(color = UnguTua)
                )
            },
            text = {
                Text(
                    "Apakah anda yakin untuk menghapus kategori ini?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(categoryId)
                        showDeleteCategoryDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = UnguTua)
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteCategoryDialog = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = UnguTua)
                ) {
                    Text("Batal")
                }
            }
        )
    }

    showDeleteFoodItemDialog?.let { foodItemId ->
        AlertDialog(
            onDismissRequest = { showDeleteFoodItemDialog = null },
            title = {
                Text(
                    "Konfirmasi Hapus",
                    style = MaterialTheme.typography.headlineLarge.copy(color = UnguTua)
                )
            },
            text = {
                Text(
                    "Apakah anda yakin untuk menghapus menu ini?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFoodItem(foodItemId)
                        showDeleteFoodItemDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = UnguTua)
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteFoodItemDialog = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = UnguTua)
                ) {
                    Text("Batal")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarManager(
                navController = navController,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = UnguTua
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HALO,",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Oranye,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DIO!",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = UnguTua,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Putih),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Tambah Kategori",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = UnguTua,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            OutlinedTextField(
                                value = categoryName,
                                onValueChange = { categoryName = it },
                                label = { Text("Nama Kategori") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Putih,
                                    focusedContainerColor = Putih,
                                    focusedBorderColor = UnguTua,
                                    unfocusedBorderColor = AbuAbu
                                ),
                                shape = RoundedCornerShape(50)
                            )
                            Button(
                                onClick = {
                                    viewModel.clearErrorMessage()
                                    if (categoryName.isBlank()) {
                                        viewModel.setErrorMessage("Nama kategori tidak boleh kosong")
                                    } else {
                                        viewModel.addCategory(categoryName)
                                        if (viewModel.errorMessage.value == null) {
                                            categoryName = ""
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    "Tambah Kategori",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        color = UnguTua,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Putih),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                if (editingFoodItem == null) "Tambah Menu" else "Edit Menu",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = UnguTua,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            OutlinedTextField(
                                value = foodId,
                                onValueChange = { foodId = it.filter { it.isDigit() } },
                                label = { Text("ID Menu") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = editingFoodItem == null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Putih,
                                    focusedContainerColor = Putih,
                                    focusedBorderColor = UnguTua,
                                    unfocusedBorderColor = AbuAbu
                                ),
                                shape = RoundedCornerShape(50)
                            )
                            OutlinedTextField(
                                value = foodName,
                                onValueChange = { foodName = it },
                                label = { Text("Nama Menu") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Putih,
                                    focusedContainerColor = Putih,
                                    focusedBorderColor = UnguTua,
                                    unfocusedBorderColor = AbuAbu
                                ),
                                shape = RoundedCornerShape(50)
                            )
                            OutlinedTextField(
                                value = foodDesc,
                                onValueChange = { foodDesc = it },
                                label = { Text("Deskripsi Menu") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Putih,
                                    focusedContainerColor = Putih,
                                    focusedBorderColor = UnguTua,
                                    unfocusedBorderColor = AbuAbu
                                ),
                                shape = RoundedCornerShape(50)
                            )
                            OutlinedTextField(
                                value = foodPrice,
                                onValueChange = {
                                    foodPrice = it.filter { it.isDigit() || it == '.' }
                                },
                                label = { Text("Harga Menu") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Putih,
                                    focusedContainerColor = Putih,
                                    focusedBorderColor = UnguTua,
                                    unfocusedBorderColor = AbuAbu
                                ),
                                shape = RoundedCornerShape(50)
                            )
                            Button(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    if (selectedImageUri == null) {
                                        if (editingFoodItem?.imagePath != null) "Gambar Ada (Pilih untuk Ganti)" else "Pilih Gambar (Opsional)"
                                    } else "Gambar Dipilih",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        color = UnguTua,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Box {
                                OutlinedTextField(
                                    value = categories.find { it.id == selectedCategoryId }?.name
                                        ?: "Pilih Kategori",
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isCategoryDropdownExpanded = true },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Putih,
                                        focusedContainerColor = Putih,
                                        focusedBorderColor = UnguTua,
                                        unfocusedBorderColor = AbuAbu
                                    ),
                                    shape = RoundedCornerShape(50),
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            isCategoryDropdownExpanded = true
                                        }) {
                                            Icon(
                                                imageVector = if (isCategoryDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = UnguTua
                                            )
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = isCategoryDropdownExpanded,
                                    onDismissRequest = { isCategoryDropdownExpanded = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    categories.forEach { category ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    category.name,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        color = UnguTua
                                                    )
                                                )
                                            },
                                            onClick = {
                                                selectedCategoryId = category.id
                                                isCategoryDropdownExpanded = false
                                            },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (editingFoodItem != null) {
                                    TextButton(
                                        onClick = {
                                            foodId = ""
                                            foodName = ""
                                            foodDesc = ""
                                            foodPrice = ""
                                            selectedImageUri = null
                                            selectedCategoryId = null
                                            editingFoodItem = null
                                            viewModel.clearErrorMessage()
                                        },
                                        colors = ButtonDefaults.textButtonColors(contentColor = UnguTua)
                                    ) {
                                        Text(
                                            "Batal",
                                            style = MaterialTheme.typography.bodyLarge.copy(color = UnguTua)
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        viewModel.clearErrorMessage()
                                        when {
                                            foodId.isBlank() -> viewModel.setErrorMessage(
                                                "ID menu tidak boleh kosong"
                                            )

                                            foodName.isBlank() -> viewModel.setErrorMessage(
                                                "Nama menu tidak boleh kosong"
                                            )

                                            foodPrice.isBlank() -> viewModel.setErrorMessage(
                                                "Harga menu tidak boleh kosong"
                                            )

                                            selectedCategoryId == null -> viewModel.setErrorMessage(
                                                "Pilih kategori terlebih dahulu"
                                            )

                                            else -> {
                                                val imagePath: String? =
                                                    selectedImageUri?.let { uri ->
                                                        saveImageToInternalStorage(context, uri)
                                                    } ?: editingFoodItem?.imagePath
                                                if (editingFoodItem == null) {
                                                    viewModel.addFoodItem(
                                                        id = foodId.toLongOrNull() ?: 0,
                                                        name = foodName,
                                                        description = foodDesc,
                                                        price = foodPrice.toDoubleOrNull() ?: 0.0,
                                                        imagePath = imagePath,
                                                        categoryId = selectedCategoryId!!
                                                    )
                                                } else {
                                                    viewModel.updateFoodItem(
                                                        id = editingFoodItem!!.id,
                                                        name = foodName,
                                                        description = foodDesc,
                                                        price = foodPrice.toDoubleOrNull() ?: 0.0,
                                                        imagePath = imagePath,
                                                        categoryId = selectedCategoryId!!
                                                    )
                                                }
                                                if (viewModel.errorMessage.value == null) {
                                                    foodId = ""
                                                    foodName = ""
                                                    foodDesc = ""
                                                    foodPrice = ""
                                                    selectedImageUri = null
                                                    selectedCategoryId = null
                                                    editingFoodItem = null
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        if (editingFoodItem == null) "Tambah Menu" else "Simpan Perubahan",
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            color = UnguTua,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    errorMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Merah,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Putih),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Daftar Kategori",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = UnguTua,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 200.dp)
                            ) {
                                items(categories) { category ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            category.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(color = UnguTua),
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = {
                                            showDeleteCategoryDialog = category.id
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Hapus Kategori",
                                                tint = Merah
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Putih),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Daftar Menu",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = UnguTua,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 200.dp)
                            ) {
                                items(foodItems) { foodItem ->
                                    FoodItemCard(
                                        foodItem = foodItem,
                                        categoryName = categories.find { it.id == foodItem.categoryId }?.name
                                            ?: "Unknown",
                                        onEdit = {
                                            editingFoodItem = foodItem
                                            foodId = foodItem.id.toString()
                                            foodName = foodItem.name
                                            foodDesc = foodItem.description
                                            foodPrice = foodItem.price.toString()
                                            selectedImageUri = null
                                            selectedCategoryId = foodItem.categoryId
                                            viewModel.clearErrorMessage()
                                        },
                                        onDelete = { showDeleteFoodItemDialog = foodItem.id }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItemEntity,
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Putih),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    foodItem.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = UnguTua
                    )
                )
                Text(
                    "Kategori: $categoryName",
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua)
                )
                Text(
                    "Harga: Rp ${foodItem.price}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua)
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Menu",
                        tint = UnguTua
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Menu",
                        tint = Merah
                    )
                }
            }
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "menu_${System.currentTimeMillis()}.jpg")
        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}