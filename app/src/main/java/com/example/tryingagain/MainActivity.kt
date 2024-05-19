package com.example.tryingagain

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val API_KEY = "AIzaSyA3riTR3XxRQYQwKUj07QqHLl85s8PkhN4"  // Replace with your actual API key
    }

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var proceedButton: Button
    private var byteArray: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val captureImageBtn = findViewById<Button>(R.id.capture_image)
        imageView = findViewById(R.id.image_view)
        textView = findViewById(R.id.text_display)
        proceedButton = findViewById(R.id.proceed_button)

        captureImageBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                dispatchTakePictureIntent()
            }
        }

        proceedButton.setOnClickListener {
            val intent = Intent(this, PantryActivity::class.java)
            intent.putStringArrayListExtra("detectedObjects", ArrayList(textView.text.toString().split("\n")))
            startActivity(intent)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            processImage(imageBitmap)
        }
    }

    private fun processImage(bitmap: Bitmap) {
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            byteArray = stream.toByteArray()
            sendImageToServer(byteArray!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendImageToServer(imageBitmap: ByteArray) {
        val base64EncodedString = Base64.encodeToString(imageBitmap, Base64.NO_WRAP)
        val jsonRequestBody = """
        {
            "requests": [
                {
                    "image": {
                        "content": "$base64EncodedString"
                    },
                    "features": [
                        {
                            "type": "OBJECT_LOCALIZATION"
                        }
                    ]
                }
            ]
        }
    """.trimIndent()

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonRequestBody)
        val request = Request.Builder()
            .url("https://vision.googleapis.com/v1/images:annotate?key=$API_KEY")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Failed to send image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    if (responseBody != null) {
                        try {
                            // Log the full response for debugging
                            Log.d("API_RESPONSE", "Response from API: $responseBody")

                            val jsonObject = JSONObject(responseBody)
                            if (jsonObject.has("responses")) {
                                val responsesArray = jsonObject.getJSONArray("responses")
                                if (responsesArray.length() > 0) {
                                    val firstResponse = responsesArray.getJSONObject(0)
                                    if (firstResponse.has("localizedObjectAnnotations")) {
                                        val localizedObjects = firstResponse.getJSONArray("localizedObjectAnnotations")

                                        val objectList = StringBuilder()
                                        for (i in 0 until localizedObjects.length()) {
                                            val objectInfo = localizedObjects.getJSONObject(i)
                                            val name = objectInfo.getString("name")
                                            objectList.append("$name\n")
                                        }

                                        textView.text = objectList.toString()
                                    } else {
                                        textView.text = "No localized objects found"
                                    }
                                } else {
                                    textView.text = "No responses found"
                                }
                            } else {
                                textView.text = "No 'responses' key found in JSON"
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@MainActivity, "Failed to parse response: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to receive response from server", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
