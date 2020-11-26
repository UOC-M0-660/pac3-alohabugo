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
    private lateinit var sessionManag: SessionManager
    // paginacion
    private var cursor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)
        // Init RecyclerView
        initRecyclerView()
        // Inicializamos variable twitchService
        twitchService = TwitchApiService(Network.createHttpClient(this))
        // Inicializamos variable sessionManag
        sessionManag = SessionManager(this@StreamsActivity)

        // TODO: Get Streams
        lifecycleScope.launch {
            try {
                getStreams()
            } catch (e: UnauthorizedException) {
                try {
                    // excepction "No autorizado"
                    Log.d(TAG, "UnauthorizedException: refreshAccessToken")
                    // refrescamos token
                    refreshAccessToken()
                    // volvemos a realizar la petición
                    getStreams()
                } catch (e: UnauthorizedException) {
                    Log.d(TAG, "UnauthorizedException: clear tokens")

                }

            }
        }

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
        // Paginacion - solicitar los siguientes 20 streams
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // Si se llega al final de la lista y no se puede realizar scroll vertical
                // y ademas el nuevo estado es de no desplazamiento
                if (!mRecyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // solicitamos streams
                    getStreams()
                }
            }
        })
    }

    private fun getStreams() {
        lifecycleScope.launch {
            // obtener el listado de streams y mostrarlos en recyclerView
            val streams = twitchService.getStreams(cursor)
            streams?.data?.let {
                adapter.addStreams(it)
            }
            // paginacion
            streams?.pagination?.cursor?.let {
                cursor = it
            }
        }
    }

    private suspend fun refreshAccessToken() {
        // refresh token using the SessionManager class
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