package group.intelliboys.smms.components.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

public class CustomMapView extends MapView {
    public CustomMapView(Context context) {
        super(context);
    }

    public CustomMapView(Context context, MapTileProviderBase aTileProvider) {
        super(context, aTileProvider);
    }

    public CustomMapView(Context context, MapTileProviderBase aTileProvider, Handler tileRequestCompleteHandler) {
        super(context, aTileProvider, tileRequestCompleteHandler);
    }

    public CustomMapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs);
    }

    public CustomMapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs, boolean hardwareAccelerated) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs, hardwareAccelerated);
    }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addMarker(Marker marker) {
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        getOverlays().add(marker);
        invalidate();
    }

    public void addMarker(Marker marker, Drawable icon) {
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(icon);
        getOverlays().add(marker);
        invalidate();
    }

    public void removeMarker(Marker marker) {
        getOverlays().remove(marker);
        invalidate();
    }

    public void drawRoute(Polyline route) {
        getOverlays().add(route);
        invalidate();
    }

    public void removeRoute(Polyline route) {
        getOverlays().remove(route);
        invalidate();
    }

    public void centerRouteOnMap(List<GeoPoint> routePoints) {
        if (routePoints == null || routePoints.isEmpty()) {
            return;
        }

        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        for (GeoPoint point : routePoints) {
            double lat = point.getLatitude();
            double lon = point.getLongitude();
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;
        }

        BoundingBox boundingBox = new BoundingBox(maxLat, maxLon, minLat, minLon);

        double paddingPercentage = 0.3;
        double latSpan = boundingBox.getLatitudeSpan();
        double lonSpan = boundingBox.getLongitudeSpan();

        boundingBox = new BoundingBox(
                boundingBox.getLatNorth() + latSpan * paddingPercentage,
                boundingBox.getLonEast() + lonSpan * paddingPercentage,
                boundingBox.getLatSouth() - latSpan * paddingPercentage,
                boundingBox.getLonWest() - lonSpan * paddingPercentage
        );

        zoomToBoundingBox(boundingBox, true);
    }
}
