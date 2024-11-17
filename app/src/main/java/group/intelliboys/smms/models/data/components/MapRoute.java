package group.intelliboys.smms.models.data.components;

import androidx.annotation.NonNull;

import org.osmdroid.views.overlay.Polyline;

import lombok.Data;

@Data
public class MapRoute implements Cloneable {
    private Polyline routePolyline;

    public MapRoute(Polyline routePolyline) {
        this.routePolyline = routePolyline;
    }

    @NonNull
    @Override
    public MapRoute clone() {
        try {
            return (MapRoute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
