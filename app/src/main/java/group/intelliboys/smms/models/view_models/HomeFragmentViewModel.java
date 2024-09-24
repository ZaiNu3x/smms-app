package group.intelliboys.smms.models.view_models;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

public class HomeFragmentViewModel {
    private static HomeFragmentViewModel instance;
    private double zoomLevel;
    private IGeoPoint mapCenter;
    private GeoPoint markerA = new GeoPoint(0d, 0d);
    private GeoPoint markerB = new GeoPoint(0d, 0d);
    private GeoPoint myLocation = new GeoPoint(0d, 0d);
    private boolean isNavigateContainerVisible;
    private String pointAValue;
    private String pointBValue;

    public static HomeFragmentViewModel getInstance() {
        if (instance == null) {
            instance = new HomeFragmentViewModel();
        }

        return instance;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public IGeoPoint getMapCenter() {
        return mapCenter;
    }

    public void setMapCenter(IGeoPoint mapCenter) {
        this.mapCenter = mapCenter;
    }

    public GeoPoint getMarkerA() {
        return markerA;
    }

    public void setMarkerA(GeoPoint markerA) {
        this.markerA = markerA;
    }

    public GeoPoint getMarkerB() {
        return markerB;
    }

    public void setMarkerB(GeoPoint markerB) {
        this.markerB = markerB;
    }

    public GeoPoint getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(GeoPoint myLocation) {
        this.myLocation = myLocation;
    }

    public boolean isNavigateContainerVisible() {
        return isNavigateContainerVisible;
    }

    public void setNavigateContainerVisible(boolean navigateContainerVisible) {
        isNavigateContainerVisible = navigateContainerVisible;
    }

    public String getPointAValue() {
        return pointAValue;
    }

    public void setPointAValue(String pointAValue) {
        this.pointAValue = pointAValue;
    }

    public String getPointBValue() {
        return pointBValue;
    }

    public void setPointBValue(String pointBValue) {
        this.pointBValue = pointBValue;
    }
}
