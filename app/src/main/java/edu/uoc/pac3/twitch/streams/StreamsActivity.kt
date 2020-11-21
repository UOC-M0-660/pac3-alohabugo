package edu.uoc.pac3.twitch.streams

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.oauth.UnauthorizedException
import io.ktor.client.features.*
import kotlinx.coroutines.launch

class StreamsActivity : AppCompatActivity() {

    private val TAG = "StreamsActivity"
    private lateinit var twitchService: TwitchApiService
    private lateinit var adapter: StreamsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)
        // Init RecyclerView
        initRecyclerView()
        // TODO: Get Streams
        getStreams()

    }

    private fun initRecyclerView() {
        // TODO: Implement
        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set Layout Manager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        // Init Adapter
        adapter = StreamsListAdapter(ArrayList())
        mRecyclerView.adapter = adapter
    }

    private fun getStreams() {
        // inicializamos variable twitchService
        twitchService = TwitchApiService(Network.createHttpClient(this))
        lifecycleScope.launch {
            try {
                // obtener el listado de streams y mostrarlos en recyclerView
                val streams = twitchService.getStreams()
                streams?.data?.let {
                    adapter.addStreams(it)
                }
            } catch (e: ClientRequestException) {
                Log.d(TAG, getString(R.string.error_streams))

                // refrescamos token
                refreshAccessToken()

                // volvemos a realizar la petición
                val streams = twitchService.getStreams()
                streams?.data?.let {
                    adapter.addStreams(it)
                }
            }
        }
    }

    private suspend fun refreshAccessToken() {
        // refresh token using the SessionManager class
        val sessionManag = SessionManager(this@StreamsActivity)
        try {
            val refreshToken = sessionManag.getRefreshToken()
            val response = refreshToken?.let { twitchService.getRefreshToken(it) }
            response?.accessToken?.let { sessionManag.saveAccessToken(it) }
            response?.refreshToken?.let { sessionManag.saveRefreshToken(it) }
        } catch (e: ClientRequestException) {
            Log.d(TAG, "Error refreshing token")
        }
    }

}