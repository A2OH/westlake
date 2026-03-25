package com.westlake.host

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Demo Compose app using Navigation + Retrofit + Coil + ViewModel.
 * Proves the full production stack works on the Westlake engine.
 */

// === Data model ===
data class DogImage(val message: String, val status: String)
data class JokeResponse(val setup: String, val punchline: String, val type: String, val id: Int)

// === Retrofit API ===
interface DogApi {
    @GET("breeds/image/random")
    suspend fun getRandomDog(): DogImage
}

interface JokeApi {
    @GET("random_joke")
    suspend fun getRandomJoke(): JokeResponse
}

// === ViewModel ===
class DemoViewModel : ViewModel() {
    var dogImageUrl by mutableStateOf<String?>(null)
    var joke by mutableStateOf<JokeResponse?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var counter by mutableIntStateOf(0)

    private val dogRetrofit = Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val jokeRetrofit = Retrofit.Builder()
        .baseUrl("https://official-joke-api.appspot.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val dogApi = dogRetrofit.create(DogApi::class.java)
    private val jokeApi = jokeRetrofit.create(JokeApi::class.java)

    suspend fun fetchDog() {
        isLoading = true
        error = null
        try {
            val result = dogApi.getRandomDog()
            dogImageUrl = result.message
            Log.i("DemoApp", "Dog image: ${result.message}")
        } catch (e: Exception) {
            error = "Failed: ${e.message}"
            Log.e("DemoApp", "Dog fetch failed", e)
        }
        isLoading = false
    }

    suspend fun fetchJoke() {
        isLoading = true
        error = null
        try {
            joke = jokeApi.getRandomJoke()
        } catch (e: Exception) {
            error = "Failed: ${e.message}"
        }
        isLoading = false
    }
}

// === Navigation ===
@Composable
fun DemoComposeApp() {
    val navController = rememberNavController()

    MaterialTheme(colorScheme = darkColorScheme()) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { DemoHomeScreen(navController) }
            composable("dogs") { DogScreen(navController) }
            composable("jokes") { JokeScreen(navController) }
            composable("counter") { CounterScreen(navController) }
        }
    }
}

// === Home Screen ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoHomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Westlake Demo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { WestlakeActivity.instance?.showHome() }) {
                        Icon(Icons.Default.ArrowBack, "Back to gallery")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Production Stack Demo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Navigation + Retrofit + Coil + ViewModel",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item { DemoCard("Random Dogs", "Fetch dog images via Retrofit + display with Coil",
                Icons.Default.Pets, Color(0xFF4CAF50)) { navController.navigate("dogs") } }

            item { DemoCard("Random Jokes", "Fetch jokes from REST API via Retrofit",
                Icons.Default.EmojiEmotions, Color(0xFFFF9800)) { navController.navigate("jokes") } }

            item { DemoCard("Counter", "ViewModel state management + Compose recomposition",
                Icons.Default.Add, Color(0xFF2196F3)) { navController.navigate("counter") } }

            item {
                Spacer(Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Stack verified:", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("✓ Jetpack Compose (Material 3)", color = Color.White.copy(0.8f), fontSize = 13.sp)
                        Text("✓ Navigation Compose", color = Color.White.copy(0.8f), fontSize = 13.sp)
                        Text("✓ Retrofit 2 (REST API)", color = Color.White.copy(0.8f), fontSize = 13.sp)
                        Text("✓ Coil (async image loading)", color = Color.White.copy(0.8f), fontSize = 13.sp)
                        Text("✓ ViewModel (state management)", color = Color.White.copy(0.8f), fontSize = 13.sp)
                        Text("✓ Coroutines (async)", color = Color.White.copy(0.8f), fontSize = 13.sp)
                        Text("✓ Gson (JSON parsing)", color = Color.White.copy(0.8f), fontSize = 13.sp)
                        Text("✓ OkHttp (HTTP client)", color = Color.White.copy(0.8f), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DemoCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(color),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp)) }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
            }
            Icon(Icons.Default.ChevronRight, null, tint = color)
        }
    }
}

// === Dog Screen (Retrofit + Coil) ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogScreen(navController: NavHostController) {
    val vm: DemoViewModel = viewModel()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Random Dogs") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { scope.launch { vm.fetchDog() } },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (vm.isLoading) "Loading..." else "Fetch Random Dog")
            }

            Spacer(Modifier.height(16.dp))

            if (vm.isLoading) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
            }

            vm.error?.let {
                Text(it, color = Color.Red, fontSize = 14.sp)
            }

            vm.dogImageUrl?.let { url ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
                        contentDescription = "Dog",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(url.substringAfterLast("/").substringBeforeLast("."),
                    color = Color.White.copy(0.5f), fontSize = 12.sp, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// === Joke Screen (Retrofit) ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokeScreen(navController: NavHostController) {
    val vm: DemoViewModel = viewModel()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Random Jokes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { scope.launch { vm.fetchJoke() } },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.EmojiEmotions, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (vm.isLoading) "Loading..." else "Get Random Joke")
            }

            Spacer(Modifier.height(24.dp))

            vm.joke?.let { j ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(j.setup, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(16.dp))
                        Text(j.punchline, fontSize = 16.sp, color = Color(0xFFFF9800))
                    }
                }
            }
        }
    }
}

// === Counter Screen (ViewModel) ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(navController: NavHostController) {
    val vm: DemoViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counter") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("${vm.counter}", fontSize = 72.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilledTonalButton(onClick = { vm.counter-- }) {
                    Icon(Icons.Default.Remove, null)
                    Text(" Minus")
                }
                Button(onClick = { vm.counter++ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))) {
                    Icon(Icons.Default.Add, null)
                    Text(" Plus")
                }
            }
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { vm.counter = 0 }) {
                Text("Reset", color = Color.White.copy(0.5f))
            }
        }
    }
}
