package dev.timatifey.posanie.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import dev.timatifey.posanie.api.FacultyAPI
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.ui.theme.PoSanieTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var facultyAPI: FacultyAPI

    private val scope = CoroutineScope(Dispatchers.Main)
    private var faculties = mutableStateListOf<Faculty>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope.launch {
            Toast.makeText(baseContext, "Start load", Toast.LENGTH_LONG).show()
            faculties.addAll(facultyAPI.getFacultiesList())
            Log.d("HELLO", faculties.toString())
        }

        setContent {
            PoSanieTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val values = remember { faculties }
                    FacultyList(faculties = values)
                }
            }
        }
    }
}

@Composable
fun FacultyList(faculties: List<Faculty>) {
    LazyColumn {
        items(faculties) { faculty: Faculty ->
            Item(faculty)
        }
    }
}

@Composable
fun Item(faculty: Faculty) {
    Text(text = faculty.title)
}
