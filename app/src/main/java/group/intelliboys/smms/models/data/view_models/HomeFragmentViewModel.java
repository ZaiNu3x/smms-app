package group.intelliboys.smms.models.data.view_models;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HomeFragmentViewModel {
    @Getter
    @Setter
    private float zoomLevel;
    @Getter
    @Setter
    private String pointAValue;
    @Getter
    @Setter
    private String pointBValue;
    @Getter
    @Setter
    private GeoPoint markerACoordinates;
    @Getter
    @Setter
    private GeoPoint markerBCoordinates;
    @Getter
    @Setter
    private IGeoPoint mapCenter;
    @Getter
    @Setter
    private boolean isNavContainerVisible;
    @Getter
    @Setter
    private List<GeoPoint> routePoints;
    @Getter
    @Setter
    private boolean isRouteInfoContainerVisible;
    @Getter
    @Setter
    private double distance;
    @Getter
    @Setter
    private double duration;

    private static HomeFragmentViewModel instance;

    private HomeFragmentViewModel() {
    }

    public static HomeFragmentViewModel getInstance() {
        if (instance == null) {
            instance = new HomeFragmentViewModel();
        }

        return instance;
    }

    public void destroy() {
        instance = null;
    }
}
