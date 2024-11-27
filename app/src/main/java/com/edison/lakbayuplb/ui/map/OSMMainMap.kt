package com.edison.lakbayuplb.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColor
import com.edison.lakbayuplb.R
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderArray
import org.osmdroid.tileprovider.modules.MBTilesFileArchive
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File

@Composable
fun OSMMapView(
    modifier: Modifier,
    title: String,
    snippet: String,
    initialLocation: GeoPoint?,
    routeType: String,
    lineString: MutableList<Pair<String, MutableList<MutableList<Pair<Double, Double>>>>>,
    destinationLocation: GeoPoint?
) {
    val context = LocalContext.current
    val osmMainMap = remember { OSMainMap(context, title, snippet) }

    // Use mutable state to track the initial and destination locations
    var currentInitialLocation by remember { mutableStateOf(initialLocation) }
    var currentDestinationLocation by remember { mutableStateOf(destinationLocation) }

    // Initialize the map view
    val mapView = remember { osmMainMap.initializeMap(currentInitialLocation, currentDestinationLocation) }

    // Embed the MapView in AndroidView
    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )

    // Add route overlays with the user's location as the starting point
    LaunchedEffect(lineString, routeType, initialLocation) {
        if(lineString.isNotEmpty()) {


            osmMainMap.clearOverlays()
            currentInitialLocation = initialLocation
            currentDestinationLocation = destinationLocation
            osmMainMap.updateMarkers(currentInitialLocation, currentDestinationLocation)

            for (i in 0 until lineString.size) {

                val profile = lineString[i].first
                val coordinates = lineString[i].second[0].toMutableList()
                // Add the user's location at the start for the 0th profile
                if (i == 0 && initialLocation != null) {
                    val userLocation = Pair(initialLocation.latitude, initialLocation.longitude)
                    coordinates.add(0, userLocation) // Add user's location at the start
                }

                // Convert coordinates to GeoJSON LineString format
                val path = createGeoJsonLineString(coordinates)

                // Add the route overlay to the map
                osmMainMap.addRouteOverlay(path, profile)

            }
            delay(5000L)

        }
    }
}

class OSMainMap(
    private val context: Context,
    private val title: String,
    private val snippet: String
) {

    private lateinit var mapView: MapView

    fun initializeMap(
        initialLocation: GeoPoint?,
        destinationLocation: GeoPoint?
    ): MapView {
        // Initialize OSMDroid configuration
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid_preferences", Context.MODE_PRIVATE))

        // Create a MapView instance
        mapView = MapView(context)

        // Path to the MBTiles file
        val mbTilesFile = File(context.getExternalFilesDir(null), "assets/map/UPLB.mbtiles")

        if (mbTilesFile.exists()) {
            // Create a tile source based on the MBTiles
            val tileSource = XYTileSource(
                "MBTiles",
                17, 21, 256, ".png",
                arrayOf()
            )

            // Set up the tile provider for MBTiles
            val archiveFile = MBTilesFileArchive.getDatabaseFileArchive(mbTilesFile)
            val tileProvider = MapTileProviderArray(
                tileSource,
                null,
                arrayOf(MapTileFileArchiveProvider(SimpleRegisterReceiver(context), tileSource, arrayOf(archiveFile)))
            )

            // Set the tile provider for the MapView
            mapView.setTileProvider(tileProvider)
        }

        // Enable multi-touch controls (for zoom and panning)
        mapView.setMultiTouchControls(true)

        // Set initial zoom and center on UPLB
        mapView.controller.setZoom(20.0)
        mapView.controller.setCenter(GeoPoint(14.165080369830758, 121.24152668957413)) // UPLB center

        mapView.minZoomLevel = 17.0
        mapView.maxZoomLevel = 21.0

        // Set bounds (optional, if you want to limit the view to a specific area)
        val boundingBox = BoundingBox(
            14.199483653098456, 121.25590394224918,
            14.147189880033771, 121.22749500166606
        )

        //14.199483653098456,
        //14.16818796226134,

        //
        mapView.setScrollableAreaLimitDouble(boundingBox)

        // Add a rotation gesture overlay for user interaction
        val rotationGestureOverlay = RotationGestureOverlay(mapView)
        rotationGestureOverlay.isEnabled = true
        mapView.overlays.add(rotationGestureOverlay)

        // Add markers if locations are not null
        updateMarkers(initialLocation, destinationLocation)

        // Force the map to refresh and apply changes
        mapView.invalidate()

        return mapView
    }

    fun clearOverlays() {
        mapView.overlays.clear()
        mapView.invalidate() // Refresh the map view to reflect changes
    }

    fun updateMarkers(initialLocation: GeoPoint?, destinationLocation: GeoPoint?) {
        // Clear existing markers
        mapView.overlays.clear()

        if (initialLocation != null) {
            addTextMarkerOverlay(
                initialLocation,
                title = "",
                snippet = "",
                iconResId = R.drawable.icons8_circle_24___, // Pass the Drawable object
                backgroundColor = Color.TRANSPARENT.toColor(),
                foreGroundColor = Color.TRANSPARENT.toColor(),
                anchorX = 0.5f, // Center horizontally
                anchorY = 0.5f  // Center vertically
            )
            mapView.controller.setCenter(initialLocation)
        }
        if (destinationLocation != null) {
            addTextMarkerOverlay(
                destinationLocation,
                title = title,
                snippet = snippet,
                iconResId = R.drawable.marker_48,
                backgroundColor = Color.WHITE.toColor(),
                foreGroundColor = Color.BLACK.toColor(),
                anchorX = 0.5f, // Center horizontally
                anchorY = 1.0f  // Bottom-center vertically
            )
        }

        // Set center and refresh the map

        mapView.invalidate()  // Ensure the map is refreshed

        mapView.setMultiTouchControls(true)
        val rotationGestureOverlay = RotationGestureOverlay(mapView)
        rotationGestureOverlay.isEnabled = true
        mapView.overlays.add(rotationGestureOverlay)
    }

    fun addRouteOverlay(lineString: String, profile: String) {
        mapView.invalidate()
        // Parse the coordinates from the LineString
        val jsonObject = JSONObject(lineString)
        val coordinatesArray = jsonObject.getJSONArray("coordinates")

        val geoPoints = mutableListOf<GeoPoint>()
        for (i in 0 until coordinatesArray.length()) {
            val coordinate = coordinatesArray.getJSONArray(i)
            val lon = coordinate.getDouble(0)
            val lat = coordinate.getDouble(1)
            geoPoints.add(GeoPoint(lat, lon))
        }

        // Determine the color based on the profile
        val colorCode = when (profile) {
            "foot" -> "#00FF00" // Green
            "bicycle" -> "#0000FF" // Blue
            "driving" -> "#FF0000" // Red
            "transit" -> "#00FFFF" // Cyan
            else -> "#000000" // Default black
        }

        // Create a polyline for the route
        val polyline = Polyline().apply {
            setPoints(geoPoints)
            outlinePaint.apply {
                color = Color.parseColor(colorCode)  // Set the color
                strokeWidth = 15f  // Set the stroke width
            }
        }

        // Add the polyline to the map
        mapView.overlays.add(polyline)

        // Force the map to refresh and apply changes
        mapView.invalidate()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addTextMarkerOverlay(
        position: GeoPoint?,
        title: String,
        snippet: String,
        iconResId: Int,
        backgroundColor: Color,
        foreGroundColor: Color,
        anchorX: Float = 0.5f,
        anchorY: Float = 1.0f
    ) {
        if (position == null) {
            return
        }

        val iconDrawable = context.getDrawable(iconResId) ?: return

        val textMarkerOverlay = TextMarkerOverlay(
            position,
            title,
            snippet,
            iconDrawable,
            backgroundColor = backgroundColor,
            foreGroundColor = foreGroundColor,
            anchorX = anchorX,
            anchorY = anchorY
        )

        mapView.overlays.add(textMarkerOverlay)
    }
}

class TextMarkerOverlay(
    private val geoPoint: GeoPoint?,
    private val title: String,
    private val snippet: String,
    private val icon: Drawable,
    private val textSize: Float = 40f,
    private val backgroundColor: Color,
    private val foreGroundColor: Color,
    private val anchorX: Float = 0.5f, // Default: Center horizontally
    private val anchorY: Float = 1.0f  // Default: Bottom-center vertically
) : Overlay() {

    override fun draw(canvas: Canvas?, mapView: MapView?, shadow: Boolean) {
        val projection = mapView?.projection ?: return
        val mapOrientation = mapView.mapOrientation

        // Get the screen coordinates from the geoPoint
        val screenPoint = Point()
        if(geoPoint!=null)
            projection.toPixels(geoPoint, screenPoint)

        // Save the current canvas state
        canvas?.save()

        // Apply map rotation to the canvas
        canvas?.rotate(-mapOrientation, screenPoint.x.toFloat(), screenPoint.y.toFloat())

        // Prepare Paint for drawing text
        val textPaint = Paint().apply {
            color = foreGroundColor.toArgb()
            textSize = this@TextMarkerOverlay.textSize
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        // Prepare Paint for drawing the background rectangle
        val backgroundPaint = Paint().apply {
            color = backgroundColor.toArgb()
            style = Paint.Style.FILL
        }

        // Calculate text metrics for the title and snippet
        val titleTextWidth = textPaint.measureText(title)
        val snippetTextWidth = textPaint.measureText(snippet)
        val textHeight = textPaint.descent() - textPaint.ascent()

        // Determine max width of the text
        val maxTextWidth = maxOf(titleTextWidth, snippetTextWidth)

        // Calculate the vertical position of the title and snippet
        val titleY = screenPoint.y - icon.intrinsicHeight - textHeight * 2 - 20 // Slightly above the icon
        val snippetY = screenPoint.y - icon.intrinsicHeight - textHeight - 10

        // Draw the white background rectangle behind the text
        canvas?.drawRect(
            screenPoint.x - maxTextWidth / 2 - 10,  // Left
            titleY + textPaint.ascent() - 20,       // Top
            screenPoint.x + maxTextWidth / 2 + 10,  // Right
            snippetY + textPaint.descent() + 10,    // Bottom
            backgroundPaint
        )

        // Draw the title and snippet
        canvas?.drawText(title, screenPoint.x.toFloat(), titleY, textPaint)
        canvas?.drawText(snippet, screenPoint.x.toFloat(), snippetY, textPaint)

        // Draw the marker icon below the text
        val iconWidth = icon.intrinsicWidth
        val iconHeight = icon.intrinsicHeight
        val adjustedX = (screenPoint.x - (iconWidth * anchorX)).toInt()
        val adjustedY = (screenPoint.y - (iconHeight * anchorY)).toInt()

        icon.setBounds(
            adjustedX,
            adjustedY,
            adjustedX + iconWidth,
            adjustedY + iconHeight
        )
        icon.draw(canvas!!)

        // Restore the canvas state (important to undo the rotation)
        canvas.restore()
    }
}
