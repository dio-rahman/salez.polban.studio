package com.main.proyek_salez.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.viewmodel.CartViewModel
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.menu.MenuItemCard
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.main.proyek_salez.data.model.CartItemWithFood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    cartViewModel: CartViewModel = hiltViewModel<CartViewModel>(),
    cashierViewModel: CashierViewModel = hiltViewModel<CashierViewModel>()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var menuInput by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<List<FoodItemEntity>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>("Rekomendasi") }
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Putih, Jingga, UnguTua)
    )
    val categories = listOf("Rekomendasi", "Makanan", "Minuman", "Lainnya")

    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarMenu(
                navController = navController,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBackground)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = UnguTua
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.salez_logo),
                    contentDescription = "Salez Logo",
                    modifier = Modifier
                        .size(180.dp)
                        .offset(x = (-35).dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(140.dp))
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

                Spacer(modifier = Modifier.height(35.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = menuInput,
                        onValueChange = { menuInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = {
                            Text(
                                text = "Cari nama menu terkait pesanan",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Putih,
                            focusedContainerColor = Putih,
                            focusedBorderColor = UnguTua,
                            unfocusedBorderColor = AbuAbu
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    IconButton(
                        onClick = { navController.navigate("cart_screen") },
                        modifier = Modifier
                            .size(40.dp)
                            .background(UnguTua, shape = RoundedCornerShape(50))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Putih
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch {
                            errorMessage = ""
                            cartViewModel.searchFoodItems(menuInput).collectLatest { items ->
                                if (items.isNotEmpty()) {
                                    searchResult = items
                                } else {
                                    searchResult = emptyList()
                                    errorMessage = "Menu '${menuInput}' tidak tersedia."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Cari Menu",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (searchResult.isNotEmpty() || errorMessage.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        if (searchResult.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier.height(120.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(searchResult) { foodItem ->
                                    Box(modifier = Modifier.width(160.dp)) {
                                        MenuItemCard(
                                            modifier = Modifier.width(160.dp),
                                            foodItem = foodItem,
                                            quantity = cartItems.find { it.foodItem.id == foodItem.id }?.cartItem?.quantity ?: 0,
                                            onAddToCart = { cartViewModel.addToCart(it) },
                                            onRemoveFromCart = { cartViewModel.decrementItem(it) },
                                            onDeleteFromCart = { cartViewModel.decrementItem(it) }
                                        )
                                        IconButton(
                                            onClick = { searchResult = searchResult.filter { it.id != foodItem.id } },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Close",
                                                tint = UnguTua,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = UnguTua,
                                    fontWeight = FontWeight.Medium
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryButton(
                            text = category,
                            onClick = { selectedCategory = category },
                            isSelected = selectedCategory == category
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                selectedCategory?.let { category ->
                    MenuItemsDisplay(
                        category = category,
                        cashierViewModel = cashierViewModel,
                        cartItems = cartItems,
                        onAddToCart = { cartViewModel.addToCart(it) },
                        onRemoveFromCart = { cartViewModel.decrementItem(it) },
                        onDeleteFromCart = { cartViewModel.decrementItem(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryButton(
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = UnguTua,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
) {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000
                20f at 0
                20f at 1000
                -20f at 4000
                -20f at 5000
            },
            repeatMode = RepeatMode.Restart
        )
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(48.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Jingga else Oranye
        ),
        shape = RoundedCornerShape(50)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = style,
                maxLines = 1,
                modifier = Modifier.offset(x = offsetX.dp)
            )
        }
    }
}

@Composable
fun MenuItemsDisplay(
    category: String,
    cashierViewModel: CashierViewModel,
    cartItems: List<CartItemWithFood>,
    onAddToCart: (FoodItemEntity) -> Unit,
    onRemoveFromCart: (FoodItemEntity) -> Unit,
    onDeleteFromCart: (FoodItemEntity) -> Unit
) {
    val foodItems = when (category) {
        "Rekomendasi" -> cashierViewModel.getRecommendedItems().collectAsState(initial = emptyList())
        else -> cashierViewModel.getFoodItemsByCategory(category).collectAsState(initial = emptyList())
    }

    if (foodItems.value.isEmpty()) {
        Text(
            text = if (category == "Rekomendasi") "Belum ada rekomendasi." else "Belum ada menu $category.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = UnguTua,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(foodItems.value.chunked(2)) { rowItems ->
                MenuRow(
                    rowItems = rowItems,
                    cartItems = cartItems,
                    onAddToCart = onAddToCart,
                    onRemoveFromCart = onRemoveFromCart,
                    onDeleteFromCart = onDeleteFromCart
                )
            }
        }
    }
}

@Composable
fun MenuRow(
    rowItems: List<FoodItemEntity>,
    cartItems: List<CartItemWithFood>,
    onAddToCart: (FoodItemEntity) -> Unit,
    onRemoveFromCart: (FoodItemEntity) -> Unit,
    onDeleteFromCart: (FoodItemEntity) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        rowItems.forEach { foodItem ->
            val quantity = cartItems.find { it.foodItem.id == foodItem.id }?.cartItem?.quantity ?: 0
            MenuItemCard(
                modifier = Modifier.weight(1f).padding(4.dp),
                foodItem = foodItem,
                quantity = quantity,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onDeleteFromCart = onDeleteFromCart
            )
        }
        if (rowItems.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}