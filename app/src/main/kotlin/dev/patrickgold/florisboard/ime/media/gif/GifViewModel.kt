package dev.patrickgold.florisboard.ime.media.gif

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL

class GifViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val API_KEY = "AIzaSyADkrSWg6N-KcN-SzHxw8Eiw6uV2vhJFIA"
        private const val CLIENT_KEY = "Auteur_Keyboard"
        private const val TAG = "GIFViewModel"

        /**
         * Get featured GIFs
         */
        private fun getFeaturedGifs(limit: Int): JSONObject? {
            // get the Featured GIFS - using the default locale of en_US
            val url = "https://tenor.googleapis.com/v2/featured?key=$API_KEY&client_key=$CLIENT_KEY&limit=$limit"
            return try {
                get(url)
            } catch (ignored: IOException) {
                null
            } catch (ignored: JSONException) {
                null
            }
        }

        /**
         * Get categories
         */
        private fun getCategories(): JSONObject? {
            // get the categories - using the default locale of en_US
            val url = "https://tenor.googleapis.com/v2/categories?key=$API_KEY&client_key=$CLIENT_KEY"
            return try {
                get(url)
            } catch (ignored: IOException) {
                null
            } catch (ignored: JSONException) {
                null
            }
        }

        /**
         * Construct and run a GET request
         */
        @Throws(IOException::class, JSONException::class)
        private fun get(url: String): JSONObject {
            var connection: HttpURLConnection? = null
            return try {
                // Get request
                connection = URL(url).openConnection() as HttpURLConnection
                connection.apply {
                    doInput = true
                    doOutput = true
                    requestMethod = "GET"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                }

                // Handle failure
                val statusCode = connection.responseCode
                if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
                    val error = "HTTP Code: '$statusCode' from '$url'"
                    throw ConnectException(error)
                }

                // Parse response
                parser(connection)
            } catch (ignored: Exception) {
                JSONObject("")
            } finally {
                connection?.disconnect()
            }
        }

        /**
         * Parse the response into JSONObject
         */
        @Throws(JSONException::class)
        private fun parser(connection: HttpURLConnection): JSONObject {
            val buffer = CharArray(1024 * 4)
            var n: Int
            var stream: InputStream? = null
            return try {
                stream = BufferedInputStream(connection.inputStream)
                val reader = InputStreamReader(stream, "UTF-8")
                val writer = StringWriter()
                while (reader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }
                JSONObject(writer.toString())
            } catch (ignored: IOException) {
                JSONObject("")
            } finally {
                stream?.close()
            }
        }
    }

    private val _gifs = MutableStateFlow<List<String>>(emptyList())
    val gifs: StateFlow<List<String>> = _gifs.asStateFlow()

    init {
        viewModelScope.launch {
            loadGifs()
        }
    }

    suspend fun loadGifs() {
        withContext(Dispatchers.IO) {
            // get the top 10 featured GIFs
            val featuredGifs = getFeaturedGifs(10)
            val gifs = mutableListOf<String>()
            featuredGifs?.getJSONArray("results")?.let { results ->
                for (i in 0 until results.length()) {
                    val gif = results.getJSONObject(i)
                    val media = gif.getJSONArray("media_formats")
                    val gifUrl = media.getJSONObject(0).getString("gif_url")
                    gifs.add(gifUrl)
                }
            }

            // get the current list of categories
            val categories = getCategories()

            // load the results for the user
            Log.d(TAG, "Featured GIFS: ${featuredGifs.toString()}")
            Log.d(TAG, "GIF Categories: ${categories.toString()}")

            _gifs.value = gifs
        }
    }

}
