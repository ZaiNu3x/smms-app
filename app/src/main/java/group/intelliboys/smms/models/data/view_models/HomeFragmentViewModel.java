package group.intelliboys.smms.models.data.view_models;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

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

    private static HomeFragmentViewModel instance;

    private HomeFragmentViewModel() {
    }

    public static HomeFragmentViewModel getInstance() {
        if (instance == null) {
            instance = new HomeFragmentViewModel();
        }

        return instance;
    }
}
