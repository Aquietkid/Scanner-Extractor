package com.example.cnicscanner

import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toFile
import coil.compose.rememberAsyncImagePainter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : ComponentActivity() {

    private var onScanComplete: ((GmsDocumentScanningResult) -> Unit)? = null

    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannerResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scannerResult?.let { onScanComplete?.invoke(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            var detectedText by remember { mutableStateOf("") }
            var showImageDialog by remember { mutableStateOf(false) }

            fun startScan() {
                val options = GmsDocumentScannerOptions.Builder()
                    .setGalleryImportAllowed(true)
                    .setPageLimit(1)
                    .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
                    .build()

                val scanner = GmsDocumentScanning.getClient(options)
                onScanComplete = { result ->
                    val savedUri = saveScanResult(result)
                    imageUri = savedUri
                    if (savedUri != null) {
                        runTextRecognition(savedUri) { text ->
                            detectedText = text
                        }
                    } else {
                        detectedText = "Failed to save image"
                    }
                }

                scanner.getStartScanIntent(this)
                    .addOnSuccessListener { intentSender ->
                        scannerLauncher.launch(
                            IntentSenderRequest.Builder(intentSender).build()
                        )
                    }
                    .addOnFailureListener { e -> e.printStackTrace() }
            }

            if (imageUri == null && detectedText.isEmpty()) {
                LaunchedEffect(Unit) { startScan() }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clickable { showImageDialog = true },
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(detectedText.ifEmpty { "Recognizing text..." })

                        Spacer(Modifier.height(24.dp))

                        Button(onClick = {
                            imageUri = null
                            detectedText = ""
                            startScan()
                        }) {
                            Text("Scan Again")
                        }
                    }
                }
            }

            if (showImageDialog && imageUri != null) {
                Dialog(onDismissRequest = { showImageDialog = false }) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    private fun saveScanResult(result: GmsDocumentScanningResult): Uri? {
        val page = result.pages?.firstOrNull() ?: return null
        val imageUri = page.imageUri
        val inputStream = contentResolver.openInputStream(imageUri) ?: return null

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "scan_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Scans")
        }

        val galleryUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return null

        contentResolver.openOutputStream(galleryUri)?.use { output ->
            inputStream.copyTo(output)
        }
        return galleryUri
    }

    private fun runTextRecognition(uri: Uri, onResult: (String) -> Unit) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())
        val image = InputImage.fromFilePath(this, uri)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                onResult(visionText.text)
            }
            .addOnFailureListener {
                onResult("Failed to recognize text")
            }
    }

    data class CNICFields(
        val name: String,
        val fatherName: String,
        val gender: String,
        val idNo: String,
        val doi: String,
        val doe: String,
        val dob: String
    )

    fun extractCNICFields(ocrText: String): CNICFields {
        val text = ocrText.lowercase()

        val nameRegex = Regex("""name[:\-]?\s*([a-z\s]+)""", RegexOption.IGNORE_CASE)
        val fatherRegex = Regex("""father\s*name[:\-]?\s*([a-z\s]+)""", RegexOption.IGNORE_CASE)
        val genderRegex = Regex("""\b(male|female|other)\b""", RegexOption.IGNORE_CASE)
        val idRegex = Regex("""\b\d{5}-\d{7}-\d\b""")
        val doiRegex = Regex("""date\s*of\s*issue[:\-]?\s*(\d{2}[/-]\d{2}[/-]\d{4})""", RegexOption.IGNORE_CASE)
        val doeRegex = Regex("""date\s*of\s*expiry[:\-]?\s*(\d{2}[/-]\d{2}[/-]\d{4})""", RegexOption.IGNORE_CASE)
        val dobRegex = Regex("""date\s*of\s*birth[:\-]?\s*(\d{2}[/-]\d{2}[/-]\d{4})""", RegexOption.IGNORE_CASE)

        fun matchOrUnknown(regex: Regex) = regex.find(text)?.groupValues?.getOrNull(1)?.trim()?.replaceFirstChar { it.uppercase() } ?: "unknown"

        return CNICFields(
            name = matchOrUnknown(nameRegex),
            fatherName = matchOrUnknown(fatherRegex),
            gender = genderRegex.find(text)?.value?.replaceFirstChar { it.uppercase() } ?: "unknown",
            idNo = idRegex.find(text)?.value ?: "unknown",
            doi = matchOrUnknown(doiRegex),
            doe = matchOrUnknown(doeRegex),
            dob = matchOrUnknown(dobRegex)
        )
    }


}
